package com.sunwayworld.emc.module.bp.result.curve.bean;

import java.time.LocalDateTime;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.format.annotation.DateTimeFormat;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.support.domain.Insertable;

/**
 * 仪器曲线配置
 *
 * @author zhangheng@sunwayworld.com@sunwayworld.com
 * @date 2020-12-01
 */
@Table("T_EMC_EQUIPT_CURVE_CONFIG")
public class EmcEquiptCurveConfigBean extends AbstractInsertable<Long> implements Insertable<Long> {

    @Transient
    private static final long serialVersionUID = -1433358807268993890L;

    @Id
    private Long id;// 主键
    private Long equiptCurveId;// 仪器曲线id
    private String curveConcentration;// 标准曲线浓度
    private String concentrationUnit;// 曲线浓度单位
    private String volume;// 标准使用溶液加入体积
    private String volumeUnit;// 加入体积单位
    private String strength;// 强度
    private String itemName;// 检测项目
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

    public Long getEquiptCurveId() {
        return equiptCurveId;
    }

    public void setEquiptCurveId(Long equiptCurveId) {
        this.equiptCurveId = equiptCurveId;
    }

    public String getCurveConcentration() {
        return curveConcentration;
    }

    public void setCurveConcentration(String curveConcentration) {
        this.curveConcentration = curveConcentration;
    }

    public String getConcentrationUnit() {
        return concentrationUnit;
    }

    public void setConcentrationUnit(String concentrationUnit) {
        this.concentrationUnit = concentrationUnit;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public String getVolumeUnit() {
        return volumeUnit;
    }

    public void setVolumeUnit(String volumeUnit) {
        this.volumeUnit = volumeUnit;
    }

    public String getStrength() {
        return strength;
    }

    public void setStrength(String strength) {
        this.strength = strength;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
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
