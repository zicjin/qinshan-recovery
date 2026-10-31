package com.sunwayworld.framework.exception;
import org.springframework.http.HttpStatus; import org.springframework.web.bind.annotation.ResponseStatus;
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) public class FastDfsException extends RuntimeException {
 private static final long serialVersionUID=-7854792968237674098L; public FastDfsException(Throwable cause){super(cause);} public FastDfsException(String msg){super(msg);} public FastDfsException(String msg,Throwable cause){super(msg,cause);}
}
