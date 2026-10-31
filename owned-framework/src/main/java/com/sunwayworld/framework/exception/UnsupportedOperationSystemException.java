package com.sunwayworld.framework.exception;
import org.springframework.http.HttpStatus; import org.springframework.web.bind.annotation.ResponseStatus;
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) public class UnsupportedOperationSystemException extends RuntimeException {
 private static final long serialVersionUID=-5973822105312013500L; public UnsupportedOperationSystemException(String msg){super(msg);} public UnsupportedOperationSystemException(Throwable t){super(t);}
}
