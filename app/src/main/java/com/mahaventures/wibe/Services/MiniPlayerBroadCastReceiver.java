package com.mahaventures.wibe.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.mahaventures.wibe.Tools.StaticTools;

public class MiniPlayerBroadCastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        context.sendBroadcast(new Intent("MINI_PLAYER")
                .putExtra("action_name", intent.getAction()));
        StaticTools.LogErrorMessage("message received");
    }
}
