package com.mahaventures.wibe.Tools;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.mahaventures.wibe.Activities.ConfirmEmailActivity;
import com.mahaventures.wibe.Activities.SignUpActivity;
import com.mahaventures.wibe.Models.User;
import com.mahaventures.wibe.Models.UserRole;
import com.mahaventures.wibe.Services.GetDataService;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StaticTools {
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

    public static void CheckEmailVerification(Context context, String key) {
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<User> call = service.GetUserInfo("Bearer " + key);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    List<UserRole> roles = response.body().getRoles();
                    boolean b = roles.stream().anyMatch(r -> r.getName().equals("EMAIL_CONFIRMED"));
                    if (!b) {
                        SendVerificationEmail(context, key);
                    }
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
    }

    private static void SendVerificationEmail(Context context, String key) {
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call call = service.SendVerificationEmail("Bearer " + key);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()) {
                    StaticTools.ShowToast(context, "Verification Email sent.", 1);
                    Intent intent = new Intent(context, ConfirmEmailActivity.class);
                    intent.putExtra("key", key);
                    context.startActivity(intent);
                } else {
                    StaticTools.LogErrorMessage("We're fucked up");
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {

            }
        });
    }
}
