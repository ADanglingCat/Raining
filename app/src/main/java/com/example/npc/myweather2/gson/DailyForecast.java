package com.example.npc.myweather2.gson;

/**
 * Created by npc on 3-13 0013.
 */

public class DailyForecast {
    public Astro astro;//天文指数
    public Cond cond;
    public Tmp tmp;
    //public Wind wind;
    public String date;//日期
    public String pop;//降水概率
    public class Astro{
        public String sr;//日出时间
        public String ss;//日落时间
    }
    public class Cond{
        public String txt_d;//白天天气
        //public String txt_n;//夜晚天气
    }
    public class Tmp{
        public String max;//最高温度
        public String min;//最低温度
    }
//    public class Wind{
//        public String dir;//风向
//        public String sc;//风力等级
//    }
}
