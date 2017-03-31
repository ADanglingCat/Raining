package com.example.npc.myweather2.util;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

/**
 * Created by npc on 3-29 0029.
 */

public class PagerAdapter extends FragmentStatePagerAdapter {
    List<Fragment> fragmentList;
    public PagerAdapter(FragmentManager fm,List<Fragment> fragmentList) {
        super(fm);
        this.fragmentList=fragmentList;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment;
        fragment=fragmentList.get(position);
        return fragment;
    }

    @Override
    public int getCount() {
        int count=fragmentList.size();
        return count;
    }
}