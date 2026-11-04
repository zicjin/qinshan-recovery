/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.office.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.NotNull;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.support.domain.Insertable;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

@Table(value="T_CORE_OFFICE_DS_FIELD")
public class CoreOfficeDsFieldBean
extends AbstractInsertable<Long>
implements Insertable<Long> {
    @Transient
    private static final long serialVersionUID = -7144304425824589143L;
    @Id
    private Long id;
    private Long dsId;
    private String sqlColumnName;
    private String showColumnName;
    @NotNull(defaultValue="0")
    private String isPicture;
    private Long pictureWidth;
    private Long pictureHeight;
    @NotNull(defaultValue="0")
    private String writeBack;
    private String updateTableName;
    private String updateColumnName;
    private Long orderNo;
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

    public Long getDsId() {
        return this.dsId;
    }

    public void setDsId(Long dsId) {
        this.dsId = dsId;
    }

    public String getSqlColumnName() {
        return this.sqlColumnName;
    }

    public void setSqlColumnName(String sqlColumnName) {
        this.sqlColumnName = sqlColumnName;
    }

    public String getShowColumnName() {
        return this.showColumnName;
    }

    public void setShowColumnName(String showColumnName) {
        this.showColumnName = showColumnName;
    }

    public String getIsPicture() {
        return this.isPicture;
    }

    public void setIsPicture(String isPicture) {
        this.isPicture = isPicture;
    }

    public Long getPictureWidth() {
        return this.pictureWidth;
    }

    public void setPictureWidth(Long pictureWidth) {
        this.pictureWidth = pictureWidth;
    }

    public Long getPictureHeight() {
        return this.pictureHeight;
    }

    public void setPictureHeight(Long pictureHeight) {
        this.pictureHeight = pictureHeight;
    }

    public String getWriteBack() {
        return this.writeBack;
    }

    public void setWriteBack(String writeBack) {
        this.writeBack = writeBack;
    }

    public String getUpdateTableName() {
        return this.updateTableName;
    }

    public void setUpdateTableName(String updateTableName) {
        this.updateTableName = updateTableName;
    }

    public String getUpdateColumnName() {
        return this.updateColumnName;
    }

    public void setUpdateColumnName(String updateColumnName) {
        this.updateColumnName = updateColumnName;
    }

    public Long getOrderNo() {
        return this.orderNo;
    }

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
