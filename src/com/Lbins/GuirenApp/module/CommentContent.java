package com.Lbins.GuirenApp.module;

/**
 * author: ${zhanghailong}
 * Date: 2015/1/31
 * Time: 15:46
 * 类的功能、说明写在此处.
 */
public class CommentContent {
    private String id;
    private String recordId;
    private String fplid;
    private String empId;
    private String content;
    private String dateline;
    private String nickName;//评论人昵称
    private String cover;//评论人头像
    private String fNickName;//父评论者昵称

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

    public String getFplid() {
        return fplid;
    }

    public void setFplid(String fplid) {
        this.fplid = fplid;
    }

    public String getEmpId() {
        return empId;
    }

    public void setEmpId(String empId) {
        this.empId = empId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public String getfNickName() {
        return fNickName;
    }

    public void setfNickName(String fNickName) {
        this.fNickName = fNickName;
    }
}
