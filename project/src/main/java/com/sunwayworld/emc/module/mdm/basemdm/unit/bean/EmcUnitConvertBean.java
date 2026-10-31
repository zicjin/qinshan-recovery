package com.sunwayworld.emc.module.mdm.basemdm.unit.bean;

import java.time.LocalDateTime;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.format.annotation.DateTimeFormat;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.support.domain.Insertable;

/**
 * 单位转换因子表
 *
 * @author yuanhh@sunwayworld.com 2019年9月9日
 */
@Table("T_EMC_UNITCONVERT")
public class EmcUnitConvertBean extends AbstractInsertable<Long> implements Insertable<Long> {

    private static final long serialVersionUID = 1847260967138492882L;
    @Id
    private Long id; // 单位转换编码<主键>
    private String unitId; // 单位编码
    private Double convertFactor; // 转换因子
    private String convertUnitId; // 转换单位ID

    private String createdById;
    private String createdByName;
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;
    private String createdByOrgId;
    private String createdByOrgName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getUnitId() {
        return unitId;
    }

    public void setUnitId(String unitId) {
        this.unitId = unitId;
    }

    public Double getConvertFactor() {
        return convertFactor;
    }

    public void setConvertFactor(Double convertFactor) {
        this.convertFactor = convertFactor;
    }

    public String getConvertUnitId() {
        return convertUnitId;
    }

    public void setConvertUnitId(String convertUnitId) {
        this.convertUnitId = convertUnitId;
    }

}
