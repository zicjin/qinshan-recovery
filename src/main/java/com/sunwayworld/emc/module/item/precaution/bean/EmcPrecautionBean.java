package com.sunwayworld.emc.module.item.precaution.bean;

import java.time.LocalDateTime;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.format.annotation.DateTimeFormat;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractAuditable;
import com.sunwayworld.framework.support.domain.Insertable;

/**
 * 预防措施计划
 * @author zhangft@sunwayworld.com
 * @date 2019-11-25
 */

@Table("T_EMC_PRECAUTION")
public class EmcPrecautionBean extends AbstractAuditable<Long> implements Insertable<Long> {

    @Transient
    private static final long serialVersionUID = 7131949795598824746L;

    @Id
    private Long id;// 申请编码

    private String precauteCode;// 预防措施编码

    private String source;// 来源

    private String sourceNo;// 来源编号

    private String implementOrgName;// 实施部门

    private String implementLeader;// 实施负责人

    private String partakeUser;// 参加人员

    private String reasion;// 原因

    private String precaution;// 拟采取预防措施

    private String verifyMethod;// 拟采取验证方法

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime completeDate;// 预计完成时间

    private String verifyUser;// 验证人

    private String createdById;// 制单人编码

    private String createdByName;// 制单人名称

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
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

    public String getPrecauteCode() {
        return precauteCode;
    }

    public void setPrecauteCode(String precauteCode) {
        this.precauteCode = precauteCode;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSourceNo() {
        return sourceNo;
    }

    public void setSourceNo(String sourceNo) {
        this.sourceNo = sourceNo;
    }

    public String getImplementOrgName() {
        return implementOrgName;
    }

    public void setImplementOrgName(String implementOrgName) {
        this.implementOrgName = implementOrgName;
    }

    public String getImplementLeader() {
        return implementLeader;
    }

    public void setImplementLeader(String implementLeader) {
        this.implementLeader = implementLeader;
    }

    public String getPartakeUser() {
        return partakeUser;
    }

    public void setPartakeUser(String partakeUser) {
        this.partakeUser = partakeUser;
    }

    public String getReasion() {
        return reasion;
    }

    public void setReasion(String reasion) {
        this.reasion = reasion;
    }

    public String getPrecaution() {
        return precaution;
    }

    public void setPrecaution(String precaution) {
        this.precaution = precaution;
    }

    public String getVerifyMethod() {
        return verifyMethod;
    }

    public void setVerifyMethod(String verifyMethod) {
        this.verifyMethod = verifyMethod;
    }

    public LocalDateTime getCompleteDate() {
        return completeDate;
    }

    public void setCompleteDate(LocalDateTime completeDate) {
        this.completeDate = completeDate;
    }

    public String getVerifyUser() {
        return verifyUser;
    }

    public void setVerifyUser(String verifyUser) {
        this.verifyUser = verifyUser;
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
