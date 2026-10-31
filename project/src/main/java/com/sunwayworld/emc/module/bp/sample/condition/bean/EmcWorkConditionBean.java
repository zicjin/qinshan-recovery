package com.sunwayworld.emc.module.bp.sample.condition.bean;

import java.time.LocalDateTime;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.format.annotation.DateTimeFormat;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.support.domain.Insertable;

/**
 * 现场工况调查
 *
 */
@Table("T_EMC_WORK_CONDITION")
public class EmcWorkConditionBean extends AbstractInsertable<Long> implements Insertable<Long> {

	@Transient
	private static final long serialVersionUID = -4838015098633741685L;

    @Id
    private Long id;// 主键
    private Long projId; //项目ID
    private String gasSource; //废气排放源名称
    private String gasCraft; //废气处理设施工艺
    private String gasVentQty; //废气排放口数量
    private String gasVentNormal; //废气排放口是否规范化
    private String gasMonitor; //废气是否有在线监测设施
    private String gasMonitorItem; //废气在线监测项目
    private String gasMonitorSupplier; //废气在线监测设备运营商
    private String gasStd; //废气执行标准
    private String waterSource; //废水排放源名称
    private String waterCraft; //废水处理工艺
    private String waterVentNormal; //废水排放口是否规范化
    private String waterMonitor; //废水是否有在线监测设施
    private String waterOutDirection; //废水排放去向
    private String waterOutRegular; //废水排放规律
    private String waterMonitorSupplier; //废水在线监测设备运营商
    private String waterStd; //废水执行标准
    private String swName; //固体废物名称
    private String swSource; //固体废物来源
    private String swQty; //固体废物数量
    private String swState; //固体废物性状
    private String swRegular; //固体废物产生规律
    private String swHandle; //固体废物贮存及处理情况

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

    public Long getProjId() {
        return projId;
    }

    public void setProjId(Long projId) {
        this.projId = projId;
    }

    public String getGasSource() {
        return gasSource;
    }

    public void setGasSource(String gasSource) {
        this.gasSource = gasSource;
    }

    public String getGasCraft() {
        return gasCraft;
    }

    public void setGasCraft(String gasCraft) {
        this.gasCraft = gasCraft;
    }

    public String getGasVentQty() {
        return gasVentQty;
    }

    public void setGasVentQty(String gasVentQty) {
        this.gasVentQty = gasVentQty;
    }

    public String getGasVentNormal() {
        return gasVentNormal;
    }

    public void setGasVentNormal(String gasVentNormal) {
        this.gasVentNormal = gasVentNormal;
    }

    public String getGasMonitor() {
        return gasMonitor;
    }

    public void setGasMonitor(String gasMonitor) {
        this.gasMonitor = gasMonitor;
    }

    public String getGasMonitorItem() {
        return gasMonitorItem;
    }

    public void setGasMonitorItem(String gasMonitorItem) {
        this.gasMonitorItem = gasMonitorItem;
    }

    public String getGasMonitorSupplier() {
        return gasMonitorSupplier;
    }

    public void setGasMonitorSupplier(String gasMonitorSupplier) {
        this.gasMonitorSupplier = gasMonitorSupplier;
    }

    public String getGasStd() {
        return gasStd;
    }

    public void setGasStd(String gasStd) {
        this.gasStd = gasStd;
    }

    public String getWaterSource() {
        return waterSource;
    }

    public void setWaterSource(String waterSource) {
        this.waterSource = waterSource;
    }

    public String getWaterCraft() {
        return waterCraft;
    }

    public void setWaterCraft(String waterCraft) {
        this.waterCraft = waterCraft;
    }

    public String getWaterVentNormal() {
        return waterVentNormal;
    }

    public void setWaterVentNormal(String waterVentNormal) {
        this.waterVentNormal = waterVentNormal;
    }

    public String getWaterMonitor() {
        return waterMonitor;
    }

    public void setWaterMonitor(String waterMonitor) {
        this.waterMonitor = waterMonitor;
    }

    public String getWaterOutDirection() {
        return waterOutDirection;
    }

    public void setWaterOutDirection(String waterOutDirection) {
        this.waterOutDirection = waterOutDirection;
    }

    public String getWaterOutRegular() {
        return waterOutRegular;
    }

    public void setWaterOutRegular(String waterOutRegular) {
        this.waterOutRegular = waterOutRegular;
    }

    public String getWaterMonitorSupplier() {
        return waterMonitorSupplier;
    }

    public void setWaterMonitorSupplier(String waterMonitorSupplier) {
        this.waterMonitorSupplier = waterMonitorSupplier;
    }

    public String getWaterStd() {
        return waterStd;
    }

    public void setWaterStd(String waterStd) {
        this.waterStd = waterStd;
    }

    public String getSwName() {
        return swName;
    }

    public void setSwName(String swName) {
        this.swName = swName;
    }

    public String getSwSource() {
        return swSource;
    }

    public void setSwSource(String swSource) {
        this.swSource = swSource;
    }

    public String getSwQty() {
        return swQty;
    }

    public void setSwQty(String swQty) {
        this.swQty = swQty;
    }

    public String getSwState() {
        return swState;
    }

    public void setSwState(String swState) {
        this.swState = swState;
    }

    public String getSwRegular() {
        return swRegular;
    }

    public void setSwRegular(String swRegular) {
        this.swRegular = swRegular;
    }

    public String getSwHandle() {
        return swHandle;
    }

    public void setSwHandle(String swHandle) {
        this.swHandle = swHandle;
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
