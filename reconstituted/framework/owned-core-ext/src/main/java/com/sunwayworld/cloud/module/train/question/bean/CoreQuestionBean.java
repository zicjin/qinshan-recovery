/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.train.question.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.NotNull;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.support.domain.Activatable;
import com.sunwayworld.framework.support.domain.Insertable;
import com.sunwayworld.framework.support.domain.Suspendable;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

@Table(value="T_CORE_QUESTION")
public class CoreQuestionBean
extends AbstractInsertable<Long>
implements Insertable<Long>,
Activatable<Long>,
Suspendable<Long> {
    @Transient
    private static final long serialVersionUID = -2472665167183562213L;
    @Id
    private Long id;
    private String questionCategory;
    private String questionType;
    private String questionContent;
    private String answer;
    private String judgementAnswer;
    private Long questionTypeOrderNo;
    private String lastUpdatedById;
    private String lastUpdatedByName;
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastUpdatedTime;
    @NotNull(defaultValue="0")
    private String activatedFlag;
    private String activatedById;
    private String activatedByName;
    @JSONField(format="yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime activatedTime;
    @NotNull(defaultValue="0")
    private String lastSuspendedFlag;
    private String lastSuspendedById;
    private String lastSuspendedByName;
    @JSONField(format="yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastSuspendedTime;
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

    public String getQuestionCategory() {
        return this.questionCategory;
    }

    public void setQuestionCategory(String questionCategory) {
        this.questionCategory = questionCategory;
    }

    public String getQuestionType() {
        return this.questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public String getQuestionContent() {
        return this.questionContent;
    }

    public void setQuestionContent(String questionContent) {
        this.questionContent = questionContent;
    }

    public String getAnswer() {
        return this.answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getJudgementAnswer() {
        return this.judgementAnswer;
    }

    public void setJudgementAnswer(String judgementAnswer) {
        this.judgementAnswer = judgementAnswer;
    }

    public Long getQuestionTypeOrderNo() {
        return this.questionTypeOrderNo;
    }

    public void setQuestionTypeOrderNo(Long questionTypeOrderNo) {
        this.questionTypeOrderNo = questionTypeOrderNo;
    }

    public String getLastUpdatedById() {
        return this.lastUpdatedById;
    }

    public void setLastUpdatedById(String lastUpdatedById) {
        this.lastUpdatedById = lastUpdatedById;
    }

    public String getLastUpdatedByName() {
        return this.lastUpdatedByName;
    }

    public void setLastUpdatedByName(String lastUpdatedByName) {
        this.lastUpdatedByName = lastUpdatedByName;
    }

    public LocalDateTime getLastUpdatedTime() {
        return this.lastUpdatedTime;
    }

    public void setLastUpdatedTime(LocalDateTime lastUpdatedTime) {
        this.lastUpdatedTime = lastUpdatedTime;
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

    @Override
    public String getLastSuspendedFlag() {
        return this.lastSuspendedFlag;
    }

    @Override
    public void setLastSuspendedFlag(String lastSuspendedFlag) {
        this.lastSuspendedFlag = lastSuspendedFlag;
    }

    @Override
    public String getLastSuspendedById() {
        return this.lastSuspendedById;
    }

    @Override
    public void setLastSuspendedById(String lastSuspendedById) {
        this.lastSuspendedById = lastSuspendedById;
    }

    @Override
    public String getLastSuspendedByName() {
        return this.lastSuspendedByName;
    }

    @Override
    public void setLastSuspendedByName(String lastSuspendedByName) {
        this.lastSuspendedByName = lastSuspendedByName;
    }

    @Override
    public LocalDateTime getLastSuspendedTime() {
        return this.lastSuspendedTime;
    }

    @Override
    public void setLastSuspendedTime(LocalDateTime lastSuspendedTime) {
        this.lastSuspendedTime = lastSuspendedTime;
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
