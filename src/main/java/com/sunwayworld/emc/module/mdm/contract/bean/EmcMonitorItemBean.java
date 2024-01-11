package com.sunwayworld.emc.module.mdm.contract.bean;

import java.time.LocalDateTime;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.format.annotation.DateTimeFormat;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.support.domain.Insertable;

/**
 * @author jinx@sunwayworld.com
 * @date 2020-04-03
 */
@Table("T_EMC_MONITOR_ITEM")
public class EmcMonitorItemBean extends AbstractInsertable<Long> implements Insertable<Long> {

    @Transient
    private static final long serialVersionUID = 8679505258944071644L;

    @Id
    private Long id;// 主键

    private Long expenseCategoryId;// 费用类别ID

    private Long contractId;// 合同ID

    private Long testId;// 测试ID

    private Long methodId;// 方法ID

    private String monitorPointCountOne;// 监测点数1

    private String sampleCountOne;// 采样次数1

    private String monitorPointCountTwo;// 监测点数2

    private String sampleCountTwo;// 采样次数2

    private String price;// 价格

    private String monitorTmplId; // 监测模板ID

    private String orderCategory; // 样品类型

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

    public Long getExpenseCategoryId() {
        return expenseCategoryId;
    }

    public void setExpenseCategoryId(Long expenseCategoryId) {
        this.expenseCategoryId = expenseCategoryId;
    }

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public Long getTestId() {
        return testId;
    }

    public void setTestId(Long testId) {
        this.testId = testId;
    }

    public Long getMethodId() {
        return methodId;
    }

    public void setMethodId(Long methodId) {
        this.methodId = methodId;
    }

    public String getMonitorPointCountOne() {
        return monitorPointCountOne;
    }

    public void setMonitorPointCountOne(String monitorPointCountOne) {
        this.monitorPointCountOne = monitorPointCountOne;
    }

    public String getSampleCountOne() {
        return sampleCountOne;
    }

    public void setSampleCountOne(String sampleCountOne) {
        this.sampleCountOne = sampleCountOne;
    }

    public String getMonitorPointCountTwo() {
        return monitorPointCountTwo;
    }

    public void setMonitorPointCountTwo(String monitorPointCountTwo) {
        this.monitorPointCountTwo = monitorPointCountTwo;
    }

    public String getSampleCountTwo() {
        return sampleCountTwo;
    }

    public void setSampleCountTwo(String sampleCountTwo) {
        this.sampleCountTwo = sampleCountTwo;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getMonitorTmplId() {
        return monitorTmplId;
    }

    public void setMonitorTmplId(String monitorTmplId) {
        this.monitorTmplId = monitorTmplId;
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

}
