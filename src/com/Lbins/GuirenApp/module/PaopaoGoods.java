package com.Lbins.GuirenApp.module;

import java.io.Serializable;

/**
 * Created by liuzh on 2015/8/16.
 * 童心堂商城宝贝表
 */
public class PaopaoGoods implements Serializable {
    private String id;
    private String typeId;
    private String name;
    private String cover;
    private String cont;
    private String sellPrice;//销售价格
    private String marketPrice;//市场价格
    private String address;
    private String person;//联系人
    private String tel;
    private String qq;
    private String empId;
    private String schoolId;
    private String isUse;
    private String isDel;
    private String upTime;//上架时间
    private String count;//商品数量

    private String nickName;
    private String empCover;
    private String mobile;
    private String schoolName;
    private String goodsPosition;//是否置顶

    private String is_zhiying;
    private String manager_id;
    private String is_youhuo;

    public String getIs_zhiying() {
        return is_zhiying;
    }

    public void setIs_zhiying(String is_zhiying) {
        this.is_zhiying = is_zhiying;
    }

    public String getManager_id() {
        return manager_id;
    }

    public void setManager_id(String manager_id) {
        this.manager_id = manager_id;
    }

    public String getIs_youhuo() {
        return is_youhuo;
    }

    public void setIs_youhuo(String is_youhuo) {
        this.is_youhuo = is_youhuo;
    }

    public String getGoodsPosition() {
        return goodsPosition;
    }

    public void setGoodsPosition(String goodsPosition) {
        this.goodsPosition = goodsPosition;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getEmpCover() {
        return empCover;
    }

    public void setEmpCover(String empCover) {
        this.empCover = empCover;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getCont() {
        return cont;
    }

    public void setCont(String cont) {
        this.cont = cont;
    }

    public String getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(String sellPrice) {
        this.sellPrice = sellPrice;
    }

    public String getMarketPrice() {
        return marketPrice;
    }

    public void setMarketPrice(String marketPrice) {
        this.marketPrice = marketPrice;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getEmpId() {
        return empId;
    }

    public void setEmpId(String empId) {
        this.empId = empId;
    }

    public String getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }

    public String getIsUse() {
        return isUse;
    }

    public void setIsUse(String isUse) {
        this.isUse = isUse;
    }

    public String getIsDel() {
        return isDel;
    }

    public void setIsDel(String isDel) {
        this.isDel = isDel;
    }

    public String getUpTime() {
        return upTime;
    }

    public void setUpTime(String upTime) {
        this.upTime = upTime;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }
}
