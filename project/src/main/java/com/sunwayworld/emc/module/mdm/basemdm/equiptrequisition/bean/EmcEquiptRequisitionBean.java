package com.sunwayworld.emc.module.mdm.basemdm.equiptrequisition.bean;

import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;
import com.alibaba.fastjson.annotation.JSONField;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.Insertable;
import com.sunwayworld.framework.support.domain.AbstractInsertable;

/**
 * 仪器出入库
 *
 * @author zhw@sunwayworld.com
 * @date 2021-02-04
 */
@Table("T_EMC_EQUIPT_REQUISITION")
public class EmcEquiptRequisitionBean extends AbstractInsertable<Long> implements Insertable<Long> {

    @Transient
    private static final long serialVersionUID = 2619685916716257603L;

    @Id
    private Long id;// 主键
    private Long equiptId;// 仪器编码
    private String rentUserId;//领用人id
    private String rentUser;// 领用人
    private String rentOrg;// 领用部门
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime rentDate;// 领用日期
    private String rentTime;//领用时间
    private String backUserId;//归还人id
    private String backUser;// 归还人
    private String backOrg;// 归还部门
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime backDate;// 归还日期
    private String backTime;//归还时间
    private String remark;// 备注
    private String projIds;//项目id
    private String projNos;//项目编码
    private String createdById;// 制单人编码
    private String createdByName;// 制单人名称
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;// 制单时间
    private String createdByOrgId;// 制单人单位编码
    private String createdByOrgName;// 制单人单位名称
    private String isBack;//是否归还
    private String equiptManagerId;//设备管理员id
    private String equiptManager;//设备管理员
    private String rentStatus;//领取时状态
    private String backStatus;//归还时状态

    private Long sampleId; // 采样单ID
    private String calibrationId; // 仪器校准ID
    private String confirmUserId; // 确认人id
    private String confirmUserName; // 确认人名称
    private String accessory; // 附件
    private String humidity; // 湿度
    private String temperature; // 温度

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getEquiptId() {
        return equiptId;
    }

    public void setEquiptId(Long equiptId) {
        this.equiptId = equiptId;
    }

    public String getRentUserId() {
        return rentUserId;
    }

    public void setRentUserId(String rentUserId) {
        this.rentUserId = rentUserId;
    }

    public String getRentUser() {
        return rentUser;
    }

    public void setRentUser(String rentUser) {
        this.rentUser = rentUser;
    }

    public String getRentOrg() {
        return rentOrg;
    }

    public void setRentOrg(String rentOrg) {
        this.rentOrg = rentOrg;
    }

    public LocalDateTime getRentDate() {
        return rentDate;
    }

    public void setRentDate(LocalDateTime rentDate) {
        this.rentDate = rentDate;
    }

    public String getRentTime() {
        return rentTime;
    }

    public void setRentTime(String rentTime) {
        this.rentTime = rentTime;
    }

    public String getBackUserId() {
        return backUserId;
    }

    public void setBackUserId(String backUserId) {
        this.backUserId = backUserId;
    }

    public String getBackUser() {
        return backUser;
    }

    public void setBackUser(String backUser) {
        this.backUser = backUser;
    }

    public String getBackOrg() {
        return backOrg;
    }

    public void setBackOrg(String backOrg) {
        this.backOrg = backOrg;
    }

    public LocalDateTime getBackDate() {
        return backDate;
    }

    public void setBackDate(LocalDateTime backDate) {
        this.backDate = backDate;
    }

    public String getBackTime() {
        return backTime;
    }

    public void setBackTime(String backTime) {
        this.backTime = backTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getProjIds() {
        return projIds;
    }

    public void setProjIds(String projIds) {
        this.projIds = projIds;
    }

    public String getProjNos() {
        return projNos;
    }

    public void setProjNos(String projNos) {
        this.projNos = projNos;
    }

    public String getIsBack() {
        return isBack;
    }

    public void setIsBack(String isBack) {
        this.isBack = isBack;
    }

    public String getEquiptManagerId() {
        return equiptManagerId;
    }

    public void setEquiptManagerId(String equiptManagerId) {
        this.equiptManagerId = equiptManagerId;
    }

    public String getEquiptManager() {
        return equiptManager;
    }

    public void setEquiptManager(String equiptManager) {
        this.equiptManager = equiptManager;
    }

    public String getRentStatus() {
        return rentStatus;
    }

    public void setRentStatus(String rentStatus) {
        this.rentStatus = rentStatus;
    }

    public String getBackStatus() {
        return backStatus;
    }

    public void setBackStatus(String backStatus) {
        this.backStatus = backStatus;
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

    public Long getSampleId() {
        return sampleId;
    }

    public void setSampleId(Long sampleId) {
        this.sampleId = sampleId;
    }

    public String getCalibrationId() {
        return calibrationId;
    }

    public void setCalibrationId(String calibrationId) {
        this.calibrationId = calibrationId;
    }

    public String getConfirmUserId() {
        return confirmUserId;
    }

    public void setConfirmUserId(String confirmUserId) {
        this.confirmUserId = confirmUserId;
    }

    public String getConfirmUserName() {
        return confirmUserName;
    }

    public void setConfirmUserName(String confirmUserName) {
        this.confirmUserName = confirmUserName;
    }

    public String getAccessory() {
        return accessory;
    }

    public void setAccessory(String accessory) {
        this.accessory = accessory;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }
}
