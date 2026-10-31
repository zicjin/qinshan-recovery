package com.sunwayworld.emc.module.mdm.bizmdm.client.bean;

import java.time.LocalDateTime;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.format.annotation.DateTimeFormat;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractPersistable;
import com.sunwayworld.framework.support.domain.Insertable;

/**
 * 客户
 *
 * @author magh@sunwayworld.com
 * @date 2019-10-19
 */

@Table("T_EMC_CLIENT")
public class EmcClientBean extends AbstractPersistable<Long> implements Insertable<Long> {

    @Transient
    private static final long serialVersionUID = -9074471364857698769L;

    @Id
    private Long id;// 主键

    private String clientNo;// 客户编码

    private String clientName;// 客户名称

    private String clientCategory;// 客户类别

    private String clientSCategory;// 客户小类

    private Long areaId;// 区域编码

    private String address;// 地址

    private String phone;// 电话

    private String website;// 网址

    private String mail;// 邮政信箱

    private String scale;// 企业规模

    private String creditCode;// 统一社会信用代码

    private String longitude;// 经度

    private String latitude;// 纬度

    private Long addIndex; // 添加次数

    private String remark;// 备注

    private String createdById;// 制单人编码

    private String createdByName;// 制单人名称

    /**
     * 江苏新锐新增字段
     */
    private String chargePersonCode;// 负责人编码
    private String chargePersonName;// 负责人名称

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;// 制单时间

    private String createdByOrgId;// 制单人单位编码

    private String createdByOrgName;// 制单人单位名称

    private String reportEditUserId;//报告编制人ID(江苏新锐)

    private String reportEditUserName;//报告编制人(江苏新锐)

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClientNo() {
        return clientNo;
    }

    public void setClientNo(String clientNo) {
        this.clientNo = clientNo;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientCategory() {
        return clientCategory;
    }

    public void setClientCategory(String clientCategory) {
        this.clientCategory = clientCategory;
    }

    public String getClientSCategory() {
        return clientSCategory;
    }

    public void setClientSCategory(String clientSCategory) {
        this.clientSCategory = clientSCategory;
    }

    public Long getAreaId() {
        return areaId;
    }

    public void setAreaId(Long areaId) {
        this.areaId = areaId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getScale() {
        return scale;
    }

    public void setScale(String scale) {
        this.scale = scale;
    }

    public String getCreditCode() {
        return creditCode;
    }

    public void setCreditCode(String creditCode) {
        this.creditCode = creditCode;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public Long getAddIndex() {
        return addIndex;
    }

    public void setAddIndex(Long addIndex) {
        this.addIndex = addIndex;
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

    public String getChargePersonCode() {
        return chargePersonCode;
    }

    public void setChargePersonCode(String chargePersonCode) {
        this.chargePersonCode = chargePersonCode;
    }

    public String getChargePersonName() {
        return chargePersonName;
    }

    public void setChargePersonName(String chargePersonName) {
        this.chargePersonName = chargePersonName;
    }

    public String getReportEditUserId() {
        return reportEditUserId;
    }

    public void setReportEditUserId(String reportEditUserId) {
        this.reportEditUserId = reportEditUserId;
    }

    public String getReportEditUserName() {
        return reportEditUserName;
    }

    public void setReportEditUserName(String reportEditUserName) {
        this.reportEditUserName = reportEditUserName;
    }
}
