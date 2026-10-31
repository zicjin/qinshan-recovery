package com.sunwayworld.framework.exception.database;
import org.springframework.http.HttpStatus; import org.springframework.web.bind.annotation.ResponseStatus;
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) public class MisMatchRowNumberJdbcException extends RuntimeException {
 private static final long serialVersionUID=-4221330128768720570L; public MisMatchRowNumberJdbcException(int expectedRowNumber,int actualRowNumber){super("Expected row number ["+expectedRowNumber+"], actual ["+actualRowNumber+"].");}
}
