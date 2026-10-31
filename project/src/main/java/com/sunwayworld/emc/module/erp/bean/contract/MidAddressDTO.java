package com.sunwayworld.emc.module.erp.bean.contract;

import java.io.Serializable;

public class MidAddressDTO implements Serializable {

    /** 地域唯一标识id */
    private Long id;

    /** 地域省编码" */
    private Integer province;

    /** 地域市编码" */
    private Integer city;

    /** 地域区编码" */
    private Integer district;

    /** 地域信息 */
    private String detail;

    /** 英文地址 */
    private String enAddress;

    /** 地址详情 */
    private String address;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getProvince() {
        return province;
    }

    public void setProvince(Integer province) {
        this.province = province;
    }

    public Integer getCity() {
        return city;
    }

    public void setCity(Integer city) {
        this.city = city;
    }

    public Integer getDistrict() {
        return district;
    }

    public void setDistrict(Integer district) {
        this.district = district;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getEnAddress() {
        return enAddress;
    }

    public void setEnAddress(String enAddress) {
        this.enAddress = enAddress;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
