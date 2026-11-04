/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.nfssi.order.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.sunwayworld.framework.data.annotation.Transient;
import java.io.Serializable;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

public class CoreNfssiOrderQueryParamDTO
implements Serializable {
    @Transient
    private static final long serialVersionUID = 9075811290638073247L;
    @JSONField(format="yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime receivedStartTime;
    @JSONField(format="yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime receivedEndTime;
    private String sampleType;
    private String token;

    public LocalDateTime getReceivedStartTime() {
        return this.receivedStartTime;
    }

    public void setReceivedStartTime(LocalDateTime receivedStartTime) {
        this.receivedStartTime = receivedStartTime;
    }

    public LocalDateTime getReceivedEndTime() {
        return this.receivedEndTime;
    }

    public void setReceivedEndTime(LocalDateTime receivedEndTime) {
        this.receivedEndTime = receivedEndTime;
    }

    public String getSampleType() {
        return this.sampleType;
    }

    public void setSampleType(String sampleType) {
        this.sampleType = sampleType;
    }

    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
