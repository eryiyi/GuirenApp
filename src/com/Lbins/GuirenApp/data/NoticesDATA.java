package com.Lbins.GuirenApp.data;


import com.Lbins.GuirenApp.module.Notice;

import java.util.List;

/**
 * Created by zhanghl on 2015/1/17.
 */
public class NoticesDATA extends Data {
    private List<Notice> data;

    public List<Notice> getData() {
        return data;
    }

    public void setData(List<Notice> data) {
        this.data = data;
    }
}
