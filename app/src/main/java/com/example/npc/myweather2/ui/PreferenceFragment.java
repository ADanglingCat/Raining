package com.example.npc.myweather2.ui;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;

import com.example.npc.myweather2.R;
import com.example.npc.myweather2.service.NotifyWeatherService;

import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;

public class PreferenceFragment extends android.preference.PreferenceFragment {
    private SharedPreferences preferences;
    private SharedPreferences.OnSharedPreferenceChangeListener listener;
    private static final String TAG = "TAGPreferenceFragment";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        initAutoBing();

        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

                switch(key){
                    case "autoBing":
                       initAutoBing();
                        break;
                    case "Notify":
                        setAlarm();
                        break;
                    default:
                        break;
                }
            }
        };
        preferences.registerOnSharedPreferenceChangeListener(listener);


    }

    @Override
    public void onPause() {
        preferences.unregisterOnSharedPreferenceChangeListener(listener);
        super.onPause();
    }

    public void initAutoBing(){
        Preference preference2 = findPreference("selfPic");
        if (preferences.getBoolean("autoBing", false)) {
            preference2.setEnabled(false);
        } else {
            preference2.setEnabled(true);
        }
    }
    public void setAlarm(){
        long time=preferences.getLong("notifyTime", 0);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        //定时通知天气
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
        Intent i = new Intent( getActivity(), NotifyWeatherService.class);
        PendingIntent p = PendingIntent.getService( getActivity(), 0, i, 0);
        alarmManager.cancel(p);
        if(preferences.getBoolean("Notify",false)){

            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, p);
        }
    }
}
