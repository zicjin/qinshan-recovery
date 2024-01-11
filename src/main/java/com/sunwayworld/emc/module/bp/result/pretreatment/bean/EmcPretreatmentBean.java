package com.sunwayworld.emc.module.bp.result.pretreatment.bean;

import java.time.LocalDateTime;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.format.annotation.DateTimeFormat;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.support.domain.AbstractPersistable;
import com.sunwayworld.framework.support.domain.Insertable;

/**
 * @author zhengxy@sunwayworld.com 2023年08月14日
 */
@Table("T_EMC_PRETREATMENT")
public class EmcPretreatmentBean extends AbstractPersistable<Long> implements Insertable<Long> {

    @Id
    private Long id;// 主键
    private String orgId; // 单位ID
    private Long projId; //项目ID
    private Long folderId; //点位ID
    private Long orderId; //样品ID
    private Long orderTaskId; //测试ID
    private String purifyStd; //净化内标
    private String purifyStdAdditive; //净化内标添加量
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime purifyStdTime; //净化内标时间
    private String purifyStdUserId; //净化内标操作人ID
    private String purifyStdUserName; //净化内标操作人
    private String extractMethodId; //提取方法
    private String orderDivision; //样品分割
    private String purifyMethodId; //净化方法ID（不需要）
    private String purifyMethod; // 净化方法
    private String injectStd; //进样内标
    private String injectStdAdditive; //进样内标添加量
    private String injectStdVolume; //进样内标定容体积
    private String injectStdUserId; //进样内标操作人ID
    private String injectStdUserName; //进样内标操作人
    private String remark; //备注
    private String status; // 状态

    private String purifyStdUnit; // 净化内标单位
    private String injectStdUnit; // 进样内标单位

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

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public Long getProjId() {
        return projId;
    }

    public void setProjId(Long projId) {
        this.projId = projId;
    }

    public Long getFolderId() {
        return folderId;
    }

    public void setFolderId(Long folderId) {
        this.folderId = folderId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getOrderTaskId() {
        return orderTaskId;
    }

    public void setOrderTaskId(Long orderTaskId) {
        this.orderTaskId = orderTaskId;
    }

    public String getPurifyStd() {
        return purifyStd;
    }

    public void setPurifyStd(String purifyStd) {
        this.purifyStd = purifyStd;
    }

    public String getPurifyStdAdditive() {
        return purifyStdAdditive;
    }

    public void setPurifyStdAdditive(String purifyStdAdditive) {
        this.purifyStdAdditive = purifyStdAdditive;
    }

    public LocalDateTime getPurifyStdTime() {
        return purifyStdTime;
    }

    public void setPurifyStdTime(LocalDateTime purifyStdTime) {
        this.purifyStdTime = purifyStdTime;
    }

    public String getPurifyStdUserId() {
        return purifyStdUserId;
    }

    public void setPurifyStdUserId(String purifyStdUserId) {
        this.purifyStdUserId = purifyStdUserId;
    }

    public String getPurifyStdUserName() {
        return purifyStdUserName;
    }

    public void setPurifyStdUserName(String purifyStdUserName) {
        this.purifyStdUserName = purifyStdUserName;
    }

    public String getExtractMethodId() {
        return extractMethodId;
    }

    public void setExtractMethodId(String extractMethodId) {
        this.extractMethodId = extractMethodId;
    }

    public String getOrderDivision() {
        return orderDivision;
    }

    public void setOrderDivision(String orderDivision) {
        this.orderDivision = orderDivision;
    }

    public String getPurifyMethodId() {
        return purifyMethodId;
    }

    public void setPurifyMethodId(String purifyMethodId) {
        this.purifyMethodId = purifyMethodId;
    }

    public String getPurifyMethod() {
        return purifyMethod;
    }

    public void setPurifyMethod(String purifyMethod) {
        this.purifyMethod = purifyMethod;
    }

    public String getInjectStd() {
        return injectStd;
    }

    public void setInjectStd(String injectStd) {
        this.injectStd = injectStd;
    }

    public String getInjectStdAdditive() {
        return injectStdAdditive;
    }

    public void setInjectStdAdditive(String injectStdAdditive) {
        this.injectStdAdditive = injectStdAdditive;
    }

    public String getInjectStdVolume() {
        return injectStdVolume;
    }

    public void setInjectStdVolume(String injectStdVolume) {
        this.injectStdVolume = injectStdVolume;
    }

    public String getInjectStdUserId() {
        return injectStdUserId;
    }

    public void setInjectStdUserId(String injectStdUserId) {
        this.injectStdUserId = injectStdUserId;
    }

    public String getInjectStdUserName() {
        return injectStdUserName;
    }

    public void setInjectStdUserName(String injectStdUserName) {
        this.injectStdUserName = injectStdUserName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPurifyStdUnit() {
        return purifyStdUnit;
    }

    public void setPurifyStdUnit(String purifyStdUnit) {
        this.purifyStdUnit = purifyStdUnit;
    }

    public String getInjectStdUnit() {
        return injectStdUnit;
    }

    public void setInjectStdUnit(String injectStdUnit) {
        this.injectStdUnit = injectStdUnit;
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
