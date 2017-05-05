package com.example.npc.myweather2.broadcast;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.npc.myweather2.service.NotifyWeatherService;
import com.example.npc.myweather2.service.UpdateWeatherService;

import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences.getBoolean("autoUpdate", true)) {
            //启动自动更新天气
            intent = new Intent(context, UpdateWeatherService.class);
            context.startService(intent);
        }
        long time=preferences.getLong("notifyTime", 0);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        //定时通知天气
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        Intent i = new Intent( context, NotifyWeatherService.class);
        PendingIntent p = PendingIntent.getService( context, 0, i, 0);
        alarmManager.cancel(p);
        if(preferences.getBoolean("Notify",false)){

            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, p);
        }
    }
}
