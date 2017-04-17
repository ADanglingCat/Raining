package com.example.npc.myweather2.model;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by npc on 4-12 0012.
 */

public class _User extends BmobUser{
    //private String username;//用户名,与邮箱相同
   // private String password;//密码
    private BmobFile userImage;//头像
    private String name;//昵称
    private String sign;//签名
    private String sex;//性别

    public BmobFile getUserImage() {
        return userImage;
    }

    public _User setUserImage(BmobFile userImage) {
        this.userImage = userImage;
        return this;
    }

    public String getName() {
        return name;
    }

    public _User setName(String name) {
        this.name = name;
        return this;
    }

    public String getSign() {
        return sign;
    }

    public _User setSign(String sign) {
        this.sign = sign;
        return this;
    }

    public String getSex() {
        return sex;
    }

    public _User setSex(String sex) {
        this.sex = sex;
        return this;
    }
}
