package com.Lbins.GuirenApp.module;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/2/18.
 */
public class CityObj implements Serializable {
    private String id;
    private String cityID;
    private String city;
    private String father;
    private String provinceName;
    private String topnum;

    public String getTopnum() {
        return topnum;
    }

    public void setTopnum(String topnum) {
        this.topnum = topnum;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCityID() {
        return cityID;
    }

    public void setCityID(String cityID) {
        this.cityID = cityID;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getFather() {
        return father;
    }

    public void setFather(String father) {
        this.father = father;
    }
}
