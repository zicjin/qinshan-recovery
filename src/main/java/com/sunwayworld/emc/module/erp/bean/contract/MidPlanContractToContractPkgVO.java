package com.sunwayworld.emc.module.erp.bean.contract;

import java.io.Serializable;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/** 中台-计划合同拆分合同报价-VO */
public class MidPlanContractToContractPkgVO implements Serializable {
    /** 报价方式 */
    private Integer pkgSource;

    @JsonSerialize(using = ToStringSerializer.class)
    /** 做单机构 */
    private Long orgGbId;

    /** 做单机构名称 */
    private String orgName;

    @JsonSerialize(using = ToStringSerializer.class)
    /** 报价人 */
    private Long saleGbId;

    /** 报价员名称 */
    private String saleName;

    public Integer getPkgSource() {
        return pkgSource;
    }

    public void setPkgSource(Integer pkgSource) {
        this.pkgSource = pkgSource;
    }

    public Long getOrgGbId() {
        return orgGbId;
    }

    public void setOrgGbId(Long orgGbId) {
        this.orgGbId = orgGbId;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public Long getSaleGbId() {
        return saleGbId;
    }

    public void setSaleGbId(Long saleGbId) {
        this.saleGbId = saleGbId;
    }

    public String getSaleName() {
        return saleName;
    }

    public void setSaleName(String saleName) {
        this.saleName = saleName;
    }
}
