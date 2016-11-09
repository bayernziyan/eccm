package com.eccm.ext.tools.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class DoubleUtil {
	/**
	 * 加法
	 * @param v1
	 * @param v2
	 * @return
	 */
	public static double add(double v1, double v2){
		BigDecimal _v1 = new BigDecimal(v1);
		BigDecimal _v2 = new BigDecimal(v2);
		return _v1.add(_v2).doubleValue();
	}
	/**
	 * 减法
	 * @param v1
	 * @param v2
	 * @return
	 */
	public static double minus(double v1, double v2){
		BigDecimal _v1 = new BigDecimal(v1);
		BigDecimal _v2 = new BigDecimal(v2);
		return _v1.subtract(_v2).doubleValue();
	}
	/**
	 * 乘法
	 * @param v1
	 * @param v2
	 * @return
	 */
	public static double multiply(double v1, double v2){
		BigDecimal _v1 = new BigDecimal(v1);
		BigDecimal _v2 = new BigDecimal(v2);
		return _v1.multiply(_v2).doubleValue();
	}
	
	/**
	 * 除法 四舍五入
	 * @param v1
	 * @param v2
	 * @return
	 */
	public static double divide(double v1, double v2){
		if(v2 == 0) return 0;
		BigDecimal _v1 = new BigDecimal(v1);
		BigDecimal _v2 = new BigDecimal(v2);
		return _v1.divide(_v2, RoundingMode.HALF_UP).doubleValue();
	}
	/**
	 *  除法 四舍五入 带精度值
	 * @param v1
	 * @param v2
	 * @param scale
	 * @return
	 */
	public static double divide(double v1, double v2,int scale){
		if(v2 == 0) return 0;
		BigDecimal _v1 = new BigDecimal(v1);
		BigDecimal _v2 = new BigDecimal(v2);
		return _v1.divide(_v2, scale,RoundingMode.HALF_UP).doubleValue();
	}
	public static void main(String[] args) {
		System.out.println(divide(61, 7,2));
	}
}
