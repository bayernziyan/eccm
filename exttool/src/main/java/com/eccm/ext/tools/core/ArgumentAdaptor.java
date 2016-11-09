package com.eccm.ext.tools.core;

public abstract interface ArgumentAdaptor<T extends Argument> {
	public abstract <T extends Argument> T adapt(T t);
}
