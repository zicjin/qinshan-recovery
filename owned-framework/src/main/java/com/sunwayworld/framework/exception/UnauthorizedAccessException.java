package com.sunwayworld.framework.exception;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class UnauthorizedAccessException extends RuntimeException {
    private static final long serialVersionUID = 1299665283142034891L;
    public UnauthorizedAccessException() { }
    public UnauthorizedAccessException(String message) { super(message); }
}
