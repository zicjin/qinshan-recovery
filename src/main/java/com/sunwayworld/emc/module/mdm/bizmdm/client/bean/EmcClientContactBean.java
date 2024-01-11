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
 * 客户联系人
 *
 * @author magh@sunwayworld.com
 * @date 2019-10-19
 */

@Table("T_EMC_CLIENT_CONTACT")
public class EmcClientContactBean extends AbstractPersistable<Long> implements Insertable<Long> {

    @Transient
    private static final long serialVersionUID = 2474706811165239864L;

    @Id
    private Long id;// 主键

    private Long clientId;// 客户编码

    private String lxrName;// 联系人名称

    private String lxrPhone;// 联系人电话

    private String lxrEmail;// 联系人邮箱

    private String fax;// 传真

    private String postcode;// 邮编

    private String lxrAddress;// 联系人地址

    private String job;// 联系人职务

    private String province;// 省

    private String city;// 市

    private String county;// 区

    private String remark;// 备注

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

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public String getLxrName() {
        return lxrName;
    }

    public void setLxrName(String lxrName) {
        this.lxrName = lxrName;
    }

    public String getLxrPhone() {
        return lxrPhone;
    }

    public void setLxrPhone(String lxrPhone) {
        this.lxrPhone = lxrPhone;
    }

    public String getLxrEmail() {
        return lxrEmail;
    }

    public void setLxrEmail(String lxrEmail) {
        this.lxrEmail = lxrEmail;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getLxrAddress() {
        return lxrAddress;
    }

    public void setLxrAddress(String lxrAddress) {
        this.lxrAddress = lxrAddress;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
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
