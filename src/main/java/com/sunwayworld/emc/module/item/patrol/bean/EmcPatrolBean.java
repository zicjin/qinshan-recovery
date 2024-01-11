package com.sunwayworld.emc.module.item.patrol.bean;

import java.time.LocalDateTime;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.format.annotation.DateTimeFormat;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractPersistable;
import com.sunwayworld.framework.support.domain.Insertable;

/**
 * 实验室安全制度管理
 * @author cuikxt@sunwayworld.com
 * @date 2019-11-09
 */

@Table("T_EMC_PATROL")
public class EmcPatrolBean extends AbstractPersistable<Long> implements Insertable<Long> {

    @Transient
    private static final long serialVersionUID = -5378116527756089563L;

    @Id
    private Long id;// 主键

    private String patrolDate;// 巡查日期

    private String patrolWeekday;// 巡查星期

    @JSONField(format = "yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime patrolTime;// 巡查时间

    private String patrolUserName;// 巡查人name

    private String patrolUserId;// 巡查人id

    private String problem;// 问题

    private String handleUserIds;// 处理人员id

    private String handleUserNames;// 处理人员名字

    private String handleReback;// 处理反馈

    private String handleResult;// 状态

    private String createdById;// 制单人编码

    private String createdByName;// 制单人名称

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;// 制单时间

    private String createdByOrgId;// 制单人单位编码

    private String createdByOrgName;// 制单人单位名称

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getPatrolDate() {
        return patrolDate;
    }

    public void setPatrolDate(String patrolDate) {
        this.patrolDate = patrolDate;
    }

    public String getPatrolWeekday() {
        return patrolWeekday;
    }

    public void setPatrolWeekday(String patrolWeekday) {
        this.patrolWeekday = patrolWeekday;
    }

    public LocalDateTime getPatrolTime() {
        return patrolTime;
    }

    public void setPatrolTime(LocalDateTime patrolTime) {
        this.patrolTime = patrolTime;
    }

    public String getPatrolUserId() {
        return patrolUserId;
    }

    public void setPatrolUserId(String patrolUserId) {
        this.patrolUserId = patrolUserId;
    }

    public String getProblem() {
        return problem;
    }

    public void setProblem(String problem) {
        this.problem = problem;
    }

    public String getHandleUserIds() {
        return handleUserIds;
    }

    public void setHandleUserIds(String handleUserIds) {
        this.handleUserIds = handleUserIds;
    }

    public String getHandleUserNames() {
        return handleUserNames;
    }

    public void setHandleUserNames(String handleUserNames) {
        this.handleUserNames = handleUserNames;
    }

    public String getHandleReback() {
        return handleReback;
    }

    public void setHandleReback(String handleReback) {
        this.handleReback = handleReback;
    }

    public String getHandleResult() {
        return handleResult;
    }

    public void setHandleResult(String handleResult) {
        this.handleResult = handleResult;
    }

    @Override
    public String getCreatedById() {
        return createdById;
    }

    @Override
    public void setCreatedById(String createdById) {
        this.createdById = createdById;
    }

    @Override
    public String getCreatedByName() {
        return createdByName;
    }

    @Override
    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    @Override
    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    @Override
    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    @Override
    public String getCreatedByOrgId() {
        return createdByOrgId;
    }

    @Override
    public void setCreatedByOrgId(String createdByOrgId) {
        this.createdByOrgId = createdByOrgId;
    }

    @Override
    public String getCreatedByOrgName() {
        return createdByOrgName;
    }

    @Override
    public void setCreatedByOrgName(String createdByOrgName) {
        this.createdByOrgName = createdByOrgName;
    }

    public String getPatrolUserName() {
        return patrolUserName;
    }

    public void setPatrolUserName(String patrolUserName) {
        this.patrolUserName = patrolUserName;
    }
}
