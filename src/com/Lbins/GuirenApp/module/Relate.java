package com.Lbins.GuirenApp.module;

import java.io.Serializable;

/**
 * author: ${zhanghailong}
 * Date: 2015/2/8
 * Time: 13:52
 * 类的功能、说明写在此处.
 */
public class Relate implements Serializable {
    private String id;
    private String typeId;
    private String recordId;
    private String goodsId;
    private String empId;
    private String empTwoId;
    private String cont;
    private String dateline;
    private String orderId;

    private String empName;
    private String empCover;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public String getEmpCover() {
        return empCover;
    }

    public void setEmpCover(String empCover) {
        this.empCover = empCover;
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

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public String getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
    }

    public String getEmpId() {
        return empId;
    }

    public void setEmpId(String empId) {
        this.empId = empId;
    }

    public String getEmpTwoId() {
        return empTwoId;
    }

    public void setEmpTwoId(String empTwoId) {
        this.empTwoId = empTwoId;
    }

    public String getCont() {
        return cont;
    }

    public void setCont(String cont) {
        this.cont = cont;
    }

    public String getDateline() {
        return dateline;
    }

    public void setDateline(String dateline) {
        this.dateline = dateline;
    }
}

