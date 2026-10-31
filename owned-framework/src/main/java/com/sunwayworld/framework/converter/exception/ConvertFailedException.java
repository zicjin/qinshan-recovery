package com.sunwayworld.framework.converter.exception;

public class ConvertFailedException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public ConvertFailedException(Class<?> source, Class<?> target, Throwable cause) {
        super("Converting from type [" + source.getName() + "] to type [" + target.getName() + "]", cause);
    }
    public ConvertFailedException(Class<?> source, Class<?> target, Object value) {
        super("Converting from type [" + source.getName() + "] value [" + value.toString() + "] to type [" + target.getName() + "]");
    }
}
