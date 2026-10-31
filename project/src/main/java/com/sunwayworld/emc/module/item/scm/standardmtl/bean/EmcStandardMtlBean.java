package com.sunwayworld.emc.module.item.scm.standardmtl.bean;

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
 * 标准物质表
 *
 * @author qianc@sunwayworld.com 2019年11月12日
 */
@Table("T_EMC_STANDARD_MTL")
public class EmcStandardMtlBean extends AbstractAuditable<Long> implements Auditable<Long>{

    @Transient
    private static final long serialVersionUID = -6887526271094201685L;

    @Id
    private Long id;//主键

    private String mtlName;//标准物质名称

    private String concent;//浓度

    private Double quantity;//数量（支）

    private Double total; //合计

    private String standNo;//国家标准编号

    private String manufacturer;//制造单位

    private String remark;//备注

    private String lvl;//等级

    private String specification;//规格

    private String contentRange;//含量范围

    private String testMethod;//检测方法

    private String techIndex;//技术指标

    private String checkRecord;//验收记录

    private String checkResult;//验收结果

    private String checkUser;//验收人

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkDate;//验收人验收日期

    private String checkRespUser;//验收负责人

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkRespDate;//验收负责人验收日期

    private String auditUser;//审批人

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate auditDate;//审批人人验收日期

    private String createdById;//制单人编码

    private String createdByName;//制单人名称

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;//制单时间

    private String createdByOrgId;//制单人单位编码

    private String createdByOrgName;//制单人单位名称

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }


    public String getMtlName() {
        return mtlName;
    }

    public void setMtlName(String mtlName) {
        this.mtlName = mtlName;
    }

    public String getConcent() {
        return concent;
    }

    public void setConcent(String concent) {
        this.concent = concent;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public String getStandNo() {
        return standNo;
    }

    public void setStandNo(String standNo) {
        this.standNo = standNo;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getLvl() {
        return lvl;
    }

    public void setLvl(String lvl) {
        this.lvl = lvl;
    }

    public String getSpecification() {
        return specification;
    }

    public void setSpecification(String specification) {
        this.specification = specification;
    }

    public String getContentRange() {
        return contentRange;
    }

    public void setContentRange(String contentRange) {
        this.contentRange = contentRange;
    }

    public String getTestMethod() {
        return testMethod;
    }

    public void setTestMethod(String testMethod) {
        this.testMethod = testMethod;
    }

    public String getTechIndex() {
        return techIndex;
    }

    public void setTechIndex(String techIndex) {
        this.techIndex = techIndex;
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

    public String getCheckUser() {
        return checkUser;
    }

    public void setCheckUser(String checkUser) {
        this.checkUser = checkUser;
    }

    public LocalDate getCheckDate() {
        return checkDate;
    }

    public void setCheckDate(LocalDate checkDate) {
        this.checkDate = checkDate;
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

    public String getAuditUser() {
        return auditUser;
    }

    public void setAuditUser(String auditUser) {
        this.auditUser = auditUser;
    }

    public LocalDate getAuditDate() {
        return auditDate;
    }

    public void setAuditDate(LocalDate auditDate) {
        this.auditDate = auditDate;
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

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

}
