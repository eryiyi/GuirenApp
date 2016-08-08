package com.Lbins.GuirenApp.data;

import com.Lbins.GuirenApp.module.Emp;

import java.util.List;

/**
 * Created by zhl on 2016/5/10.
 */
public class EmpsRmData extends Data {
    private List<Emp>[] data;

    public List<Emp>[] getData() {
        return data;
    }

    public void setData(List<Emp>[] data) {
        this.data = data;
    }
}
