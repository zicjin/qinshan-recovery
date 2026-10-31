package com.sunwayworld.framework.exception;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class ConcurrentException extends RuntimeException {
    private static final long serialVersionUID = 1987819443176955637L;
    public ConcurrentException() { }
    public ConcurrentException(String message) { super(message); }
}
