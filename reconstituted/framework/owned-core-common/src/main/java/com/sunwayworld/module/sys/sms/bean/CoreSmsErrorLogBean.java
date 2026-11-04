/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.sms.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.sunwayworld.framework.data.annotation.Clob;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

@Table(value="T_CORE_SMS_ERROR_LOG")
public class CoreSmsErrorLogBean
extends AbstractInsertable<Long> {
    private static final long serialVersionUID = -3059482309253760850L;
    @Id
    private Long id;
    private Long smsId;
    @Clob
    private String errorLog;
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getSmsId() {
        return this.smsId;
    }

    public void setSmsId(Long smsId) {
        this.smsId = smsId;
    }

    public String getErrorLog() {
        return this.errorLog;
    }

    public void setErrorLog(String errorLog) {
        this.errorLog = errorLog;
    }
}
