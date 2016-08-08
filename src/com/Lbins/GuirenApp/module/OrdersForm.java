package com.Lbins.GuirenApp.module;

import java.io.Serializable;
import java.util.List;

/**
 * author: ${zhanghailong}
 * Date: 2015/3/27
 * Time: 14:59
 * 类的功能、说明写在此处.
 */
public class OrdersForm implements Serializable {
    private List<Order> list;

    public List<Order> getList() {
        return list;
    }

    public void setList(List<Order> list) {
        this.list = list;
    }
}
