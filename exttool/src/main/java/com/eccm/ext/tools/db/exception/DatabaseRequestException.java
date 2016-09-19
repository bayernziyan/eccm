package com.eccm.ext.tools.db.exception;

public class DatabaseRequestException extends RuntimeException{
	 	private static final long serialVersionUID = 1L;

	    public DatabaseRequestException() {
	        super();
	    }

	    public DatabaseRequestException(String message, Throwable cause) {
	        super(message, cause);
	    }

	    public DatabaseRequestException(String message) {
	        super(message);
	    }

	    public DatabaseRequestException(Throwable cause) {
	        super(cause);
	    }
	    
	    
}
