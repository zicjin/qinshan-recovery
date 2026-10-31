package com.sunwayworld.framework.exception;
import org.springframework.http.HttpStatus; import org.springframework.web.bind.annotation.ResponseStatus;
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) public class FtpException extends RuntimeException {
 private static final long serialVersionUID=-7854792968237674098L; public FtpException(Throwable cause){super(cause);} public FtpException(String msg){super(msg);} public FtpException(String msg,Throwable cause){super(msg,cause);}
}
