package com.example.npc.myweather2.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;

public class NotifyService extends Service {
    public NotifyService() {
    }

    @Override
    public void onCreate() {


        Log.d("TAG", "Notify onCreate: ");
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        setAlarm();
        return super.onStartCommand(intent,flags,startId);
    }
    public void setAlarm(){
        //定时通知天气
        AlarmManager alarmManager=(AlarmManager)getSystemService(ALARM_SERVICE);
        Calendar calendar=Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        //现在的时间
        Date date2=calendar.getTime();
        Intent i=new Intent(this,NotifyWeatherService.class);
        PendingIntent p=PendingIntent.getService(NotifyService.this,0,i,0);
        //设定的时间
        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(this);
        Date date1=new Date( preferences.getLong("notifyTime",0));
        calendar.setTime(date1);
        //alarmManager.cancel(p);
       // if(date1.after(date2))
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY,p);
    }
}
