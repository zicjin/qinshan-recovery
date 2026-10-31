package com.sunwayworld.framework.exception;
import org.springframework.http.HttpStatus; import org.springframework.web.bind.annotation.ResponseStatus;
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) public class ThreadException extends RuntimeException {
 private static final long serialVersionUID=3820187417600099920L; public ThreadException(String msg,Throwable cause){super(msg,cause);} public ThreadException(Throwable cause){super(cause);}
}
