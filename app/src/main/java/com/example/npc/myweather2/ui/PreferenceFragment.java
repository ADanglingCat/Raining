package com.example.npc.myweather2.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;

import com.example.npc.myweather2.R;

public class PreferenceFragment extends android.preference.PreferenceFragment {
    SharedPreferences preferences;
    SharedPreferences.OnSharedPreferenceChangeListener listener;
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
                    default:
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
}
