package com.example.npc.myweather2.model;

import org.litepal.crud.DataSupport;

/**
 * Created by npc on 3-23 0023.
 */

public class CountyList extends DataSupport {
    private int id;
    private int countyId;
    private boolean mainCity;
    private String weatherId;
    private String countyName;
    public CountyList(){}
    public CountyList(int countyId){
        this.countyId=countyId;
    }
    public CountyList(String weatherId,String countyName){
        this.weatherId=weatherId;
        this.countyName=countyName;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCountyId() {
        return countyId;
    }

    public void setCountyId(int countyId) {
        this.countyId = countyId;
    }

    public boolean isMainCity() {
        return mainCity;
    }

    public void setMainCity(boolean mainCity) {
        this.mainCity = mainCity;
    }

    public String getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }
}
