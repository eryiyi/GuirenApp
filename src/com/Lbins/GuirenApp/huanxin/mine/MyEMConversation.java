package com.Lbins.GuirenApp.huanxin.mine;


import com.Lbins.GuirenApp.module.Emp;
import com.hyphenate.chat.EMConversation;

/**
 * Created by Administrator on 2015/3/5.
 * 继承贵人提供的EMConversation，增加emp属性，以及getter and setter 方法
 */
public class MyEMConversation {


    private Emp emp;

    private EMConversation emConversation;

    public EMConversation getEmConversation() {
        return emConversation;
    }

    public void setEmConversation(EMConversation emConversation) {
        this.emConversation = emConversation;
    }

    public Emp getEmp() {
        return emp;
    }

    public void setEmp(Emp emp) {
        this.emp = emp;
    }
}
