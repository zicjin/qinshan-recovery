package com.sunwayworld.emc.module.bp.info.infodata.bean;

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
 * 综合信息数据分析
 *
 * @author qianc@sunwayworld.com 2019年11月25日
 */
@Table("T_EMC_INFO_DATA_ITEM")
public class EmcInfoDataItemBean extends AbstractPersistable<Long> implements Insertable<Long>{

    @Transient
    private static final long serialVersionUID = 32533797815211047L;

    @Id
    private Long id;//主键

    private Long infoConfigId;//综合信息配置id

    private Long infoDataId;//综合信息数据id

    private String monitorPointName;//监测点名称

    private String itemName;//分析项名称

    private String result;//分析项值

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate monitorDate;//监测日期

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


    public Long getInfoConfigId() {
        return infoConfigId;
    }

    public void setInfoConfigId(Long infoConfigId) {
        this.infoConfigId = infoConfigId;
    }

    public String getMonitorPointName() {
        return monitorPointName;
    }

    public void setMonitorPointName(String monitorPointName) {
        this.monitorPointName = monitorPointName;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public LocalDate getMonitorDate() {
        return monitorDate;
    }

    public void setMonitorDate(LocalDate monitorDate) {
        this.monitorDate = monitorDate;
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

    public Long getInfoDataId() {
        return infoDataId;
    }

    public void setInfoDataId(Long infoDataId) {
        this.infoDataId = infoDataId;
    }


}
