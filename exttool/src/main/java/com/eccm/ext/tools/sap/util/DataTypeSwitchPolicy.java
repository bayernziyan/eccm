package com.eccm.ext.tools.sap.util;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataTypeSwitchPolicy {
	Class sourceType();
	Class returnType();
	boolean format() default false;
}
