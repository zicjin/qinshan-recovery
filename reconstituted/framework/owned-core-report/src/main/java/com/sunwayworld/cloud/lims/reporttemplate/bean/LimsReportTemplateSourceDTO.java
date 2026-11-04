/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.lims.reporttemplate.bean;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.sunwayworld.cloud.lims.reporttemplate.bean.LimsReportTemplateColumnDTO;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractBaseData;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;

public class LimsReportTemplateSourceDTO
extends AbstractBaseData {
    @Transient
    private static final long serialVersionUID = -7154649662543394200L;
    private Long id;
    private Long templateId;
    private String sourceCode;
    private String parentSourceCode;
    private String sourceName;
    private String sourceTable;
    private String sourceType;
    private String sql;
    private String paramTarget;
    private String param;
    private String remark;
    private String resolveFunction;
    private String createdById;
    private String createdByName;
    @JSONField(format="yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;
    private String createdByOrgId;
    private String createdByOrgName;
    private List<LimsReportTemplateColumnDTO> columnsList;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTemplateId() {
        return this.templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public String getSourceCode() {
        return this.sourceCode;
    }

    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    public String getSourceType() {
        return this.sourceType;
    }

    public String getSourceName() {
        return this.sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getSourceTable() {
        return this.sourceTable;
    }

    public void setSourceTable(String sourceTable) {
        this.sourceTable = sourceTable;
    }

    public String getSql() {
        return this.sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getParamTarget() {
        return this.paramTarget;
    }

    public void setParamTarget(String paramTarget) {
        this.paramTarget = paramTarget;
    }

    public String getParam() {
        return this.param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCreatedById() {
        return this.createdById;
    }

    public void setCreatedById(String createdById) {
        this.createdById = createdById;
    }

    public String getCreatedByName() {
        return this.createdByName;
    }

    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    public LocalDateTime getCreatedTime() {
        return this.createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public String getCreatedByOrgId() {
        return this.createdByOrgId;
    }

    public void setCreatedByOrgId(String createdByOrgId) {
        this.createdByOrgId = createdByOrgId;
    }

    public String getCreatedByOrgName() {
        return this.createdByOrgName;
    }

    public void setCreatedByOrgName(String createdByOrgName) {
        this.createdByOrgName = createdByOrgName;
    }

    public String getParentSourceCode() {
        return this.parentSourceCode;
    }

    public void setParentSourceCode(String parentSourceCode) {
        this.parentSourceCode = parentSourceCode;
    }

    public String getResolveFunction() {
        return this.resolveFunction;
    }

    public void setResolveFunction(String resolveFunction) {
        this.resolveFunction = resolveFunction;
    }

    public List<LimsReportTemplateColumnDTO> getColumnsList() {
        return this.columnsList;
    }

    public void setColumnsList(List<LimsReportTemplateColumnDTO> columnsList) {
        this.columnsList = columnsList;
    }

    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
