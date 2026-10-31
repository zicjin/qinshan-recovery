package com.sunwayworld.framework.exception;
import org.springframework.http.HttpStatus; import org.springframework.web.bind.annotation.ResponseStatus;
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) public class ResolverArgumentException extends RuntimeException {
 private static final long serialVersionUID=-5911502087917338234L; public ResolverArgumentException(String message){super(message);} public ResolverArgumentException(String message,Throwable cause){super(message,cause);} public ResolverArgumentException(Throwable cause){super(cause);}
}
