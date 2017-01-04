package com.Lbins.GuirenApp.module;

import java.io.Serializable;

/**
 * Created by zhl on 2016/12/20.
 */
public class HelpObj implements Serializable {
    private String help_id;
    private String mm_emp_id;
    private String help_title;
    private String help_content;
    private String help_pic;
    private String help_type;//0求助  1服务
    private String lat;
    private String lng;
    private String address;
    private String help_money;
    private String help_danwei_id;
    private String help_type_id;
    private String is_use;
    private String is_del;

    private String mm_emp_nickname;
    private String mm_emp_cover;
    private String help_danwei_name;
    private String help_type_name;
    private String dateline;
    private String hxusername;

    private String provinceID;
    private String cityID;
    private String areaID;

    private String province;
    private String city;
    private String area;

    public String getHxusername() {
        return hxusername;
    }

    public void setHxusername(String hxusername) {
        this.hxusername = hxusername;
    }

    public String getDateline() {
        return dateline;
    }

    public void setDateline(String dateline) {
        this.dateline = dateline;
    }

    public String getHelp_type_name() {
        return help_type_name;
    }

    public void setHelp_type_name(String help_type_name) {
        this.help_type_name = help_type_name;
    }

    public String getHelp_danwei_name() {
        return help_danwei_name;
    }

    public void setHelp_danwei_name(String help_danwei_name) {
        this.help_danwei_name = help_danwei_name;
    }

    public String getMm_emp_nickname() {
        return mm_emp_nickname;
    }

    public void setMm_emp_nickname(String mm_emp_nickname) {
        this.mm_emp_nickname = mm_emp_nickname;
    }

    public String getMm_emp_cover() {
        return mm_emp_cover;
    }

    public void setMm_emp_cover(String mm_emp_cover) {
        this.mm_emp_cover = mm_emp_cover;
    }

    public String getMm_emp_id() {
        return mm_emp_id;
    }

    public void setMm_emp_id(String mm_emp_id) {
        this.mm_emp_id = mm_emp_id;
    }

    public String getHelp_id() {
        return help_id;
    }

    public void setHelp_id(String help_id) {
        this.help_id = help_id;
    }

    public String getHelp_title() {
        return help_title;
    }

    public void setHelp_title(String help_title) {
        this.help_title = help_title;
    }

    public String getHelp_content() {
        return help_content;
    }

    public void setHelp_content(String help_content) {
        this.help_content = help_content;
    }

    public String getHelp_pic() {
        return help_pic;
    }

    public void setHelp_pic(String help_pic) {
        this.help_pic = help_pic;
    }

    public String getHelp_type() {
        return help_type;
    }

    public void setHelp_type(String help_type) {
        this.help_type = help_type;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getHelp_money() {
        return help_money;
    }

    public void setHelp_money(String help_money) {
        this.help_money = help_money;
    }

    public String getHelp_danwei_id() {
        return help_danwei_id;
    }

    public void setHelp_danwei_id(String help_danwei_id) {
        this.help_danwei_id = help_danwei_id;
    }

    public String getHelp_type_id() {
        return help_type_id;
    }

    public void setHelp_type_id(String help_type_id) {
        this.help_type_id = help_type_id;
    }

    public String getIs_use() {
        return is_use;
    }

    public void setIs_use(String is_use) {
        this.is_use = is_use;
    }

    public String getIs_del() {
        return is_del;
    }

    public void setIs_del(String is_del) {
        this.is_del = is_del;
    }



    public String getProvinceID() {
        return provinceID;
    }

    public void setProvinceID(String provinceID) {
        this.provinceID = provinceID;
    }

    public String getCityID() {
        return cityID;
    }

    public void setCityID(String cityID) {
        this.cityID = cityID;
    }

    public String getAreaID() {
        return areaID;
    }

    public void setAreaID(String areaID) {
        this.areaID = areaID;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }
}
