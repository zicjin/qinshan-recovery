package com.sunwayworld.framework.exception.core;

import com.alibaba.fastjson.JSON;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.owned.internal.context.OwnedRequestContext;
import java.io.Serializable;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;

/** Owned serializable HTTP error payload for the web error seam. */
public class HttpErrorMessage implements Serializable {
    private static final long serialVersionUID = 8288215780660705202L;
    private Long timestamp;
    private Integer status;
    private String error;
    private String message;
    private String path;
    private String stacktrace;
    private HttpErrorMessage() { }
    public static HttpErrorMessage of(HttpStatus status, String message, Throwable throwable) { HttpErrorMessage value=of(status,message); value.setStacktrace(ObjectUtils.getStackTrace(throwable)); return value; }
    public static HttpErrorMessage of(HttpStatus status, String message) { HttpErrorMessage value=new HttpErrorMessage(); value.setTimestamp(System.currentTimeMillis()); value.setStatus(status.value()); value.setError(status.getReasonPhrase()); value.setMessage(message); HttpServletRequest request=OwnedRequestContext.getHttpServletRequest(); if(request!=null)value.setPath(request.getRequestURI()); return value; }
    public Long getTimestamp(){return timestamp;} public void setTimestamp(Long value){timestamp=value;}
    public Integer getStatus(){return status;} public void setStatus(Integer value){status=value;}
    public String getError(){return error;} public void setError(String value){error=value;}
    public String getMessage(){return message;} public void setMessage(String value){message=value;}
    public String getPath(){return path;} public void setPath(String value){path=value;}
    public String getStacktrace(){return stacktrace;} public void setStacktrace(String value){stacktrace=value;}
    public String toString(){return JSON.toJSONString(this);}
}
