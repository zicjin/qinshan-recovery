package com.sunwayworld.emc.module.mdm.basemdm.vendor.bean;

import java.time.LocalDateTime;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.format.annotation.DateTimeFormat;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractPersistable;
import com.sunwayworld.framework.support.domain.Insertable;


/**
 * 供应物品记录表
 *
 * @author qianc@sunwayworld.com 2019年10月28日
 */
@Table("T_EMC_VENDOR_MTL")
public class EmcVendorMtlBean extends AbstractPersistable<Long> implements Insertable<Long>{

    @Transient
    private static final long serialVersionUID = 1219675115051192146L;

    @Id
    private Long id;//主键

    private Long vendorId;//厂商ID

    private Long orderNo;//排序码

    private String supplyMtlName;//供应物品名称

    private String supplyCategory;//供应品类别

    private String supplySpec;//供应品规格

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime supplyTime;//供应物品时间

    private Double supplyQty;//数量

    private Double totalAmount;//总价

    private String qualityRecord;//质量记录

    private String auditUser;//审核人

    private String authorizeUser;//批准人

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
    public Long getVendorId() {
        return vendorId;
    }

    public void setVendorId(Long vendorId) {
        this.vendorId = vendorId;
    }
    public Long getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Long orderNo) {
        this.orderNo = orderNo;
    }
    public String getSupplyMtlName() {
        return supplyMtlName;
    }

    public void setSupplyMtlName(String supplyMtlName) {
        this.supplyMtlName = supplyMtlName;
    }
    public String getSupplyCategory() {
        return supplyCategory;
    }

    public void setSupplyCategory(String supplyCategory) {
        this.supplyCategory = supplyCategory;
    }
    public String getSupplySpec() {
        return supplySpec;
    }

    public void setSupplySpec(String supplySpec) {
        this.supplySpec = supplySpec;
    }
    public LocalDateTime getSupplyTime() {
        return supplyTime;
    }

    public void setSupplyTime(LocalDateTime supplyTime) {
        this.supplyTime = supplyTime;
    }
    public Double getSupplyQty() {
        return supplyQty;
    }

    public void setSupplyQty(Double supplyQty) {
        this.supplyQty = supplyQty;
    }
    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }
    public String getQualityRecord() {
        return qualityRecord;
    }

    public void setQualityRecord(String qualityRecord) {
        this.qualityRecord = qualityRecord;
    }
    public String getAuditUser() {
        return auditUser;
    }

    public void setAuditUser(String auditUser) {
        this.auditUser = auditUser;
    }
    public String getAuthorizeUser() {
        return authorizeUser;
    }

    public void setAuthorizeUser(String authorizeUser) {
        this.authorizeUser = authorizeUser;
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
