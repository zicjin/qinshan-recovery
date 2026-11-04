/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.lims.reporttemplate.support.data;

import java.io.Serializable;

public class LimsCustomizeRenderData<T>
implements Serializable {
    private static final long serialVersionUID = -6829159272864634786L;
    private T data;
    private String customerRender;

    public T getData() {
        return this.data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getCustomerRender() {
        return this.customerRender;
    }

    public void setCustomerRender(String customerRender) {
        this.customerRender = customerRender;
    }
}
