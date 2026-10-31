package com.sunwayworld.framework.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class ReflectionException extends RuntimeException {
    private static final long serialVersionUID = -3299356518561298244L;
    public ReflectionException(Throwable throwable) { super(throwable); }
}
