package com.sunwayworld.emc.module.item.car.bean;

import java.time.LocalDateTime;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.format.annotation.DateTimeFormat;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractAuditable;
import com.sunwayworld.framework.support.domain.Insertable;

/**
 * 用车申请表
 * @author cuikx@sunwayworld.com
 * @date 2019-11-12
 */

@Table("T_EMC_CAR")
public class EmcCarBean extends AbstractAuditable<Long> implements Insertable<Long> {

    @Transient
    private static final long serialVersionUID = 5632483012303031194L;

    @Id
    private Long id;// 主键

    private String useDeptId;// 用车站室Id

    private String useDeptName;// 用车站室name

    private String carNum;// 车牌号

    private String passengerIds;// 随行人员Ids

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime leaveTime;// 预计出发时间

    private String useReason;// 用车事由

    private String destination;// 目的地

    private String pickCarStatus;// 取车状态

    private String remark;// 备注

    private String driverId;// 驾驶人id

    private String driverName;// 驾驶人name

    private String vehicleType;//车辆类型

    private String  vehicleBrand;//车辆品牌



    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime actualUseTime;// 实际用车开始时间

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime actualRebackTime;// 实际用车结束时间

    private String beforeMile;// 行驶开始里程

    private String afterMile;// 行驶结束里程

    private String oiling;// 是否加油

    private String oil;// 加油量

    private String oilCardNo;// 油卡号

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime oilingTime;// 加油时间

    private String oilingMile;// 加油时里程

    private String createdById;// 制单人编码

    private String createdByName;// 制单人名称

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;// 制单时间

    private String createdByOrgId;// 制单人单位编码

    private String createdByOrgName;// 制单人单位名称

    private String applyUserId;// 申请人id

    private String applyUserName;// 申请人名字

    private String passengerNames;// 随行人员names

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getPassengerIds() {
        return passengerIds;
    }

    public void setPassengerIds(String passengerIds) {
        this.passengerIds = passengerIds;
    }

    public LocalDateTime getLeaveTime() {
        return leaveTime;
    }

    public void setLeaveTime(LocalDateTime leaveTime) {
        this.leaveTime = leaveTime;
    }

    public String getUseReason() {
        return useReason;
    }

    public void setUseReason(String useReason) {
        this.useReason = useReason;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getPickCarStatus() {
        return pickCarStatus;
    }

    public void setPickCarStatus(String pickCarStatus) {
        this.pickCarStatus = pickCarStatus;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public LocalDateTime getActualUseTime() {
        return actualUseTime;
    }

    public void setActualUseTime(LocalDateTime actualUseTime) {
        this.actualUseTime = actualUseTime;
    }

    public LocalDateTime getActualRebackTime() {
        return actualRebackTime;
    }

    public void setActualRebackTime(LocalDateTime actualRebackTime) {
        this.actualRebackTime = actualRebackTime;
    }

    public String getBeforeMile() {
        return beforeMile;
    }

    public void setBeforeMile(String beforeMile) {
        this.beforeMile = beforeMile;
    }

    public String getAfterMile() {
        return afterMile;
    }

    public void setAfterMile(String afterMile) {
        this.afterMile = afterMile;
    }

    public String getOiling() {
        return oiling;
    }

    public void setOiling(String oiling) {
        this.oiling = oiling;
    }

    public String getOil() {
        return oil;
    }

    public void setOil(String oil) {
        this.oil = oil;
    }

    public String getOilCardNo() {
        return oilCardNo;
    }

    public void setOilCardNo(String oilCardNo) {
        this.oilCardNo = oilCardNo;
    }

    public LocalDateTime getOilingTime() {
        return oilingTime;
    }

    public void setOilingTime(LocalDateTime oilingTime) {
        this.oilingTime = oilingTime;
    }

    public String getOilingMile() {
        return oilingMile;
    }

    public void setOilingMile(String oilingMile) {
        this.oilingMile = oilingMile;
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

    public String getApplyUserId() {
        return applyUserId;
    }

    public void setApplyUserId(String applyUserId) {
        this.applyUserId = applyUserId;
    }

    public String getApplyUserName() {
        return applyUserName;
    }

    public void setApplyUserName(String applyUserName) {
        this.applyUserName = applyUserName;
    }

    public String getPassengerNames() {
        return passengerNames;
    }

    public void setPassengerNames(String passengerNames) {
        this.passengerNames = passengerNames;
    }

    public String getCarNum() {
        return carNum;
    }

    public void setCarNum(String carNum) {
        this.carNum = carNum;
    }

    public String getUseDeptId() {
        return useDeptId;
    }

    public void setUseDeptId(String useDeptId) {
        this.useDeptId = useDeptId;
    }

    public String getUseDeptName() {
        return useDeptName;
    }

    public void setUseDeptName(String useDeptName) {
        this.useDeptName = useDeptName;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }
    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getVehicleBrand() {
        return vehicleBrand;
    }

    public void setVehicleBrand(String vehicleBrand) {
        this.vehicleBrand = vehicleBrand;
    }


}
