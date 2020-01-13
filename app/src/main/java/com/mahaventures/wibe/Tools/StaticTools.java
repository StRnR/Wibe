package com.mahaventures.wibe.Tools;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StaticTools {
    public static void ShowToast(Context context, String message, int length) {
        Toast toast = Toast.makeText(context, message, length);
        toast.show();
    }

    public static int CalculatePasswordStrength(String password) {
        int iPasswordScore = 0;
        if (password.length() < 8)
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
}
