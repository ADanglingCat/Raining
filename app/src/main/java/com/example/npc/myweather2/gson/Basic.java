package com.example.npc.myweather2.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by npc on 3-13 0013.
 */

public class Basic {
    @SerializedName("city")
    public String cityName;
    @SerializedName("id")
    public String weatherId;
    public Update update;
    public class Update{
        @SerializedName("loc")
        public String updateTime;
    }
}
