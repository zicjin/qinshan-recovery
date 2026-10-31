package com.sunwayworld.framework.exception;
public class NotImplementedException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public NotImplementedException() { super(); }
    public NotImplementedException(String message) { super(message); }
    public NotImplementedException(Throwable cause) { super(cause); }
}
