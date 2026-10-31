package com.sunwayworld.framework.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class RoundingRuleException extends RuntimeException {
    private static final long serialVersionUID = -6762796249760976132L;
    public RoundingRuleException(Throwable cause) { super(cause); }
    public RoundingRuleException(String msg) { super(msg); }
}
