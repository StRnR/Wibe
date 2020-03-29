package com.mahaventures.wibe.Tools;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.palette.graphics.Palette;

import com.mahaventures.wibe.Activities.ConfirmEmailActivity;
import com.mahaventures.wibe.Activities.LoadActivity;
import com.mahaventures.wibe.Activities.PlayerActivity;
import com.mahaventures.wibe.Models.NewModels.Album;
import com.mahaventures.wibe.Models.NewModels.Artist;
import com.mahaventures.wibe.Models.NewModels.Collection;
import com.mahaventures.wibe.Models.NewModels.MyModels.BrowseItem;
import com.mahaventures.wibe.Models.NewModels.Playlist;
import com.mahaventures.wibe.Models.NewModels.Track;
import com.mahaventures.wibe.Models.User;
import com.mahaventures.wibe.Models.UserRole;
import com.mahaventures.wibe.Services.GetDataService;
import com.mahaventures.wibe.Services.PlaySongBroadcastReceiver;
import com.mahaventures.wibe.Services.PostDataService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StaticTools {
    private final static int MinimumPasswordLength = 6;
    private static boolean cvb;

    public static void ShowToast(Context context, String message, int length) {
        Toast toast = Toast.makeText(context, message, length);
        toast.show();
    }

    public static int CalculatePasswordStrength(String password) {
        int iPasswordScore = 0;
        if (password.length() < MinimumPasswordLength)
            return 0;
        else if (password.length() >= 10)
            iPasswordScore += 2;
        else
            iPasswordScore += 1;
        if (password.matches("(?=.*[0-9].*[0-9]).*"))
            iPasswordScore += 2;
        else if (password.matches("(?=.*[0-9]).*"))
            iPasswordScore += 1;
        if (password.matches("(?=.*[a-z]).*"))
            iPasswordScore += 2;
        if (password.matches("(?=.*[A-Z].*[A-Z]).*"))
            iPasswordScore += 2;
        else if (password.matches("(?=.*[A-Z]).*"))
            iPasswordScore += 1;
        if (password.matches("(?=.*[~!@#$%^&*()_-].*[~!@#$%^&*()_-]).*"))
            iPasswordScore += 2;
        else if (password.matches("(?=.*[~!@#$%^&*()_-]).*"))
            iPasswordScore += 1;
        return iPasswordScore;
    }

    public static void LogErrorMessage(String msg) {
        Log.wtf("****Error Message****", msg);
    }

    public static void LogTimedMessage(String msg) {
        Log.wtf(String.format("**%s**", new Date().toString()), msg);
    }

    public static boolean EmailValidation(String email) {
        Pattern pattern = Pattern.compile("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])");
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static void OnFailure(Context context) {
        ShowToast(context, "Something went wrong, try again", 0);
    }

    public static boolean CheckEmailVerification(String key) {
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<User> call = service.GetUserInfo("Bearer " + key);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    List<UserRole> roles = response.body().getRoles();
                    cvb = roles.stream().anyMatch(r -> r.getName().equals("EMAIL_CONFIRMED"));
                } else {
                    try {
                        String msg = response.errorBody().string();
                        StaticTools.LogErrorMessage(msg);
                    } catch (Exception e) {
                        Log.wtf("exception", e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                StaticTools.LogErrorMessage(t.getMessage());
            }
        });
        return cvb;
    }

    public static void SendVerificationEmail(Context context, String key, boolean b) {
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call call = service.SendVerificationEmail("Bearer " + key);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()) {
                    StaticTools.ShowToast(context, "Verification Email sent.", 1);
                    if (b) {
                        Intent intent = new Intent(context, ConfirmEmailActivity.class);
                        intent.putExtra("key", key);
                        context.startActivity(intent);
                    }
                } else {
                    StaticTools.LogErrorMessage("We're fucked up");
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
            }
        });
    }

    public static int getDominantColor(Bitmap bitmap) {
        List<Palette.Swatch> swatchesTemp = Palette.from(bitmap).generate().getSwatches();
        List<Palette.Swatch> swatches = new ArrayList<Palette.Swatch>(swatchesTemp);
        Collections.sort(swatches, new Comparator<Palette.Swatch>() {
            @Override
            public int compare(Palette.Swatch swatch1, Palette.Swatch swatch2) {
                return swatch2.getPopulation() - swatch1.getPopulation();
            }
        });
        return swatches.size() > 0 ? swatches.get(0).getRgb() : getRandomColor();
    }

    private static int getRandomColor() {
        Random rand = new Random();
        int r = rand.nextInt(255);
        int g = rand.nextInt(255);
        int b = rand.nextInt(255);
        return Color.rgb(r, g, b);
    }

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return model;
        } else {
            return String.format("%s %s", manufacturer, model);
        }
    }

    public static String getDeviceType(Context context) {
        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (Objects.requireNonNull(manager).getPhoneType() == TelephonyManager.PHONE_TYPE_NONE) {
            return "Tablet";
        } else {
            return "mobile";
        }
    }

    public static String getStore(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
            String s = packageManager.getInstallerPackageName(applicationInfo.packageName);
            return (s != null && !s.equals("")) ? s : "default";
        } catch (Exception e) {
            return "default";
        }
    }

    public static String getArtistsName(Track track) {
        String artists = "";
        if (track.artists == null)
            return artists;
        if (track.artists.data.size() == 1) {
            artists = track.artists.data.get(0).name;
        } else {
            List<String> strings = track.artists.data.stream().map(x -> x.name).collect(Collectors.toList());
            artists = TextUtils.join(", ", strings);
        }
        return artists;
    }

    public static String getSongDuration(int seconds) {
        String second = "";
        if (String.valueOf(seconds % 60).length() == 1) {
            second = String.format(Locale.getDefault(), "0%d", seconds % 60);
        } else {
            second = String.valueOf(seconds % 60);
        }
        return String.format(Locale.getDefault(), "%d:%s", seconds / 60, second);
    }

    public static String getToken() {
        return String.format("Bearer %s", LoadActivity.token);
    }

    public static String getHPI() {
        return LoadActivity.homePageId;
    }

    public static List<BrowseItem> getBrowseItems(Collection collection) {
        List<BrowseItem> list = new ArrayList<>();
        for (Track track : collection.tracks.data) {
            list.add(new BrowseItem(track.image.medium.url, track.name, BrowseItem.BrowseType.Track, track.id, track.backgroundColor));
        }
        for (Artist artist : collection.artists.data) {
            list.add(new BrowseItem(artist.image.medium.url, artist.name, BrowseItem.BrowseType.Artist, artist.id, artist.backgroundColor));
        }
        for (Album album : collection.albums.data) {
            list.add(new BrowseItem(album.image.medium.url, album.name, BrowseItem.BrowseType.Album, album.id, album.backgroundColor));
        }
        for (Playlist playlist : collection.playlists.data) {
            list.add(new BrowseItem(playlist.image.medium.url, playlist.name, BrowseItem.BrowseType.Playlist, playlist.id, playlist.backgroundColor));
        }
        Collections.shuffle(list);
        return list;
    }

    public static void addToMySong(Context context, String id) {
        PostDataService service = RetrofitClientInstance.getRetrofitInstance().create(PostDataService.class);
        Call call = service.AddToMySongs(StaticTools.getToken(), id);
        call.enqueue(new retrofit2.Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful())
                    StaticTools.ShowToast(context, "added", 0);
            }

            @Override
            public void onFailure(Call call, Throwable t) {

            }
        });
    }

    public static void PlayTrack(Context context, String artist, Track track) {
        PlayerActivity.mArtistString = artist;
        PlayerActivity.mTrackNameString = track.name;
        Intent intent = new Intent(context, PlayerActivity.class);
        PlayerActivity.trackNumber = 0;
        if (PlayerActivity.queue != null) {
            PlayerActivity.queue.clear();
            PlayerActivity.queue.add(track);
        } else {
            PlayerActivity.queue = new ArrayList<>();
            PlayerActivity.queue.add(track);
        }

        Intent bcIntent = new Intent(context, PlaySongBroadcastReceiver.class)
                .setAction("pay");
        context.sendBroadcast(bcIntent);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                context.startActivity(intent);
            }
        }, 100);
    }

    public static void PlayQueue(Context context, String artist, List<Track> tracks) {
        Intent intent = new Intent(context, PlayerActivity.class);
        PlayerActivity.trackNumber = 0;
        if (PlayerActivity.queue != null) {
            PlayerActivity.queue.clear();
            PlayerActivity.queue.addAll(tracks);
        } else {
            PlayerActivity.queue = new ArrayList<>();
            PlayerActivity.queue.addAll(tracks);
        }

        Intent bcIntent = new Intent(context, PlaySongBroadcastReceiver.class)
                .setAction("pay");
        context.sendBroadcast(bcIntent);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                context.startActivity(intent);
            }
        }, 100);
    }

    public static void PlayTrackInQueue(Context context, String artist, Track track) {
        PlayerActivity.mArtistString = artist;
        PlayerActivity.mTrackNameString = track.name;
        Intent intent = new Intent(context, PlayerActivity.class);
        PlayerActivity.trackNumber = 0;
        Collections.rotate(PlayerActivity.queue, PlayerActivity.queue.size() - PlayerActivity.queue.indexOf(PlayerActivity.queue.stream().filter(t -> t.id.equals(track.id)).findFirst().get()));
//        rotate(PlayerActivity.queue, PlayerActivity.queue.indexOf(PlayerActivity.queue.stream().filter(t -> t.id.equals(track.id)).findFirst().get()));
//        PlayerActivity.queue.removeIf(track1 -> track1.id.equals(track.id));
//        PlayerActivity.queue.add(0, track);
        Intent bcIntent = new Intent(context, PlaySongBroadcastReceiver.class)
                .setAction("pay");
        context.sendBroadcast(bcIntent);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                context.startActivity(intent);
            }
        }, 100);
    }

    public static void ServerError(Context context, String message) {
//        Intent intent = new Intent(context, OnServerFailureActivity.class);
//        context.startActivity(intent);
        ShowToast(context, message, 0);
        LogErrorMessage(message);
    }

    static <T> List<T> rotate(List<T> aL, int shift) {
        if (aL.size() == 0)
            return aL;
        for (int i = shift; i < aL.size() - 1; i++) {
            T t = aL.get(i);
            aL.remove(i);
            aL.add(0, t);
        }
        return aL;
    }
}