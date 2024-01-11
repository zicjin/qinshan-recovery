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
 * 测试样品类型表
 *
 * @author yuanhh@sunwayworld.com
 * @date 2019-09-19
 */

@Table("T_EMC_TEST_ORDER_CATEGORY")
public class EmcTestMethodOrderCategoryBean extends AbstractPersistable<Long> implements Insertable<Long> {

    @Transient
    private static final long serialVersionUID = -5227308569046025142L;

    @Id
    private Long id;// 主键

    private Long testMethodId;// 测试方法ID

    private String monitorTmplId;// 监测模板ID

    private Long containerCategoryId;// 容器类型ID

    private String containerGroup;// 容器组

    private String fixative;// 固定剂

    private Long sampleqty;// 采样个数

    private String sampleWeight;// 采样重量

    private String storeCondition; // 保存方式

    private String createdById;// 制单人编码

    private String createdByName;// 制单人名称

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;// 制单时间

    private String createdByOrgId;// 制单人单位编码

    private String createdByOrgName;// 制单人单位名称

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTestMethodId() {
        return testMethodId;
    }

    public void setTestMethodId(Long testMethodId) {
        this.testMethodId = testMethodId;
    }

    public String getMonitorTmplId() {
        return monitorTmplId;
    }

    public void setMonitorTmplId(String monitorTmplId) {
        this.monitorTmplId = monitorTmplId;
    }

    public Long getContainerCategoryId() {
        return containerCategoryId;
    }

    public void setContainerCategoryId(Long containerCategoryId) {
        this.containerCategoryId = containerCategoryId;
    }

    public String getContainerGroup() {
        return containerGroup;
    }

    public void setContainerGroup(String containerGroup) {
        this.containerGroup = containerGroup;
    }

    public String getFixative() {
        return fixative;
    }

    public void setFixative(String fixative) {
        this.fixative = fixative;
    }

    public Long getSampleqty() {
        return sampleqty;
    }

    public void setSampleqty(Long sampleqty) {
        this.sampleqty = sampleqty;
    }

    public String getSampleWeight() {
        return sampleWeight;
    }

    public void setSampleWeight(String sampleWeight) {
        this.sampleWeight = sampleWeight;
    }

    public String getStoreCondition() {
        return storeCondition;
    }

    public void setStoreCondition(String storeCondition) {
        this.storeCondition = storeCondition;
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
}
