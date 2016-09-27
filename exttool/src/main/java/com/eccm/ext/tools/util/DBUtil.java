package com.eccm.ext.tools.util;

import java.util.List;

public class DBUtil {
	public static String columnStr(String prefix,String suffix,List<String> list,String sept){
		if(null == list || list.isEmpty())return "";
		StringBuilder sb = new StringBuilder();
		for(String c : list){
			if(sb.length()>0) sb.append(sept);
			sb.append(prefix.replace("[this]", c)).append(c).append(suffix.replace("[this]", c));
			
		}
		return sb.toString();
	}
}
