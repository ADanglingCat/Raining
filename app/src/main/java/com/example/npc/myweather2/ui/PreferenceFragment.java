package com.example.npc.myweather2.ui;

import android.os.Bundle;

import com.example.npc.myweather2.R;

public class PreferenceFragment extends android.preference.PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }


}
