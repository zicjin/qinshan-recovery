package com.sunwayworld.emc.module.mdm.basemdm.fixedasset.bean;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.format.annotation.DateTimeFormat;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractPersistable;
import com.sunwayworld.framework.support.domain.Insertable;


/**
 * 固定资产管理
 * @author cuikx@sunwayworld.com
 * @date 2019-11-25
 */

@Table("T_EMC_FIXEDASSET")
public class EmcFixedassetBean extends AbstractPersistable<Long> implements Insertable<Long>{

    @Transient
    private static final long serialVersionUID = -7768780149773554338L;

    @Id
    private Long id;//主键

    private String assetNo;//资产编号

    private String assetCategory;//资产类型

    private String assetName;//资产名称

    private String model;//型号

    private Long quantity;//数量

    private Double price;//单价金额

    private Double totalAmount;//总价金额

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDate purchaseTime;//购入时间

    private String factoryNo;//出厂编号

    private String responsityOrgId;//责任科室Id

    private String responsityOrgName;//责任科室name

    private String useOrgId;//使用科室Id

    private String useOrgName;//使用科室name

    private String useModify;//使用变更

    private String storePlace;//存放地点

    private String remark;//备注

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



    public String getAssetNo() {
        return assetNo;
    }

    public void setAssetNo(String assetNo) {
        this.assetNo = assetNo;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public String getAssetCategory() {
        return assetCategory;
    }

    public void setAssetCategory(String assetCategory) {
        this.assetCategory = assetCategory;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public LocalDate getPurchaseTime() {
        return purchaseTime;
    }

    public void setPurchaseTime(LocalDate purchaseTime) {
        this.purchaseTime = purchaseTime;
    }

    public String getFactoryNo() {
        return factoryNo;
    }

    public void setFactoryNo(String factoryNo) {
        this.factoryNo = factoryNo;
    }

    public String getResponsityOrgId() {
        return responsityOrgId;
    }

    public void setResponsityOrgId(String responsityOrgId) {
        this.responsityOrgId = responsityOrgId;
    }

    public String getUseOrgId() {
        return useOrgId;
    }

    public void setUseOrgId(String useOrgId) {
        this.useOrgId = useOrgId;
    }

    public String getUseModify() {
        return useModify;
    }

    public void setUseModify(String useModify) {
        this.useModify = useModify;
    }

    public String getStorePlace() {
        return storePlace;
    }

    public void setStorePlace(String storePlace) {
        this.storePlace = storePlace;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getResponsityOrgName() {
        return responsityOrgName;
    }

    public void setResponsityOrgName(String responsityOrgName) {
        this.responsityOrgName = responsityOrgName;
    }

    public String getUseOrgName() {
        return useOrgName;
    }

    public void setUseOrgName(String useOrgName) {
        this.useOrgName = useOrgName;
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
