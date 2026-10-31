package com.sunwayworld.framework.exception;

public class CloneException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public CloneException(Object value, Throwable cause) {
        super("Unable clone object [" + value + "].", cause);
    }
}
