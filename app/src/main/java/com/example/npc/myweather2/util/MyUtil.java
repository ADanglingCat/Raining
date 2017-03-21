package com.example.npc.myweather2.util;

import android.content.Context;
import android.widget.Toast;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by npc on 3-14 0014.
 */

public class MyUtil {
    private static Toast toast;
    public static void showToast(Context context,String message){
        if(toast==null){
            toast=Toast.makeText(context,message,Toast.LENGTH_SHORT);
        }else{
            toast.setText(message);
        }
        if(!"".equals(message))
            toast.show();
    }
    public static void sendRequest(String address,okhttp3.Callback callback){
        OkHttpClient client=new OkHttpClient();
        Request request=new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }
}
