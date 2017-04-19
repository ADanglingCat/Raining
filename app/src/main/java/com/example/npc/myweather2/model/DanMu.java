package com.example.npc.myweather2.model;

import cn.bmob.v3.BmobObject;

/**
 * Created by npc on 4-18 0018.
 */

public class DanMu extends BmobObject{
    private _User user;
    private String content;
    private Boolean color;
    public DanMu(){}

    public DanMu(String content,_User user,Boolean color){
        this.content=content;
        this.user=user;
        this.color=color;
    }

    public Boolean getColor() {
        return color;
    }

    public void setColor(Boolean color) {
        this.color = color;
    }

    public _User getUser() {
        return user;
    }

    public void setUser(_User user) {
        this.user = user;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }



}
