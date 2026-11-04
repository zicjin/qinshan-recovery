/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.monitor.monitor.bean;

import com.sunwayworld.framework.data.annotation.Transient;
import java.io.Serializable;

public class CoreMonitorAlarmDTO
implements Serializable {
    @Transient
    private static final long serialVersionUID = -6556636550794813939L;
    private Long normalQty;
    private Long alarmQty;
    private Long abnormalQty;

    public Long getNormalQty() {
        return this.normalQty;
    }

    public void setNormalQty(Long normalQty) {
        this.normalQty = normalQty;
    }

    public Long getAlarmQty() {
        return this.alarmQty;
    }

    public void setAlarmQty(Long alarmQty) {
        this.alarmQty = alarmQty;
    }

    public Long getAbnormalQty() {
        return this.abnormalQty;
    }

    public void setAbnormalQty(Long abnormalQty) {
        this.abnormalQty = abnormalQty;
    }
}
