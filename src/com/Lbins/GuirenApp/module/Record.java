package com.Lbins.GuirenApp.module;

import java.io.Serializable;

/**
 * Created by zhl on 2016/5/11.
 * mm_msg_id	varchar	32	信息ID	Y	32位UUID
 mm_emp_id	varchar	32	会员ID	Y	32位
 mm_msg_type	Char	1	信息类型	Y	0文字 1图片
 mm_msg_content	varchar	1000	信息内容	Y	500字以内
 mm_msg_picurl	varchar	500	图片地址	N
 dateline	varchar	50	时间戳	Y
 is_del	char	1	是否显示	Y	0默认显示
 1不显示
 is_top	char	1	是否置顶	Y	默认0否 1是
 top_num	Varchar	255	置顶数字	Y	默认0
 越大越靠前
 */
public class Record implements Serializable{
    private String mm_msg_id;
    private String mm_emp_id;
    private String mm_msg_type;
    private String mm_msg_content;
    private String mm_msg_picurl;
    private String dateline;
    private String is_del;
    private String is_top;
    private String top_num;

    private String mm_emp_mobile;
    private String mm_emp_nickname;
    private String mm_emp_cover;
    private String deviceType;
    private String mm_emp_sex;
    private String mm_emp_birthday;
    private String mm_emp_up_emp;
    private String mm_hangye_id;
    private String zanNum;//赞数量
    private String plNum;//评论数量
    private String hangye;//所属行业
    private String mm_emp_motto;//sign

    public String getMm_emp_motto() {
        return mm_emp_motto;
    }

    public void setMm_emp_motto(String mm_emp_motto) {
        this.mm_emp_motto = mm_emp_motto;
    }

    public String getHangye() {
        return hangye;
    }

    public void setHangye(String hangye) {
        this.hangye = hangye;
    }

    public String getZanNum() {
        return zanNum;
    }

    public void setZanNum(String zanNum) {
        this.zanNum = zanNum;
    }

    public String getPlNum() {
        return plNum;
    }

    public void setPlNum(String plNum) {
        this.plNum = plNum;
    }

    public String getMm_emp_mobile() {
        return mm_emp_mobile;
    }

    public void setMm_emp_mobile(String mm_emp_mobile) {
        this.mm_emp_mobile = mm_emp_mobile;
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

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getMm_emp_sex() {
        return mm_emp_sex;
    }

    public void setMm_emp_sex(String mm_emp_sex) {
        this.mm_emp_sex = mm_emp_sex;
    }

    public String getMm_emp_birthday() {
        return mm_emp_birthday;
    }

    public void setMm_emp_birthday(String mm_emp_birthday) {
        this.mm_emp_birthday = mm_emp_birthday;
    }

    public String getMm_emp_up_emp() {
        return mm_emp_up_emp;
    }

    public void setMm_emp_up_emp(String mm_emp_up_emp) {
        this.mm_emp_up_emp = mm_emp_up_emp;
    }

    public String getMm_hangye_id() {
        return mm_hangye_id;
    }

    public void setMm_hangye_id(String mm_hangye_id) {
        this.mm_hangye_id = mm_hangye_id;
    }

    public String getMm_msg_id() {
        return mm_msg_id;
    }

    public void setMm_msg_id(String mm_msg_id) {
        this.mm_msg_id = mm_msg_id;
    }

    public String getMm_emp_id() {
        return mm_emp_id;
    }

    public void setMm_emp_id(String mm_emp_id) {
        this.mm_emp_id = mm_emp_id;
    }

    public String getMm_msg_type() {
        return mm_msg_type;
    }

    public void setMm_msg_type(String mm_msg_type) {
        this.mm_msg_type = mm_msg_type;
    }

    public String getMm_msg_content() {
        return mm_msg_content;
    }

    public void setMm_msg_content(String mm_msg_content) {
        this.mm_msg_content = mm_msg_content;
    }

    public String getMm_msg_picurl() {
        return mm_msg_picurl;
    }

    public void setMm_msg_picurl(String mm_msg_picurl) {
        this.mm_msg_picurl = mm_msg_picurl;
    }

    public String getDateline() {
        return dateline;
    }

    public void setDateline(String dateline) {
        this.dateline = dateline;
    }

    public String getIs_del() {
        return is_del;
    }

    public void setIs_del(String is_del) {
        this.is_del = is_del;
    }

    public String getIs_top() {
        return is_top;
    }

    public void setIs_top(String is_top) {
        this.is_top = is_top;
    }

    public String getTop_num() {
        return top_num;
    }

    public void setTop_num(String top_num) {
        this.top_num = top_num;
    }
}
