package com.sunwayworld.framework.beans.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class BeanException extends RuntimeException {
    private static final long serialVersionUID = -6193426921421285936L;
    public BeanException(String message) { super(message); }
}
