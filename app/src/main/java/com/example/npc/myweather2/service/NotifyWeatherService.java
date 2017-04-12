package com.example.npc.myweather2.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import com.example.npc.myweather2.R;
import com.example.npc.myweather2.gson.DailyForecast;
import com.example.npc.myweather2.gson.Weather;
import com.example.npc.myweather2.model.CountyList;
import com.example.npc.myweather2.ui.MainActivity;
import com.example.npc.myweather2.util.MyUtil;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class NotifyWeatherService extends Service {
    private List<CountyList> lists;
    private Weather weather;
    private static final String TAG = "TAGNotifyWeatherService";
    public NotifyWeatherService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        lists = DataSupport.findAll(CountyList.class);
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String countyName;
        String weatherInfo;
        DailyForecast dailyForecast;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(NotifyWeatherService.this);
        if (lists != null) {
            for (CountyList list : lists) {
                if (list.isMainCity()) {
                    //只更新默认城市天气
                    for (int i = 0; i < 3; i++) {
                        updateWeather(list.getWeatherId());
                        if (weather != null) {
                            countyName = list.getCountyName();
                            dailyForecast = weather.dailyForecasts.get(0);
                            weatherInfo = dailyForecast.cond.txt_d + "  " + dailyForecast.tmp.max + "℃~" + dailyForecast.tmp.min + "℃ 空气质量:" + weather.aqi.city.qlty;
                            Intent intent1 = new Intent(this, MainActivity.class);
                            PendingIntent pi = PendingIntent.getActivity(this, 0, intent1, 0);
                            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                            Notification notification = new NotificationCompat.Builder(NotifyWeatherService.this)
                                    .setContentTitle(countyName + " 今日天气")
                                    .setContentText(weatherInfo)
                                    .setWhen(System.currentTimeMillis())
                                    .setAutoCancel(true)
                                    .setContentIntent(pi)
                                    .setSmallIcon(R.drawable.ic_notify)
                                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_notify))
                                    .build();

                            Calendar calendar = Calendar.getInstance();
                            //现在的时间
                            int currentHour= calendar.get(Calendar.HOUR_OF_DAY);
                            int currentMinute= calendar.get(Calendar.MINUTE);
                           long time=preferences.getLong("notifyTime", 0);
                            calendar.setTimeInMillis(time);
                            int hour= calendar.get(Calendar.HOUR_OF_DAY);
                            int minute= calendar.get(Calendar.MINUTE);
                            if (currentHour==hour&&currentMinute>=minute) {
                                notificationManager.notify(1, notification);
                            }
                            break;

                        }

                    }


                }
            }

        }
        return super.onStartCommand(intent, flags, startId);
    }

    //更新天气
    public void updateWeather(final String weatherId) {
        String weatherAddress = getResources().getString(R.string.weatherAddress);
        String weatherKey = getResources().getString(R.string.weatherKey);
        String weatherUrl = weatherAddress + weatherId + "&" + weatherKey;
        MyUtil.sendRequest(weatherUrl, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                weather = MyUtil.handleWeatherResponse(responseText);
                if (weather != null && "ok".equals(weather.status)) {
                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(NotifyWeatherService.this).edit();
                    editor.putString("weather" + weatherId, responseText);
                    editor.apply();
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
        });
    }
}
