package com.example.npc.myweather2.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.npc.myweather2.service.UpdateWeatherService;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences.getBoolean("autoUpdate", true)) {
            //启动自动更新
            intent = new Intent(context, UpdateWeatherService.class);
            context.startService(intent);
        }
    }
}
