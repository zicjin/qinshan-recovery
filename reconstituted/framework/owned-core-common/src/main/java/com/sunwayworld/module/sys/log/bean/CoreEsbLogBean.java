/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.log.bean;

import com.sunwayworld.framework.data.annotation.Clob;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractPersistable;
import com.sunwayworld.framework.support.domain.Persistable;
import java.time.LocalDateTime;

@Table(value="T_CORE_ESB_LOG")
public class CoreEsbLogBean
extends AbstractPersistable<Long>
implements Persistable<Long> {
    @Transient
    private static final long serialVersionUID = -1223168870123520533L;
    @Id
    private Long id;
    private String name;
    private String dataflow;
    private LocalDateTime requestDateTime;
    @Clob
    private String requestData;
    private LocalDateTime responseDateTime;
    @Clob
    private String responseData;
    private String responseStatus;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDataflow() {
        return this.dataflow;
    }

    public void setDataflow(String dataflow) {
        this.dataflow = dataflow;
    }

    public LocalDateTime getRequestDateTime() {
        return this.requestDateTime;
    }

    public void setRequestDateTime(LocalDateTime requestDateTime) {
        this.requestDateTime = requestDateTime;
    }

    public String getRequestData() {
        return this.requestData;
    }

    public void setRequestData(String requestData) {
        this.requestData = requestData;
    }

    public LocalDateTime getResponseDateTime() {
        return this.responseDateTime;
    }

    public void setResponseDateTime(LocalDateTime responseDateTime) {
        this.responseDateTime = responseDateTime;
    }

    public String getResponseData() {
        return this.responseData;
    }

    public void setResponseData(String responseData) {
        this.responseData = responseData;
    }

    public String getResponseStatus() {
        return this.responseStatus;
    }

    public void setResponseStatus(String responseStatus) {
        this.responseStatus = responseStatus;
    }
}
