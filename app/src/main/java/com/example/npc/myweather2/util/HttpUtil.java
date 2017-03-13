package com.example.npc.myweather2.util;


import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by npc on 3-13 0013.
 */

public class HttpUtil {
    public static void sendRequest(String address,okhttp3.Callback callback){
        OkHttpClient client=new OkHttpClient();
        Request request=new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }
}
