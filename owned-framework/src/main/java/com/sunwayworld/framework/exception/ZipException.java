package com.sunwayworld.framework.exception;

public class ZipException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public ZipException(String message) { super(message); }
    public ZipException(Throwable cause) { super(cause); }
}
