package com.sunwayworld.framework.exception.database;
import org.springframework.http.HttpStatus; import org.springframework.web.bind.annotation.ResponseStatus;
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) public class UnsupportedDataTypeJdbcException extends RuntimeException {
 private static final long serialVersionUID=-5603054311012304227L; public UnsupportedDataTypeJdbcException(String dataType){super(dataType);}
}
