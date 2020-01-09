package com.mahaventures.wibe.Services;

import android.content.Context;
import android.widget.Toast;

public class StaticTools {
    public static void ShowToast(Context context, String message, int length) {
        Toast toast = new Toast(context);
        toast.setText(message);
        toast.setDuration(length);
        toast.show();
    }
}
