/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.exception.core;

import com.alibaba.fastjson.JSON;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.ServletUtils;
import java.io.Serializable;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;

public class HttpErrorMessage
implements Serializable {
    private static final long serialVersionUID = 8288215780660705202L;
    private Long timestamp;
    private Integer status;
    private String error;
    private String message;
    private String path;
    private String stacktrace;

    private HttpErrorMessage() {
    }

    public static HttpErrorMessage of(HttpStatus status, String message, Throwable throwable) {
        HttpErrorMessage errorMessage = HttpErrorMessage.of(status, message);
        errorMessage.setStacktrace(ObjectUtils.getStackTrace(throwable));
        return errorMessage;
    }

    public static HttpErrorMessage of(HttpStatus status, String message) {
        HttpErrorMessage errorMessage = new HttpErrorMessage();
        errorMessage.setTimestamp(System.currentTimeMillis());
        errorMessage.setStatus(status.value());
        errorMessage.setError(status.getReasonPhrase());
        errorMessage.setMessage(message);
        HttpServletRequest request = ServletUtils.getCurrentRequest();
        if (request != null) {
            errorMessage.setPath(request.getRequestURI());
        }
        return errorMessage;
    }

    public Long getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getStatus() {
        return this.status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getError() {
        return this.error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getStacktrace() {
        return this.stacktrace;
    }

    public void setStacktrace(String stacktrace) {
        this.stacktrace = stacktrace;
    }

    public String toString() {
        return JSON.toJSONString(this);
    }
}
