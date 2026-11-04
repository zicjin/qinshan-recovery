/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.customsurvey.bean;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.NotNull;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractPersistable;
import com.sunwayworld.framework.support.domain.Persistable;

@Table(value="T_CORE_CUSTOM_SURVEY_SUM_RET")
public class CoreCustomSurveySumRetBean
extends AbstractPersistable<Long>
implements Persistable<Long> {
    @Transient
    private static final long serialVersionUID = -82566196618711043L;
    @Id
    private Long id;
    private Long surveySumId;
    private String surveyContent;
    @NotNull(defaultValue="0")
    private Long total;
    @NotNull(defaultValue="0")
    private Long verySatisfiedCount;
    @NotNull(defaultValue="0")
    private Long satisfiedCount;
    @NotNull(defaultValue="0")
    private Long commonlyCount;
    @NotNull(defaultValue="0")
    private Long needsImprovedCount;
    @NotNull(defaultValue="0")
    private Long dissatisfiedCount;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getSurveySumId() {
        return this.surveySumId;
    }

    public void setSurveySumId(Long surveySumId) {
        this.surveySumId = surveySumId;
    }

    public String getSurveyContent() {
        return this.surveyContent;
    }

    public void setSurveyContent(String surveyContent) {
        this.surveyContent = surveyContent;
    }

    public Long getTotal() {
        return this.total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getVerySatisfiedCount() {
        return this.verySatisfiedCount;
    }

    public void setVerySatisfiedCount(Long verySatisfiedCount) {
        this.verySatisfiedCount = verySatisfiedCount;
    }

    public Long getSatisfiedCount() {
        return this.satisfiedCount;
    }

    public void setSatisfiedCount(Long satisfiedCount) {
        this.satisfiedCount = satisfiedCount;
    }

    public Long getCommonlyCount() {
        return this.commonlyCount;
    }

    public void setCommonlyCount(Long commonlyCount) {
        this.commonlyCount = commonlyCount;
    }

    public Long getNeedsImprovedCount() {
        return this.needsImprovedCount;
    }

    public void setNeedsImprovedCount(Long needsImprovedCount) {
        this.needsImprovedCount = needsImprovedCount;
    }

    public Long getDissatisfiedCount() {
        return this.dissatisfiedCount;
    }

    public void setDissatisfiedCount(Long dissatisfiedCount) {
        this.dissatisfiedCount = dissatisfiedCount;
    }
}
