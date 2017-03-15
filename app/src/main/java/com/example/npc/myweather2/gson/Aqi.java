package com.example.npc.myweather2.gson;

/**
 * Created by npc on 3-13 0013.
 */

public class Aqi {
    public City city;
    class City{
        public String aqi;
        public String co;
        public String no2;
        public String o3;
        public String pm10;
        public String pm25;
        public String qlty;//空气质量评价
        public String so2;
    }
}
