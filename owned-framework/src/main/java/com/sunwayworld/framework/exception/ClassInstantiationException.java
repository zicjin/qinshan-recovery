package com.sunwayworld.framework.exception;

/** Construction failure preserving the source utility's exception boundary. */
public class ClassInstantiationException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public ClassInstantiationException(Class<?> type, Throwable cause) {
        super("Unable create new instance of a class [" + type.getName() + "]", cause);
    }
}
