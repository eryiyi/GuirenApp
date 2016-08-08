package com.Lbins.GuirenApp.module;

import java.io.Serializable;

/**
 * Created by zhl on 2016/5/18.
 */
public class HangYeType implements Serializable{
    private String mm_hangye_id;
    private String mm_hangye_name;
    private String mm_hangye_fid;
    private String top_num;
    private String fName;

    public String getMm_hangye_id() {
        return mm_hangye_id;
    }

    public void setMm_hangye_id(String mm_hangye_id) {
        this.mm_hangye_id = mm_hangye_id;
    }

    public String getMm_hangye_name() {
        return mm_hangye_name;
    }

    public void setMm_hangye_name(String mm_hangye_name) {
        this.mm_hangye_name = mm_hangye_name;
    }

    public String getMm_hangye_fid() {
        return mm_hangye_fid;
    }

    public void setMm_hangye_fid(String mm_hangye_fid) {
        this.mm_hangye_fid = mm_hangye_fid;
    }

    public String getTop_num() {
        return top_num;
    }

    public void setTop_num(String top_num) {
        this.top_num = top_num;
    }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }
}
