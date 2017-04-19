package com.example.npc.myweather2.model;

import org.litepal.crud.DataSupport;

/**
 * Created by npc on 4-18 0018.
 */

public class MyDanmaku extends DataSupport {
    private int id;
    private String content;
    private _User user;
    private boolean color;
    public MyDanmaku(){}

    public MyDanmaku(DanMu danMu){
        this.content=danMu.getContent();
        this.user=danMu.getUser();
        this.color=danMu.getColor();
    }

    public boolean isColor() {
        return color;
    }

    public void setColor(boolean color) {
        this.color = color;
    }

    public _User getUser() {
        return user;
    }

    public void setUser(_User user) {
        this.user = user;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
