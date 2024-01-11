package com.sunwayworld.emc.module.mdm.basemdm.mtl.bean;

import java.time.LocalDateTime;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.format.annotation.DateTimeFormat;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.support.domain.AbstractPersistable;
import com.sunwayworld.framework.support.domain.Insertable;

/**
 * @author baogx@sunwayworld.com
 *
 * @date 2019年9月19日 下午4:00:07
 */
@Table("T_EMC_MTL")
public class EmcMtlBean extends AbstractPersistable<String> implements Insertable<String>{

    private static final long serialVersionUID = -6003931017931268308L;

    @Id
    private String id; // 材料编码<主键>

    private String mtlLabNo; // 实验室编号

    private String mtlName; // 材料名称

    private String specification; // 规格型号

    private Double stockQty; // 库存数量

    private Long mtlCategoryId; // 材料类型

    private String harmfulness; // 危害性

    private String createdById; // 制单人编码

    private String createdByName; // 制单人名称

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;// 制单时间

    private String createdByOrgId;// 制单人单位编码

    private String createdByOrgName;// 制单人单位名称

    private String orgId;//所属单位编码

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getMtlLabNo() {
        return mtlLabNo;
    }

    public void setMtlLabNo(String mtlLabNo) {
        this.mtlLabNo = mtlLabNo;
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

    public Long getMtlCategoryId() {
        return mtlCategoryId;
    }

    public void setMtlCategoryId(Long mtlCategoryId) {
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

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}
}
