package com.eccm.ext.tools.core;

import java.io.Serializable;
import java.util.Collection;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class ArgumentFields<A extends Argument> implements Serializable {

	private static final long serialVersionUID = 7744044531052818471L;

	Argument[] fields;

	public ArgumentFields() {
		this.fields = new Argument[]{} ;
	}

	ArgumentFields(Collection<? extends Argument> fields) {
		this.fields = fields.toArray(new Argument[fields.size()]);
	}

	public final int size() {
		return fields.length;
	}

	public final Argument argument(String name) {
		if (name == null) {
			return null;
		}

		for (Argument f : fields) {
			if (f.getCode().equals(name)) {
				return f;
			}
		}

		return null;
	}

	public final Argument argument(int index) {
		if (index >= 0 && index < fields.length) {
			return fields[index];
		}
		return null;
	}
	
	public final   ArgumentFields<? extends Argument> arguments(int index){
		if(index >= fields.length){
			Argument[] result = new Argument[index + 1];
			System.arraycopy(fields, 0, result, 0, fields.length);
			this.fields = result;
		}
		return this;
	}
	
	public final Argument[] arguments(String... f) {
		Argument[] result = new Argument[f.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = argument(f[i]);
        }
        return result;
    }
	public final Argument[] arguments(int start , int end) {
		Argument[] result = new Argument[end-start+1];
		System.arraycopy(fields, start, result, 0, end-start+1);
        return result;
    }
	public final void add(Argument f) {
		Argument[] result = new Argument[fields.length + 1];

        System.arraycopy(fields, 0, result, 0, fields.length);
        result[fields.length] = f;

        fields = result;
    }
	
	public final void add(Argument f, int indx) {
		arguments(indx);
		fields[indx] = f;        
    }
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
