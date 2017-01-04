package com.pinyin;



import com.Lbins.GuirenApp.module.CityObj;

import java.util.Comparator;

public class PinyinComparator implements Comparator {

	@Override
	public int compare(Object o1, Object o2) {
		 CityObj o11 = (CityObj) o1;
		CityObj o22 = (CityObj) o2;
		 String str1 = PingYinUtil.getPingYin(o11.getCity());
	     String str2 = PingYinUtil.getPingYin(o22.getCity());
	     return str1.compareTo(str2);
	}

}
