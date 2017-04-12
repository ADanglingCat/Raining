package com.example.npc.myweather2.ui;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TimePicker;

import com.example.npc.myweather2.R;
import com.example.npc.myweather2.service.NotifyWeatherService;

import java.util.Calendar;
import java.util.Date;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by npc on 3-20 0020.
 */

public class MyTimePreference extends DialogPreference {
    private long currentTime;
    private TimePicker timePicker;

    public MyTimePreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setDialogLayoutResource(R.layout.timepicker_dialog);
        setPositiveButtonText(android.R.string.ok);
        setNegativeButtonText(android.R.string.cancel);
        setDialogIcon(null);
        setDialogTitle(null);
    }

    public void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            Calendar calendar = Calendar.getInstance();
            //Date date1=calendar.getTime();//现在的时间
            calendar.set(Calendar.HOUR_OF_DAY,timePicker.getCurrentHour());
            calendar.set(Calendar.MINUTE,timePicker.getCurrentMinute());
            currentTime = (calendar.getTime()).getTime();
            SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
            editor.putLong("notifyTime",currentTime);
            editor.apply();
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            if (preferences.getBoolean("Notify", false)) {
                //定时通知天气
                AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(ALARM_SERVICE);
                Intent i = new Intent(getContext(), NotifyWeatherService.class);
                PendingIntent p = PendingIntent.getService(getContext(), 0, i, 0);
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, p);
            }

        }
    }

    //获取sharepreference中的配置参数
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        if (restorePersistedValue) {
            // Restore existing state
            currentTime = this.getPersistedLong(0);
        } else {
            // Set default state from the XML attribute
            currentTime = Long.parseLong(defaultValue.toString());// defaultValue;
        }
        setDefaultValue(currentTime);
    }

    //默认值
    protected Object onGetDefaultValue(TypedArray a, int index) {
        currentTime = Long.parseLong(a.getString(index));
        return currentTime;
    }

    //弹出对话框时初始化
    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        timePicker = (TimePicker) view.findViewById(R.id.timePicker);
        if (timePicker != null) {
            timePicker.setIs24HourView(true);
            Date date = new Date(currentTime);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            timePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
            timePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));
        }
    }
}
