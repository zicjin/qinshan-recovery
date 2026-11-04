/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.train.examtmpl.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.NotNull;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.support.domain.Activatable;
import com.sunwayworld.framework.support.domain.Insertable;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

@Table(value="T_CORE_EXAM_TMPL")
public class CoreExamTmplBean
extends AbstractInsertable<Long>
implements Insertable<Long>,
Activatable<Long> {
    @Transient
    private static final long serialVersionUID = -2472665167183562213L;
    @Id
    private Long id;
    private String tmplName;
    @NotNull(defaultValue="0")
    private Long singleChoiceQty;
    @NotNull(defaultValue="0")
    private Long singleChoiceScore;
    @NotNull(defaultValue="0")
    private Long multipleChoiceQty;
    @NotNull(defaultValue="0")
    private Long multipleChoiceScore;
    @NotNull(defaultValue="0")
    private Long completionQty;
    @NotNull(defaultValue="0")
    private Long completionScore;
    @NotNull(defaultValue="0")
    private Long judgementQty;
    @NotNull(defaultValue="0")
    private Long judgementScore;
    @NotNull(defaultValue="0")
    private Long shortAnswerQty;
    @NotNull(defaultValue="0")
    private Long shortAnswerScore;
    @NotNull(defaultValue="0")
    private Long totalScore;
    @NotNull(defaultValue="0")
    private Long citationQty;
    private String createdById;
    private String createdByName;
    @JSONField(format="yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;
    private String createdByOrgId;
    private String createdByOrgName;
    @NotNull(defaultValue="0")
    private String activatedFlag;
    private String activatedById;
    private String activatedByName;
    @JSONField(format="yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime activatedTime;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getTmplName() {
        return this.tmplName;
    }

    public void setTmplName(String tmplName) {
        this.tmplName = tmplName;
    }

    public Long getSingleChoiceQty() {
        return this.singleChoiceQty;
    }

    public void setSingleChoiceQty(Long singleChoiceQty) {
        this.singleChoiceQty = singleChoiceQty;
    }

    public Long getSingleChoiceScore() {
        return this.singleChoiceScore;
    }

    public void setSingleChoiceScore(Long singleChoiceScore) {
        this.singleChoiceScore = singleChoiceScore;
    }

    public Long getCompletionQty() {
        return this.completionQty;
    }

    public void setCompletionQty(Long completionQty) {
        this.completionQty = completionQty;
    }

    public Long getCompletionScore() {
        return this.completionScore;
    }

    public void setCompletionScore(Long completionScore) {
        this.completionScore = completionScore;
    }

    public Long getMultipleChoiceQty() {
        return this.multipleChoiceQty;
    }

    public void setMultipleChoiceQty(Long multipleChoiceQty) {
        this.multipleChoiceQty = multipleChoiceQty;
    }

    public Long getMultipleChoiceScore() {
        return this.multipleChoiceScore;
    }

    public void setMultipleChoiceScore(Long multipleChoiceScore) {
        this.multipleChoiceScore = multipleChoiceScore;
    }

    public Long getJudgementQty() {
        return this.judgementQty;
    }

    public void setJudgementQty(Long judgementQty) {
        this.judgementQty = judgementQty;
    }

    public Long getJudgementScore() {
        return this.judgementScore;
    }

    public void setJudgementScore(Long judgementScore) {
        this.judgementScore = judgementScore;
    }

    public Long getShortAnswerQty() {
        return this.shortAnswerQty;
    }

    public void setShortAnswerQty(Long shortAnswerQty) {
        this.shortAnswerQty = shortAnswerQty;
    }

    public Long getShortAnswerScore() {
        return this.shortAnswerScore;
    }

    public void setShortAnswerScore(Long shortAnswerScore) {
        this.shortAnswerScore = shortAnswerScore;
    }

    public Long getTotalScore() {
        return this.totalScore;
    }

    public void setTotalScore(Long totalScore) {
        this.totalScore = totalScore;
    }

    public Long getCitationQty() {
        return this.citationQty;
    }

    public void setCitationQty(Long citationQty) {
        this.citationQty = citationQty;
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

    @Override
    public String getActivatedFlag() {
        return this.activatedFlag;
    }

    @Override
    public void setActivatedFlag(String activatedFlag) {
        this.activatedFlag = activatedFlag;
    }

    @Override
    public String getActivatedById() {
        return this.activatedById;
    }

    @Override
    public void setActivatedById(String activatedById) {
        this.activatedById = activatedById;
    }

    @Override
    public String getActivatedByName() {
        return this.activatedByName;
    }

    @Override
    public void setActivatedByName(String activatedByName) {
        this.activatedByName = activatedByName;
    }

    @Override
    public LocalDateTime getActivatedTime() {
        return this.activatedTime;
    }

    @Override
    public void setActivatedTime(LocalDateTime activatedTime) {
        this.activatedTime = activatedTime;
    }
}
