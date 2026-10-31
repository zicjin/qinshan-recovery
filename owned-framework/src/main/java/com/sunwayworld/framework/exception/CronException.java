package com.sunwayworld.framework.exception;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class CronException extends RuntimeException {
    private static final long serialVersionUID = 3199336072993488942L;
    public CronException(String message) { super(message); }
    public CronException(String message, Throwable throwable) { super(message, throwable); }
}
