/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.bp.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.NotNull;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.support.domain.Insertable;
import com.sunwayworld.framework.support.domain.Orderable;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

@Table(value="T_LCDP_BIZ_PROCESS_NODE")
public class LcdpBizProcessNodeBean
extends AbstractInsertable<Long>
implements Insertable<Long>,
Orderable<Long> {
    @Transient
    private static final long serialVersionUID = 5984070833682572834L;
    @Id
    private Long id;
    private Long bpId;
    private String bpNodeCode;
    private String bpNodeName;
    private String bpNodeDesc;
    private Long orderNo;
    @NotNull(defaultValue="1")
    private String menuAddFlag;
    private String createdById;
    private String createdByName;
    @JSONField(format="yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;
    private String createdByOrgId;
    private String createdByOrgName;
    private String special;
    private String specialExpression;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getBpId() {
        return this.bpId;
    }

    public void setBpId(Long bpId) {
        this.bpId = bpId;
    }

    public String getBpNodeCode() {
        return this.bpNodeCode;
    }

    public void setBpNodeCode(String bpNodeCode) {
        this.bpNodeCode = bpNodeCode;
    }

    public String getBpNodeName() {
        return this.bpNodeName;
    }

    public void setBpNodeName(String bpNodeName) {
        this.bpNodeName = bpNodeName;
    }

    public String getBpNodeDesc() {
        return this.bpNodeDesc;
    }

    public void setBpNodeDesc(String bpNodeDesc) {
        this.bpNodeDesc = bpNodeDesc;
    }

    @Override
    public Long getOrderNo() {
        return this.orderNo;
    }

    @Override
    public void setOrderNo(Long orderNo) {
        this.orderNo = orderNo;
    }

    public String getMenuAddFlag() {
        return this.menuAddFlag;
    }

    public void setMenuAddFlag(String menuAddFlag) {
        this.menuAddFlag = menuAddFlag;
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

    public String getSpecial() {
        return this.special;
    }

    public void setSpecial(String special) {
        this.special = special;
    }

    public String getSpecialExpression() {
        return this.specialExpression;
    }

    public void setSpecialExpression(String specialExpression) {
        this.specialExpression = specialExpression;
    }
}
