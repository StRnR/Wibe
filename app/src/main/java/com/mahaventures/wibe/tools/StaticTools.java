package com.mahaventures.wibe.tools;

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

import com.mahaventures.wibe.activities.MySongsActivity;
import com.mahaventures.wibe.activities.PlayerActivity;
import com.mahaventures.wibe.models.Album;
import com.mahaventures.wibe.models.Artist;
import com.mahaventures.wibe.models.BrowseItem;
import com.mahaventures.wibe.models.Collection;
import com.mahaventures.wibe.models.InitModel;
import com.mahaventures.wibe.models.MySong;
import com.mahaventures.wibe.models.Playlist;
import com.mahaventures.wibe.models.Track;
import com.mahaventures.wibe.services.DeleteDataService;
import com.mahaventures.wibe.services.GetDataService;
import com.mahaventures.wibe.services.PlaySongBroadcastReceiver;
import com.mahaventures.wibe.services.PostDataService;

import java.util.ArrayList;
import java.util.Collections;
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
    public static String token;
    public static String homePageId;
    private static String name;

    public static final String AlbumActivityTag = "album";
    public static final String ArtistActivityTag = "artist";
    public static final String BrowseActivityTag = "browse";
    public static final String ChangePasswordActivityTag = "change";
    public static final String ConfirmEmailActivityTag = "email";
    public static final String ForgotPassActivityTag = "forgot";
    public static final String LoadingActivityTag = "loading";
    public static final String MainActivityTag = "main";
    public static final String MyProfileActivityTag = "profile";
    public static final String MySongsActivityTag = "songs";
    public static final String PlayerActivityTag = "player";
    public static final String PlaylistActivityTag = "playlist";
    public static final String ResetPassActivityTag = "reset";
    public static final String SearchActivityTag = "search";
    public static final String SignInActivityTag = "login";
    public static final String SignUpActivityTag = "register";

    public static String getName() {
        return name;
    }

    public static void setName(String name) {
        StaticTools.name = name;
    }

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
        return !matcher.matches();
    }

    public static int getDominantColor(Bitmap bitmap) {
        List<Palette.Swatch> swatchesTemp = Palette.from(bitmap).generate().getSwatches();
        List<Palette.Swatch> swatches = new ArrayList<>(swatchesTemp);
        Collections.sort(swatches, (swatch1, swatch2) -> swatch2.getPopulation() - swatch1.getPopulation());
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
        String second;
        if (String.valueOf(seconds % 60).length() == 1) {
            second = String.format(Locale.getDefault(), "0%d", seconds % 60);
        } else {
            second = String.valueOf(seconds % 60);
        }
        return String.format(Locale.getDefault(), "%d:%s", seconds / 60, second);
    }

    public static String getToken() {
        return String.format("Bearer %s", token);
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
        Call call = service.AddToMySongs(getToken(), id);
        call.enqueue(new retrofit2.Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful())
                    StaticTools.ShowToast(context, "Added", 0);
            }

            @Override
            public void onFailure(Call call, Throwable t) {

            }
        });
    }

    public static void deleteFromMySong(Context context, String id) {
        DeleteDataService service = RetrofitClientInstance.getRetrofitInstance().create(DeleteDataService.class);
        Call call = service.DeleteFromMySong(getToken(), id);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful())
                    StaticTools.ShowToast(context, "Deleted", 0);
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

    public static void PlayQueue(Context context, List<Track> tracks) {
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
        ShowToast(context, "Please try again later...", 0);
        LogErrorMessage(message);
    }

    public static void getHPI() {
        PostDataService service = RetrofitClientInstance.getRetrofitInstance().create(PostDataService.class);
        Call<InitModel> call = service.Init(getToken());
        call.enqueue(new Callback<InitModel>() {
            @Override
            public void onResponse(Call<InitModel> call, Response<InitModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    homePageId = response.body().homePageId;
                }
            }

            @Override
            public void onFailure(Call<InitModel> call, Throwable t) {
            }
        });
    }

    public static boolean IsAdded(String trackId) {
        if (MySongsActivity.mySongTracks != null) {
            return MySongsActivity.mySongTracks.stream().anyMatch(track -> track.id.equals(trackId));
        } else {
            GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
            String url = "https://api.musicify.ir/profile/tracks?include=track.album,track.artists";
            Call<MySong> call = service.GetMySongs(getToken(), url);
            call.enqueue(new Callback<MySong>() {
                @Override
                public void onResponse(Call<MySong> call, Response<MySong> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        MySongsActivity.mySongTracks = response.body().data.stream().map(x -> x.track).collect(Collectors.toList());
                    }
                }

                @Override
                public void onFailure(Call<MySong> call, Throwable t) {
                }
            });
        }
        return false;
    }
}