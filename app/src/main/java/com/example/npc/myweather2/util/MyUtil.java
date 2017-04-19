package com.example.npc.myweather2.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.npc.myweather2.gson.Weather;
import com.example.npc.myweather2.model.City;
import com.example.npc.myweather2.model.County;
import com.example.npc.myweather2.model.DanMu;
import com.example.npc.myweather2.model.MyDanmaku;
import com.example.npc.myweather2.model.Province;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by npc on 3-14 0014.
 */

public class MyUtil {
    private static Toast toast;
    private static final String TAG = "tagMyUtil";
    public static void showToast(Context context,String message){
        if(toast==null){
            toast=Toast.makeText(context,message,Toast.LENGTH_SHORT);
        }else{
            toast.setText(message);
        }
        if(!"".equals(message))
            toast.show();
    }
    public static void showToast(String message){
        if(toast==null){
            toast=Toast.makeText(MyApplication.getContext(),message,Toast.LENGTH_SHORT);
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
    public static boolean handleProvinceResponse(String response){
        if(!TextUtils.isEmpty(response)){
            try{
                JSONArray provinces=new JSONArray(response);
                for(int i=0;i<provinces.length();i++){
                    JSONObject provinceObject=provinces.getJSONObject(i);
                    Province province=new Province();
                    province.setProvinceName(provinceObject.getString("name"));
                    province.setProvinceCode(provinceObject.getInt("id"));
                    province.save();
                }
                return true;
            }catch (JSONException e){
                e.printStackTrace();
            }

        }
        return false;
    }
    public static boolean handleCityResponse(String response,int provinceId){
        if(!TextUtils.isEmpty(response)){
            try{
                JSONArray cityList=new JSONArray(response);
                for(int i=0;i<cityList.length();i++){
                    JSONObject cityObject=cityList.getJSONObject(i);
                    City city=new City();
                    city.setCityName(cityObject.getString("name"));
                    city.setCityCode(cityObject.getInt("id"));
                    city.setProvinceId(provinceId);
                    city.save();
                }
                return true;
            }catch(JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    public static boolean handleCountyResponse(String response,int cityId){
        if(!TextUtils.isEmpty(response)){
            try{
                JSONArray countyList=new JSONArray(response);
                for(int i=0;i<countyList.length();i++){
                    JSONObject countyObject=countyList.getJSONObject(i);
                    County county=new County();
                    county.setCountyName(countyObject.getString("name"));
                    county.setWeatherId(countyObject.getString("weather_id"));
                    county.setCityId(cityId);
                    county.save();
                }
                return true;
            }catch(JSONException e){
                e.printStackTrace();
            }
        }
        return false;
    }
    public static Weather handleWeatherResponse(String response){
        try{
            JSONObject jsonObject=new JSONObject(response);
            JSONArray jsonArray=jsonObject.getJSONArray("HeWeather5");
            String weatherContent=jsonArray.getJSONObject(0).toString();
            return new Gson().fromJson(weatherContent,Weather.class);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public static boolean handleCountyResponse(String response){
        if(!TextUtils.isEmpty(response)){
            try{
                JSONArray provinces=new JSONArray(response);
                for(int i=0;i<provinces.length();i++){
                    JSONObject provinceObject=provinces.getJSONObject(i);
                    Province province=new Province();
                    province.setProvinceName(provinceObject.getString("name"));
                    province.setProvinceCode(provinceObject.getInt("id"));
                    province.save();
                }
                return true;
            }catch (JSONException e){
                e.printStackTrace();
            }

        }
        return false;
    }
    public static String myMD5(String mess) {
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(md5.digest(mess.getBytes()));
            String result = "";
            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                result += temp;
            }
            return result;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getMD5(String mess) {
        if (TextUtils.isEmpty(mess)) {
            return null;
        } else {
            String result = myMD5(mess);
            for (int i = 0; i < 1; i++) {
                result = myMD5(result);
            }
            return result;
        }
    }
    //获取连接网络类型
    public boolean getNetworkType(){
        ConnectivityManager manager=(ConnectivityManager)MyApplication.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info=manager.getActiveNetworkInfo();
        if(info!=null){
            if(info.getType()==ConnectivityManager.TYPE_WIFI){
                return true;
            }
        }
        return false;
    }
    public static void getDanMaku() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        calendar.set(Calendar.HOUR_OF_DAY, 1);
        Date date = calendar.getTime();
        try {
            date = sdf.parse(sdf.format(date));
        } catch (ParseException pe) {
            pe.printStackTrace();
        }

        BmobQuery<DanMu> query = new BmobQuery<>();
        query.addWhereGreaterThanOrEqualTo("createdAt", new BmobDate(date))
                .order("createAt")
                .addQueryKeys("user,content,isMine,color");
        query.findObjects(new FindListener<DanMu>() {
            @Override
            public void done(List<DanMu> list, BmobException e) {
                if (e == null) {
                    DataSupport.deleteAll(MyDanmaku.class);
                    if (list.size() > 0) {
                        for (DanMu danMu : list) {
                            MyDanmaku myDanmaku = new MyDanmaku(danMu);
                            myDanmaku.save();
                        }
                    }
                } else {
                    Log.d(TAG, "done: " + e.getMessage());
                }

            }
        });
    }
}
