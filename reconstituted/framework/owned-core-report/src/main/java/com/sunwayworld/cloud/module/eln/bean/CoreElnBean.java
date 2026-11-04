/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.eln.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.sunwayworld.framework.data.annotation.Clob;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.ReadOnly;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractPersistable;
import com.sunwayworld.framework.support.domain.Insertable;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

@Table(value="T_CORE_ELN")
public class CoreElnBean
extends AbstractPersistable<Long>
implements Insertable<Long> {
    @Transient
    private static final long serialVersionUID = -150205844352772440L;
    @Id
    private Long id;
    private String elnName;
    private Long elnTmplId;
    private String bizCategory;
    private String bizId;
    @ReadOnly
    private String elnContent;
    private Long elnContentFileId;
    @Clob
    private String elnParam;
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

    public Long getElnTmplId() {
        return this.elnTmplId;
    }

    public void setElnTmplId(Long elnTmplId) {
        this.elnTmplId = elnTmplId;
    }

    public String getBizCategory() {
        return this.bizCategory;
    }

    public void setBizCategory(String bizCategory) {
        this.bizCategory = bizCategory;
    }

    public String getBizId() {
        return this.bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public String getElnContent() {
        return this.elnContent;
    }

    public void setElnContent(String elnContent) {
        this.elnContent = elnContent;
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

    public String getElnParam() {
        return this.elnParam;
    }

    public void setElnParam(String elnParam) {
        this.elnParam = elnParam;
    }

    public String getElnName() {
        return this.elnName;
    }

    public void setElnName(String elnName) {
        this.elnName = elnName;
    }

    public Long getElnContentFileId() {
        return this.elnContentFileId;
    }

    public void setElnContentFileId(Long elnContentFileId) {
        this.elnContentFileId = elnContentFileId;
    }
}
