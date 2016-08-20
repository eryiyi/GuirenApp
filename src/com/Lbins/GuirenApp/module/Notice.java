package com.Lbins.GuirenApp.module;

import java.io.Serializable;

/**
 * author: ${zhanghailong}
 * Date: 2015/2/6
 * Time: 21:39
 * 类的功能、说明写在此处.
 */
public class Notice implements Serializable {
    private String mm_notice_id;
    private String mm_manager_id;
    private String mm_notice_title;
    private String mm_notice_content;
    private String dateline;
    private String is_del;
    private String mm_emp_id;//给某个用户单独推送的时候用

    public String getMm_notice_id() {
        return mm_notice_id;
    }

    public void setMm_notice_id(String mm_notice_id) {
        this.mm_notice_id = mm_notice_id;
    }

    public String getMm_manager_id() {
        return mm_manager_id;
    }

    public void setMm_manager_id(String mm_manager_id) {
        this.mm_manager_id = mm_manager_id;
    }

    public String getMm_notice_title() {
        return mm_notice_title;
    }

    public void setMm_notice_title(String mm_notice_title) {
        this.mm_notice_title = mm_notice_title;
    }

    public String getMm_notice_content() {
        return mm_notice_content;
    }

    public void setMm_notice_content(String mm_notice_content) {
        this.mm_notice_content = mm_notice_content;
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

    public String getMm_emp_id() {
        return mm_emp_id;
    }

    public void setMm_emp_id(String mm_emp_id) {
        this.mm_emp_id = mm_emp_id;
    }
}
