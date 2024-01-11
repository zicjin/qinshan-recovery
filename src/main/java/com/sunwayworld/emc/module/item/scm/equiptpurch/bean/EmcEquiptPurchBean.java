package com.sunwayworld.emc.module.item.scm.equiptpurch.bean;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.format.annotation.DateTimeFormat;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractAuditable;
import com.sunwayworld.framework.support.domain.Auditable;

/**
 * 仪器采购表
 *
 * @author qianc@sunwayworld.com 2019年11月12日
 */
@Table("T_EMC_EQUIPT_PURCH")
public class EmcEquiptPurchBean extends AbstractAuditable<Long> implements Auditable<Long>{

    @Transient
    private static final long serialVersionUID = 5904766122832005675L;

    @Id
    private Long id;// 主键

    private String equiptName;// 仪器名称

    private String model;// 型号

    private String manufacturer;// 生产厂家

    private String purchLoc;// 购买地点

    private Double purchQty;// 采购数量

    private Double price;// 单价

    private String purchAttachName;// 购买附件名称

    private Double fee;// 经费

    private String payWay;// 付款方式

    private Double totalAmount;// 总金额

    private String useDesc;// 技术指标、用途说明

    private String factoryNo;// 出厂编号

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate factoryDate;// 出厂日期

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate purchDate;// 购置日期

    private String contractNo;// 合同编号

    private Double amount;// 金额

    private String checkRecord;// 调试验收记录

    private String checkResult;// 验收结果

    private String checkGroup;// 验收小组

    private String debugUser;// 调试验收人

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate debugDate;// 调试验收人验收日期

    private String checkRespUser;// 验收负责人

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkRespDate;// 验收负责人验收日期

    private String receiveUser;// 接收使用人

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate receiveDate;// 接收使用人验收日期

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

    public String getEquiptName() {
        return equiptName;
    }

    public void setEquiptName(String equiptName) {
        this.equiptName = equiptName;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getPurchLoc() {
        return purchLoc;
    }

    public void setPurchLoc(String purchLoc) {
        this.purchLoc = purchLoc;
    }

    public Double getPurchQty() {
        return purchQty;
    }

    public void setPurchQty(Double purchQty) {
        this.purchQty = purchQty;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getPurchAttachName() {
        return purchAttachName;
    }

    public void setPurchAttachName(String purchAttachName) {
        this.purchAttachName = purchAttachName;
    }

    public Double getFee() {
        return fee;
    }

    public void setFee(Double fee) {
        this.fee = fee;
    }

    public String getPayWay() {
        return payWay;
    }

    public void setPayWay(String payWay) {
        this.payWay = payWay;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getUseDesc() {
        return useDesc;
    }

    public void setUseDesc(String useDesc) {
        this.useDesc = useDesc;
    }

    public String getFactoryNo() {
        return factoryNo;
    }

    public void setFactoryNo(String factoryNo) {
        this.factoryNo = factoryNo;
    }

    public LocalDate getFactoryDate() {
        return factoryDate;
    }

    public void setFactoryDate(LocalDate factoryDate) {
        this.factoryDate = factoryDate;
    }

    public LocalDate getPurchDate() {
        return purchDate;
    }

    public void setPurchDate(LocalDate purchDate) {
        this.purchDate = purchDate;
    }

    public String getContractNo() {
        return contractNo;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getCheckRecord() {
        return checkRecord;
    }

    public void setCheckRecord(String checkRecord) {
        this.checkRecord = checkRecord;
    }

    public String getCheckResult() {
        return checkResult;
    }

    public void setCheckResult(String checkResult) {
        this.checkResult = checkResult;
    }

    public String getCheckGroup() {
        return checkGroup;
    }

    public void setCheckGroup(String checkGroup) {
        this.checkGroup = checkGroup;
    }

    public String getDebugUser() {
        return debugUser;
    }

    public void setDebugUser(String debugUser) {
        this.debugUser = debugUser;
    }

    public LocalDate getDebugDate() {
        return debugDate;
    }

    public void setDebugDate(LocalDate debugDate) {
        this.debugDate = debugDate;
    }

    public String getCheckRespUser() {
        return checkRespUser;
    }

    public void setCheckRespUser(String checkRespUser) {
        this.checkRespUser = checkRespUser;
    }

    public LocalDate getCheckRespDate() {
        return checkRespDate;
    }

    public void setCheckRespDate(LocalDate checkRespDate) {
        this.checkRespDate = checkRespDate;
    }

    public String getReceiveUser() {
        return receiveUser;
    }

    public void setReceiveUser(String receiveUser) {
        this.receiveUser = receiveUser;
    }

    public LocalDate getReceiveDate() {
        return receiveDate;
    }

    public void setReceiveDate(LocalDate receiveDate) {
        this.receiveDate = receiveDate;
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
