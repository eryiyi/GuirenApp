package com.Lbins.GuirenApp.widget;

import com.Lbins.GuirenApp.module.EmpRelateObj;

import java.util.Comparator;

public class PinyinComparator implements Comparator {

	@Override
	public int compare(Object o1, Object o2) {
		EmpRelateObj empRelateObj1 = (EmpRelateObj) o1;
		EmpRelateObj empRelateObj2 = (EmpRelateObj) o2;
		 String str1 = PingYinUtil.getPingYin(empRelateObj1.getMm_emp_nickname());
	     String str2 = PingYinUtil.getPingYin(empRelateObj2.getMm_emp_nickname());
	     return str1.compareTo(str2);
	}

}
