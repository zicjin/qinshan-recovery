/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.riskidentificaregist.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractAuditable;
import com.sunwayworld.framework.support.domain.Auditable;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

@Table(value="T_CORE_RISK_IDENTIFICA_REGIST")
public class CoreRiskIdentificaRegistBean
extends AbstractAuditable<Long>
implements Auditable<Long> {
    @Transient
    private static final long serialVersionUID = 612610319067957890L;
    @Id
    private Long id;
    private String createdByOrgName;
    private String createdByOrgId;
    @JSONField(format="yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;
    private String createdByName;
    private String createdById;
    private String groupReviewResult;
    private String riskDesc;
    private String riskNo;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getCreatedByOrgName() {
        return this.createdByOrgName;
    }

    public void setCreatedByOrgName(String createdByOrgName) {
        this.createdByOrgName = createdByOrgName;
    }

    public String getCreatedByOrgId() {
        return this.createdByOrgId;
    }

    public void setCreatedByOrgId(String createdByOrgId) {
        this.createdByOrgId = createdByOrgId;
    }

    public LocalDateTime getCreatedTime() {
        return this.createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public String getCreatedByName() {
        return this.createdByName;
    }

    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    public String getCreatedById() {
        return this.createdById;
    }

    public void setCreatedById(String createdById) {
        this.createdById = createdById;
    }

    public String getGroupReviewResult() {
        return this.groupReviewResult;
    }

    public void setGroupReviewResult(String groupReviewResult) {
        this.groupReviewResult = groupReviewResult;
    }

    public String getRiskDesc() {
        return this.riskDesc;
    }

    public void setRiskDesc(String riskDesc) {
        this.riskDesc = riskDesc;
    }

    public String getRiskNo() {
        return this.riskNo;
    }

    public void setRiskNo(String riskNo) {
        this.riskNo = riskNo;
    }
}
