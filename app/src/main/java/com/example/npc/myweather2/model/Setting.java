package com.example.npc.myweather2.model;

import cn.bmob.v3.BmobObject;

/**
 * Created by npc on 4-14 0014.
 */

public class Setting extends BmobObject {
    private _User user;
    private Boolean notify;
    private Long notifyTime;
    private Boolean autoUpdate;
    private Boolean updateMode;
    private String updateFre;//更新频率
    private Boolean nightUpdate;
    private Boolean diy;
    private Boolean autoBing;
    private String alpha;
    private Boolean save;

    public _User getUser() {
        return user;
    }

    public Setting setUser(_User user) {
        this.user = user;
        return this;
    }

    public Boolean getNotify() {
        return notify;
    }

    public Setting setNotify(Boolean notify) {
        this.notify = notify;
        return this;
    }

    public Long getNotifyTime() {
        return notifyTime;
    }

    public Setting setNotifyTime(Long notifyTime) {
        this.notifyTime = notifyTime;
        return this;
    }

    public Boolean getAutoUpdate() {
        return autoUpdate;
    }

    public Setting setAutoUpdate(Boolean autoUpdate) {
        this.autoUpdate = autoUpdate;
        return this;
    }

    public Boolean getUpdateMode() {
        return updateMode;
    }

    public Setting setUpdateMode(Boolean updateMode) {
        this.updateMode = updateMode;
        return this;
    }



    public Boolean getNightUpdate() {
        return nightUpdate;
    }

    public Setting setNightUpdate(Boolean nightUpdate) {
        this.nightUpdate = nightUpdate;
        return this;
    }

    public Boolean getDiy() {
        return diy;
    }

    public Setting setDiy(Boolean diy) {
        this.diy = diy;
        return this;
    }

    public Boolean getAutoBing() {
        return autoBing;
    }

    public Setting setAutoBing(Boolean autoBing) {
        this.autoBing = autoBing;
        return this;
    }



    public Boolean getSave() {
        return save;
    }

    public Setting setSave(Boolean save) {
        this.save = save;
        return this;
    }

    public String getUpdateFre() {
        return updateFre;
    }

    public Setting setUpdateFre(String updateFre) {
        this.updateFre = updateFre;
        return this;
    }

    public String getAlpha() {
        return alpha;
    }

    public Setting setAlpha(String alpha) {
        this.alpha = alpha;
        return this;

    }
}
