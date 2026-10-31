package com.sunwayworld.emc.module.bp.order.orderretain.bean;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import com.alibaba.fastjson.annotation.JSONField;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractAuditable;
import com.sunwayworld.framework.support.domain.Insertable;

/**
 * 留样实体类
 * @author zhengxy@sunwayworld.com 2019年10月17日
 */
@Table("T_EMC_ORDER_RETAIN")
public class EmcOrderRetainBean extends AbstractAuditable<Long> implements Insertable<Long> {

    @Transient
    private static final long serialVersionUID = -2740713738360456106L;

    @Id
    private Long id;  // 主键
    private Long orderId;  // 样品编码
    private Long projId;  // 项目编码
    private Long folderId;  // 点位编码
    private Long retainQty;  // 留样数量
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime retainTimeLimit;  // 留样时限
    private String retainLoc;  // 留样位置
    private String retainUserId;  // 留样人
    private Long retainDays;  // 留样天数
    private String handleUserId;  // 处置人
    private LocalDate handleDate;  // 处置日期
    private String handleRecord;  // 处置记录
    
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

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getProjId() {
        return projId;
    }

    public void setProjId(Long projId) {
        this.projId = projId;
    }

    public Long getFolderId() {
        return folderId;
    }

    public void setFolderId(Long folderId) {
        this.folderId = folderId;
    }

    public Long getRetainQty() {
        return retainQty;
    }

    public void setRetainQty(Long retainQty) {
        this.retainQty = retainQty;
    }

    public LocalDateTime getRetainTimeLimit() {
        return retainTimeLimit;
    }

    public void setRetainTimeLimit(LocalDateTime retainTimeLimit) {
        this.retainTimeLimit = retainTimeLimit;
    }

    public String getRetainLoc() {
        return retainLoc;
    }

    public void setRetainLoc(String retainLoc) {
        this.retainLoc = retainLoc;
    }

    public String getRetainUserId() {
        return retainUserId;
    }

    public void setRetainUserId(String retainUserId) {
        this.retainUserId = retainUserId;
    }

    public Long getRetainDays() {
        return retainDays;
    }

    public void setRetainDays(Long retainDays) {
        this.retainDays = retainDays;
    }

    public String getHandleUserId() {
        return handleUserId;
    }

    public void setHandleUserId(String handleUserId) {
        this.handleUserId = handleUserId;
    }

    public LocalDate getHandleDate() {
        return handleDate;
    }

    public void setHandleDate(LocalDate handleDate) {
        this.handleDate = handleDate;
    }

    public String getHandleRecord() {
        return handleRecord;
    }

    public void setHandleRecord(String handleRecord) {
        this.handleRecord = handleRecord;
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


}
