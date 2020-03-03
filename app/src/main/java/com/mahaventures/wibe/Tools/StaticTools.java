package com.mahaventures.wibe.Tools;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import androidx.palette.graphics.Palette;

import com.mahaventures.wibe.Activities.ConfirmEmailActivity;
import com.mahaventures.wibe.Models.User;
import com.mahaventures.wibe.Models.UserRole;
import com.mahaventures.wibe.Services.GetDataService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StaticTools {
    private static boolean cvb;
    private final static int MinimumPasswordLength = 6;

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
}
