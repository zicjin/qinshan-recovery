package com.sunwayworld.framework.exception.database;
import org.springframework.http.HttpStatus; import org.springframework.web.bind.annotation.ResponseStatus;
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) public class JdbcException extends RuntimeException {
 private static final long serialVersionUID=-1649163943493927291L; public JdbcException(Exception ex){super(ex);} public JdbcException(String message){super(message);}
}
