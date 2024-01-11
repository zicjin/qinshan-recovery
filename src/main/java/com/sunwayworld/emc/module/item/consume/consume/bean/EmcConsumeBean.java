package com.sunwayworld.emc.module.item.consume.consume.bean;

import java.time.LocalDateTime;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.format.annotation.DateTimeFormat;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractPersistable;
import com.sunwayworld.framework.support.domain.Insertable;

/**
 * @author qianc@sunwayworld.com
 *
 * @date 2019年11月8日
 */
@Table("T_EMC_CONSUME")
public class EmcConsumeBean extends AbstractPersistable<String> implements Insertable<String>{

    @Transient
    private static final long serialVersionUID = 4691812900126226226L;

    @Id
    private String id; // 材料编码<主键>

    private String mtlName; // 材料名称

    private String specification; // 规格型号

    private Double stockQty; // 库存数量

    private String mtlCategoryId; // 材料类型

    private String harmfulness; // 危害性

    private String createdById; // 制单人编码

    private String createdByName; // 制单人名称

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;// 制单时间

    private String createdByOrgId;// 制单人单位编码

    private String createdByOrgName;// 制单人单位名称

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMtlName() {
        return mtlName;
    }

    public void setMtlName(String mtlName) {
        this.mtlName = mtlName;
    }

    public String getSpecification() {
        return specification;
    }

    public void setSpecification(String specification) {
        this.specification = specification;
    }

    public Double getStockQty() {
        return stockQty;
    }

    public void setStockQty(Double stockQty) {
        this.stockQty = stockQty;
    }

    public String getMtlCategoryId() {
        return mtlCategoryId;
    }

    public void setMtlCategoryId(String mtlCategoryId) {
        this.mtlCategoryId = mtlCategoryId;
    }

    public String getHarmfulness() {
        return harmfulness;
    }

    public void setHarmfulness(String harmfulness) {
        this.harmfulness = harmfulness;
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
