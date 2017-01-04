package com.Lbins.GuirenApp.module;

import java.io.Serializable;

/**
 * Created by zhl on 2016/12/24.
 */
public class HelpDanwei implements Serializable{
    private String help_danwei_id;
    private String help_danwei_name;
    private String top_number;

    public String getHelp_danwei_id() {
        return help_danwei_id;
    }

    public void setHelp_danwei_id(String help_danwei_id) {
        this.help_danwei_id = help_danwei_id;
    }

    public String getHelp_danwei_name() {
        return help_danwei_name;
    }

    public void setHelp_danwei_name(String help_danwei_name) {
        this.help_danwei_name = help_danwei_name;
    }

    public String getTop_number() {
        return top_number;
    }

    public void setTop_number(String top_number) {
        this.top_number = top_number;
    }
}
