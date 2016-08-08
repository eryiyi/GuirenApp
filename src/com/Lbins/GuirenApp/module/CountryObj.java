package com.Lbins.GuirenApp.module;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/2/16.
 */
public class CountryObj implements Serializable{
    private String id;
    private String areaID;
    private String area;
    private String father;
    private String cityName;
    private String topnum;

    public String getTopnum() {
        return topnum;
    }

    public void setTopnum(String topnum) {
        this.topnum = topnum;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAreaID() {
        return areaID;
    }

    public void setAreaID(String areaID) {
        this.areaID = areaID;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getFather() {
        return father;
    }

    public void setFather(String father) {
        this.father = father;
    }
}
