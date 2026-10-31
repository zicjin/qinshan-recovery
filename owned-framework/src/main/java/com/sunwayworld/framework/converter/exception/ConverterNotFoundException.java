package com.sunwayworld.framework.converter.exception;

public class ConverterNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public ConverterNotFoundException(Class<?> source, Class<?> target) {
        super("No converter found capable of converting from type [" + source.getName() + "] to type [" + target.getName() + "]");
    }
}
