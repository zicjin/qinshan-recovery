package com.sunwayworld.framework.exception;
public class TypeMismatchException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public TypeMismatchException(Class<?> expected, Class<?> actual) { super("Expected type [" + expected.getName() + "], but actual type [" + actual.getName() + "]."); }
    public TypeMismatchException(String expected, Class<?> actual) { super("Expected type [" + expected + "], but actual type [" + actual.getName() + "]."); }
}
