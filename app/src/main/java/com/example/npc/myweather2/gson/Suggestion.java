package com.example.npc.myweather2.gson;

/**
 * Created by npc on 3-13 0013.
 */

public class Suggestion {
    public class Air{
        public String brf;
        public String txt;
    }
    public class Comf{
        public String brf;
        public String txt;//舒适度
    }
    public class Cw{
        public String brf;
        public   String txt;//洗车
    }
    public class Drsg{
        public String brf;
        public   String txt;//穿衣
    }
    public class Flu{
        public String brf;
        public  String txt;//流感
    }
    public class Sport{
        public String brf;
        public  String txt;//运动
    }
    public class Trav{
        public String brf;
        public   String txt;//旅行
    }
    public class Uv{
        public String brf;
        public String txt;//紫外线
    }
    public Comf comf;
    public Cw cw;
    public Drsg drsg;
    public Flu flu;
    public Sport sport;
    public Trav trav;
    public Uv uv;
    public Air air;
}
