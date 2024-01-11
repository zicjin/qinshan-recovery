package com.sunwayworld.emc.module.erp.bean.contract;

import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

public class BaseErp5Param<T> {

    /** 商户ID */
    private String merchantId;

    /** 操作人员编号 */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long operatorGbId;

    private String requestBody;

    private String wpUserUnionId;

    private List<T> list;

    private String processorName;

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }

    public String getWpUserUnionId() {
        return wpUserUnionId;
    }

    public void setWpUserUnionId(String wpUserUnionId) {
        this.wpUserUnionId = wpUserUnionId;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public String getProcessorName() {
        return processorName;
    }

    public void setProcessorName(String processorName) {
        this.processorName = processorName;
    }

    public Long getOperatorGbId() {
        return operatorGbId;
    }

    public void setOperatorGbId(Long operatorGbId) {
        this.operatorGbId = operatorGbId;
    }
}
