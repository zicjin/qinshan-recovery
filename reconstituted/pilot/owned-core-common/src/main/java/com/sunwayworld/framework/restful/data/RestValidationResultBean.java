/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.restful.data;

import java.io.Serializable;

public class RestValidationResultBean
implements Serializable {
    private static final long serialVersionUID = -9109863731294581096L;
    private Boolean valid;
    private String message;

    public RestValidationResultBean(Boolean valid) {
        this.valid = valid;
    }

    public RestValidationResultBean(Boolean valid, String message) {
        this.valid = valid;
        this.message = message;
    }

    public Boolean getValid() {
        return this.valid;
    }

    public void setValid(Boolean valid) {
        this.valid = valid;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
