package com.example.npc.myweather2.util;

import android.text.TextUtils;

import com.example.npc.myweather2.db.City;
import com.example.npc.myweather2.db.County;
import com.example.npc.myweather2.db.Province;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by npc on 3-13 0013.
 */

public class HandleResponse {
    //解析省级数据
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
            /*Gson gson=new Gson();
            List<Province> provinceList=gson.fromJson(response, new TypeToken<List<Province>>(){}.getType());
            for(Province province:provinceList){
                province.save();
            }
        return true;*/
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
}
