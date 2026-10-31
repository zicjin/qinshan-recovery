package com.sunwayworld.emc.module.erp.bean.contract;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import java.io.Serializable;

/** 中台-计划合同拆分合同报价-DTO */
public class MidPlanContractToContractPkgDTO implements Serializable {

    /** 报价方式 */
    private Integer pkgSource;

    /** 做单机构 */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long orgGbId;

    /** 报价人 */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long saleGbId;

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

    public Long getSaleGbId() {
        return saleGbId;
    }

    public void setSaleGbId(Long saleGbId) {
        this.saleGbId = saleGbId;
    }
}
