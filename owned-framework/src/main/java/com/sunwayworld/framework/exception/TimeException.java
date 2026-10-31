package com.sunwayworld.framework.exception;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class TimeException extends RuntimeException {
    private static final long serialVersionUID = 6698706736425765959L;
    public TimeException(String msg) { super(msg); }
}
