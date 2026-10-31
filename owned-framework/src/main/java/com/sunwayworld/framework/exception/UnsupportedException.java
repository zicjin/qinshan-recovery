package com.sunwayworld.framework.exception;

public class UnsupportedException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public UnsupportedException() { super(); }
    public UnsupportedException(String message) { super(message); }
    public UnsupportedException(Throwable cause) { super(cause); }
}
