package com.example.npc.myweather2.db;

import org.litepal.crud.DataSupport;

/**
 * Created by npc on 3-13 0013.
 */

public class Province extends DataSupport{
    private int provinceId;
    private String provinceName;
    private int provinceCode;

    public int getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }

    public int getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(int provinceCode) {
        this.provinceCode = provinceCode;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }
}
