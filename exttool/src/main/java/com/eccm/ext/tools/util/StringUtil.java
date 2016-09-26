package com.eccm.ext.tools.util;

import static com.eccm.ext.tools.constant.EmptyObjectConstant.EMPTY_STRING;

import com.eccm.ext.tools.constant.MD5N;
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
	
	public static String isBlank(String originalStr,String defaultStr){
		if(isBlank(originalStr))
			return defaultStr;
		return originalStr;
	}
	
	/**
	 * 获取循环冗余校验码 CRC32 ，32位速度快
	 * @param value
	 * @return {@link Long}
	 */
	public static long CRC32(String value){
		/*java.util.zip.CRC32 crc = new java.util.zip.CRC32();
		crc.update(value.getBytes());*/
		return com.eccm.ext.tools.constant.CRC32.getCrc32(value.getBytes());
	}
	/**
	 * 获取md5校验码 ，128位 碰撞概率低
	 * @param value
	 * @return {@link String}
	 */
	public static String MD5(String value){
		return new MD5N().getMD5ofStr(value);
	}
}
