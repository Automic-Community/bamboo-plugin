package com.automic.cda.ws.rest.exceptions;

/**
 * @author yogitadalal
 *
 */
public class BambooCdaRuntimeException extends RuntimeException {

    private static final long serialVersionUID = -2727414165585891758L;

    private final int code;

    public BambooCdaRuntimeException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BambooCdaRuntimeException(int code, String message, Exception e) {
        super(message, e);
        this.code = code;
    }

    public int getExceptionCode() {
        return code;
    }

}
