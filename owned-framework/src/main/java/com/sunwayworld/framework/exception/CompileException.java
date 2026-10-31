package com.sunwayworld.framework.exception;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class CompileException extends RuntimeException {
    private static final long serialVersionUID = 3199336072993488982L;
    public CompileException(String message) { super(message); }
    public CompileException(String message, Throwable throwable) { super(message, throwable); }
}
