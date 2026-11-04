/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.resource.bean;

import com.sunwayworld.framework.data.annotation.Transient;
import java.io.Serializable;

public class LcdpResourceBacthCheckoutDTO
implements Serializable {
    @Transient
    private static final long serialVersionUID = -5096587512508471946L;
    private Boolean valid;
    private String message;

    public LcdpResourceBacthCheckoutDTO(Boolean valid) {
        this.valid = valid;
    }

    public LcdpResourceBacthCheckoutDTO(Boolean valid, String message) {
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
