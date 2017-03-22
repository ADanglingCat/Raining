package com.example.npc.myweather2.gson;

/**
 * Created by npc on 3-13 0013.
 */

public class Now {
    public class Cond{
        public String txt;
    }
    public class Wind{
        public String dir;//风向
        public String sc;//风力等级
    }
    public Cond cond;
    public Wind wind;
    public String fl;//体感温度
    public String tmp;//温度

}
