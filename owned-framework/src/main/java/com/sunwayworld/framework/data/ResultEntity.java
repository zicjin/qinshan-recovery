package com.sunwayworld.framework.data;

import java.io.Serializable;

/** Supplier-compatible success/failure result carrier. */
public class ResultEntity implements Serializable {
    private static final long serialVersionUID = -1490526520371140941L;
    private boolean sucess;
    private String message;

    private ResultEntity() { }

    public static ResultEntity success() {
        ResultEntity entity = new ResultEntity();
        entity.setSucess(true);
        return entity;
    }

    public static ResultEntity fail(String message) {
        ResultEntity entity = new ResultEntity();
        entity.setSucess(false);
        entity.setMessage(message);
        return entity;
    }

    public boolean isSucess() { return sucess; }
    public void setSucess(boolean sucess) { this.sucess = sucess; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
