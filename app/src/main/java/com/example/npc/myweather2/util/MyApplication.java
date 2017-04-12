package com.example.npc.myweather2.util;

import android.app.Application;
import android.content.Context;

import org.litepal.LitePalApplication;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobConfig;

/**
 * Created by npc on 3-21 0021.
 */

public class MyApplication extends Application {
    private static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        context=getApplicationContext();
        LitePalApplication.initialize(context);

        BmobConfig config=new BmobConfig.Builder(context)
                .setApplicationId("66177263610d2753e0ff170c90f95b57")
                .setConnectTimeout(30)
                .setUploadBlockSize(1024*1024)
                .setFileExpiration(2500)
                .build();
        Bmob.initialize(config);
    }
    public static Context getContext(){
        return context;
    }
}
