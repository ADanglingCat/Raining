package com.example.npc.myweather2.util;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;

import org.litepal.LitePalApplication;

/**
 * Created by npc on 3-21 0021.
 */

public class MyApplication extends Application {
    private static Context context;
    private Bitmap myBitmap;

    public Bitmap getMyBitmap() {
        return myBitmap;
    }

    public void setMyBitmap(Bitmap myBitmap) {
        this.myBitmap = myBitmap;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        context=getApplicationContext();
        LitePalApplication.initialize(context);
    }
    public static Context getContext(){
        return context;
    }
}
