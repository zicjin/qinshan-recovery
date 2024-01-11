package com.sunwayworld.emc.module.mdm.bizmdm.test.bean;

import java.time.LocalDateTime;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.format.annotation.DateTimeFormat;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractPersistable;
import com.sunwayworld.framework.support.domain.Insertable;

/**
 * 测试分析项QC指标表
 *
 * @author yuanhh@sunwayworld.com
 * @date 2019-09-19
 */

@Table("T_EMC_ITEM_QCINDEX")
public class EmcItemQcIndexBean extends AbstractPersistable<Long> implements Insertable<Long> {

    @Transient
    private static final long serialVersionUID = -3574492142967011516L;

    @Id
    private Long id;// 主键

    private Long itemId;// 分析项ID

    private Long qcCategoryId;// QC类型ID

    private String resultLowLimit;// 结果低限

    private String resultHighLimit;// 结果高限

    private String decisionCategory;// 判定类别

    private String stdLowLimit;// 标准低限

    private String stdHighLimit;// 标准高限

    private String orderCategory;// 样品类型

    private String createdById;// 制单人编码

    private String createdByName;// 制单人名称

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;// 制单时间

    private String createdByOrgId;// 制单人单位编码

    private String createdByOrgName;// 制单人单位名称

    private String monitorTmplId;// 样品类型id

    private String orgId;// 所属单位id

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public Long getQcCategoryId() {
        return qcCategoryId;
    }

    public void setQcCategoryId(Long qcCategoryId) {
        this.qcCategoryId = qcCategoryId;
    }

    public String getResultLowLimit() {
        return resultLowLimit;
    }

    public void setResultLowLimit(String resultLowLimit) {
        this.resultLowLimit = resultLowLimit;
    }

    public String getResultHighLimit() {
        return resultHighLimit;
    }

    public void setResultHighLimit(String resultHighLimit) {
        this.resultHighLimit = resultHighLimit;
    }

    public String getDecisionCategory() {
        return decisionCategory;
    }

    public void setDecisionCategory(String decisionCategory) {
        this.decisionCategory = decisionCategory;
    }

    public String getStdLowLimit() {
        return stdLowLimit;
    }

    public void setStdLowLimit(String stdLowLimit) {
        this.stdLowLimit = stdLowLimit;
    }

    public String getStdHighLimit() {
        return stdHighLimit;
    }

    public void setStdHighLimit(String stdHighLimit) {
        this.stdHighLimit = stdHighLimit;
    }

    public String getOrderCategory() {
        return orderCategory;
    }

    public void setOrderCategory(String orderCategory) {
        this.orderCategory = orderCategory;
    }

    public String getCreatedById() {
        return createdById;
    }

    public void setCreatedById(String createdById) {
        this.createdById = createdById;
    }

    public String getCreatedByName() {
        return createdByName;
    }

    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public String getCreatedByOrgId() {
        return createdByOrgId;
    }

    public void setCreatedByOrgId(String createdByOrgId) {
        this.createdByOrgId = createdByOrgId;
    }

    public String getCreatedByOrgName() {
        return createdByOrgName;
    }

    public void setCreatedByOrgName(String createdByOrgName) {
        this.createdByOrgName = createdByOrgName;
    }

    public String getMonitorTmplId() {
        return monitorTmplId;
    }

    public void setMonitorTmplId(String monitorTmplId) {
        this.monitorTmplId = monitorTmplId;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }
}
