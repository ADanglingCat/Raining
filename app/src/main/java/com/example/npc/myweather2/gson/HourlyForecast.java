package com.example.npc.myweather2.gson;

/**
 * Created by npc on 3-14 0014.
 */

public class HourlyForecast {
    public Cond cond;
   // public Wind wind;
    public String date;//时间
    //public String pop;//降水概率
    public String tmp;//温度
    class Cond{
        public String txt;//天气情况
    }
//    class Wind{
//        public String dir;//风向
//        public String sc;//风力等级
//    }
}
