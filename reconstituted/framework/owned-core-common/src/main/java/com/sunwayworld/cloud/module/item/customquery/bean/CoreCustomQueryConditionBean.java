/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.customquery.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.sunwayworld.framework.data.annotation.Clob;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.support.domain.Insertable;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

@Table(value="T_CORE_CUSTOM_QUERY_CONDITION")
public class CoreCustomQueryConditionBean
extends AbstractInsertable<Long>
implements Insertable<Long> {
    @Transient
    private static final long serialVersionUID = -5232734281115706500L;
    @Id
    private Long id;
    private Long customQueryId;
    private String fieldName;
    private String title;
    private String conditionCategory;
    private String dsCategory;
    private String dataSource;
    @Clob
    private String customSql;
    private String frReportDsName;
    private String matchPattern;
    private Long orderNo;
    private String chooseField;
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

    public Long getCustomQueryId() {
        return this.customQueryId;
    }

    public void setCustomQueryId(Long customQueryId) {
        this.customQueryId = customQueryId;
    }

    public String getFieldName() {
        return this.fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getConditionCategory() {
        return this.conditionCategory;
    }

    public void setConditionCategory(String conditionCategory) {
        this.conditionCategory = conditionCategory;
    }

    public String getDsCategory() {
        return this.dsCategory;
    }

    public void setDsCategory(String dsCategory) {
        this.dsCategory = dsCategory;
    }

    public String getDataSource() {
        return this.dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public String getCustomSql() {
        return this.customSql;
    }

    public void setCustomSql(String customSql) {
        this.customSql = customSql;
    }

    public String getFrReportDsName() {
        return this.frReportDsName;
    }

    public void setFrReportDsName(String frReportDsName) {
        this.frReportDsName = frReportDsName;
    }

    public String getMatchPattern() {
        return this.matchPattern;
    }

    public void setMatchPattern(String matchPattern) {
        this.matchPattern = matchPattern;
    }

    public Long getOrderNo() {
        return this.orderNo;
    }

    public void setOrderNo(Long orderNo) {
        this.orderNo = orderNo;
    }

    public String getChooseField() {
        return this.chooseField;
    }

    public void setChooseField(String chooseField) {
        this.chooseField = chooseField;
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
