/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.codegenerator.codegenerateconfig.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.support.domain.Insertable;
import com.sunwayworld.framework.support.domain.Orderable;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

@Table(value="T_CORE_CODE_GEN_CONFIG_FIELD")
public class CoreCodeGenerateConfigFieldBean
extends AbstractInsertable<Long>
implements Insertable<Long>,
Orderable<Long> {
    @Transient
    private static final long serialVersionUID = 7873768526275407026L;
    @Id
    private Long id;
    private Long tableId;
    private String field;
    private String fieldName;
    private String fieldLength;
    private String fieldType;
    private String require;
    private String dataRequire;
    private Long orderNo;
    private String mainDisplay;
    private String detailDisplay;
    private String createdById;
    private String createdByName;
    @JSONField(format="yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;
    private String createdByOrgId;
    private String createdByOrgName;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getTableId() {
        return this.tableId;
    }

    public void setTableId(Long tableId) {
        this.tableId = tableId;
    }

    public String getField() {
        return this.field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getFieldName() {
        return this.fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldLength() {
        return this.fieldLength;
    }

    public void setFieldLength(String fieldLength) {
        this.fieldLength = fieldLength;
    }

    public String getFieldType() {
        return this.fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public String getRequire() {
        return this.require;
    }

    public void setRequire(String require) {
        this.require = require;
    }

    public String getDataRequire() {
        return this.dataRequire;
    }

    public void setDataRequire(String dataRequire) {
        this.dataRequire = dataRequire;
    }

    public String getMainDisplay() {
        return this.mainDisplay;
    }

    public void setMainDisplay(String mainDisplay) {
        this.mainDisplay = mainDisplay;
    }

    public String getDetailDisplay() {
        return this.detailDisplay;
    }

    public void setDetailDisplay(String detailDisplay) {
        this.detailDisplay = detailDisplay;
    }

    @Override
    public Long getOrderNo() {
        return this.orderNo;
    }

    @Override
    public void setOrderNo(Long orderNo) {
        this.orderNo = orderNo;
    }

    @Override
    public String getCreatedById() {
        return this.createdById;
    }

    @Override
    public void setCreatedById(String createdById) {
        this.createdById = createdById;
    }

    @Override
    public String getCreatedByName() {
        return this.createdByName;
    }

    @Override
    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    @Override
    public LocalDateTime getCreatedTime() {
        return this.createdTime;
    }

    @Override
    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    @Override
    public String getCreatedByOrgId() {
        return this.createdByOrgId;
    }

    @Override
    public void setCreatedByOrgId(String createdByOrgId) {
        this.createdByOrgId = createdByOrgId;
    }

    @Override
    public String getCreatedByOrgName() {
        return this.createdByOrgName;
    }

    @Override
    public void setCreatedByOrgName(String createdByOrgName) {
        this.createdByOrgName = createdByOrgName;
    }
}
