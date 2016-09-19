package com.eccm.ext.tools;

import static com.eccm.ext.tools.constant.EmptyObjectConstant.EMPTY_STRING;
public class StringUtil {
	public static boolean isIntChar(Character c){
		int  code = (int)(c);
		if( code > 47 && code < 58)return true;
		return false;
	}
	
	public static boolean isBlank( String originalStr ) {
		if ( null == originalStr ) {
			return true;
		}		
		return trimToEmpty( originalStr ).isEmpty();
	}
	
	public static String trimToEmpty( String originalStr ) {
		if ( null == originalStr || originalStr.isEmpty() )
			return EMPTY_STRING;		
		return originalStr.trim();
	}
}
