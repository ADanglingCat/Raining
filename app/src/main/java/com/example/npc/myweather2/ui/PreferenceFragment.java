package com.example.npc.myweather2.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;

import com.example.npc.myweather2.R;

public class PreferenceFragment extends android.preference.PreferenceFragment {
    SharedPreferences preferences;
    SharedPreferences.OnSharedPreferenceChangeListener listener;
    SharedPreferences.Editor editor;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        editor=preferences.edit();
    }

    @Override
    public void onResume() {
        super.onResume();
        //initSave();
        initAutoBing();

        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                switch(key){
                    case "autoBing":
                        initAutoBing();
                        break;
//                    case "save":
//                        initSave();
//                        break;
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

    public void initSave() {
        Preference preference1 = findPreference("autoBing");
        Preference preference2 = findPreference("selfPic");
        if (preferences.getBoolean("save", false)) {
            editor.putBoolean("autoBing",false);
            editor.apply();
            preference1.setEnabled(false);
            preference2.setEnabled(true);
        } else {
            preference1.setEnabled(true);
        }
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
