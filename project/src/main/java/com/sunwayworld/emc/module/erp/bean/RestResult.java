package com.sunwayworld.emc.module.erp.bean;

/**
 * @author zhengxy@sunwayworld.com 2023年07月25日
 */
public class RestResult<T> {

    private String resultCode;
    private String message;
    private String errorStackTrack;
    private T resultData;

    public RestResult() {
    }

    public RestResult(String resultCode, String message) {
        this.resultCode = resultCode;
        this.message = message;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getErrorStackTrack() {
        return errorStackTrack;
    }

    public void setErrorStackTrack(String errorStackTrack) {
        this.errorStackTrack = errorStackTrack;
    }

    public T getResultData() {
        return resultData;
    }

    public void setResultData(T resultData) {
        this.resultData = resultData;
    }
}
