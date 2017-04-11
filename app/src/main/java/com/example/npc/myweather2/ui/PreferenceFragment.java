package com.example.npc.myweather2.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;

import com.example.npc.myweather2.R;

public class PreferenceFragment extends android.preference.PreferenceFragment {
    SharedPreferences p;
    SharedPreferences.OnSharedPreferenceChangeListener listener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);


    }

    @Override
    public void onResume() {
        super.onResume();
        p = PreferenceManager.getDefaultSharedPreferences(getActivity());

        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if ("autoBing".equals(key)) {
                    Preference selfPic = findPreference("selfPic");
                    if (p.getBoolean("autoBing", false)) {
                        selfPic.setEnabled(false);
                    } else {
                        selfPic.setEnabled(true);
                    }

                }
            }
        };
        p.registerOnSharedPreferenceChangeListener(listener);


    }

    @Override
    public void onPause() {
        p.unregisterOnSharedPreferenceChangeListener(listener);
        super.onPause();
    }
}
