package com.automic.cda.ws.rest.exceptions;

public class BambooCdaException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = -289186436423694791L;

    public BambooCdaException(String message) {
	super(message);
    }
    
    public BambooCdaException(Throwable e) {
    	super(e);
    }
}
