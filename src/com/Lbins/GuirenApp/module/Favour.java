package com.Lbins.GuirenApp.module;

import java.io.Serializable;

/**
 * author: ${zhanghailong}
 * Date: 2015/2/6
 * Time: 8:26
 * 类的功能、说明写在此处.
 */
public class Favour implements Serializable {
    private String id;
    private String recordId;
    private String empId;
    private String dateline;
    private String nickName;
    private String cover;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public String getEmpId() {
        return empId;
    }

    public void setEmpId(String empId) {
        this.empId = empId;
    }

    public String getDateline() {
        return dateline;
    }

    public void setDateline(String dateline) {
        this.dateline = dateline;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }
}
