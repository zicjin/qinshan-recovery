package com.sunwayworld.emc.module.mdm.basemdm.envrecord.bean;

import java.time.LocalDateTime;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.format.annotation.DateTimeFormat;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.support.domain.Insertable;

/**
 * 环境记录
 *
 * @author zhangft@sunwayworld.com 2019/9/4
 *
 */
@Table("T_EMC_ENV_RECORD")
public class EmcEnvRecordBean extends AbstractInsertable<String>implements Insertable<String> {

    @Transient
    private static final long serialVersionUID = 3546429108320440981L;

    @Id
    private String id;

    private String monitorCompany; // 监测公司

    private String orgId; // 部门编码

    private String orgName; // 部门

    private String room; // 房间

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime monitorDate; // 日期

    private String monitorTime; // 时间

    private String temperature; // 温度

    private String humidity; // 湿度

    private String pressure; // 气压

    private String recordUserId; // 记录人编码

    private String recordUserName; // 记录人

    private String standardTemperature; // 温度标准值

    private String standardHumidity; // 湿度标准值

    private String thermometerNo; // 干湿温度计编号

    private String remark; // 备注

    private String createdById; // 制单人编码

    private String createdByName; // 制单人名称

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime; // 制单时间

    private String createdByOrgId; // 制单人单位编码

    private String createdByOrgName; // 制单人单位名称


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMonitorCompany() {
        return monitorCompany;
    }

    public void setMonitorCompany(String monitorCompany) {
        this.monitorCompany = monitorCompany;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public LocalDateTime getMonitorDate() {
        return monitorDate;
    }

    public void setMonitorDate(LocalDateTime monitorDate) {
        this.monitorDate = monitorDate;
    }

    public String getMonitorTime() {
        return monitorTime;
    }

    public void setMonitorTime(String monitorTime) {
        this.monitorTime = monitorTime;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getPressure() {
        return pressure;
    }

    public void setPressure(String pressure) {
        this.pressure = pressure;
    }

    public String getRecordUserId() {
        return recordUserId;
    }

    public void setRecordUserId(String recordUserId) {
        this.recordUserId = recordUserId;
    }

    public String getRecordUserName() {
        return recordUserName;
    }

    public void setRecordUserName(String recordUserName) {
        this.recordUserName = recordUserName;
    }

    public String getStandardTemperature() {
        return standardTemperature;
    }

    public void setStandardTemperature(String standardTemperature) {
        this.standardTemperature = standardTemperature;
    }

    public String getStandardHumidity() {
        return standardHumidity;
    }

    public void setStandardHumidity(String standardHumidity) {
        this.standardHumidity = standardHumidity;
    }

    public String getThermometerNo() {
        return thermometerNo;
    }

    public void setThermometerNo(String thermometerNo) {
        this.thermometerNo = thermometerNo;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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
