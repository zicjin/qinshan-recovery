package com.sunwayworld.emc.module.erp.bean.contract;

import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

public class PushAuditUserContractReq extends BaseErp5Param {

    /** 合同GbId */
    private String userContractGbId;

    /** 合同编号 */
    private String userContractNumber;

    /** 订单GbId列表 */
    @JsonSerialize(using = ToStringSerializer.class)
    private List<String> orderGbIds;

    /** 订单编号列表 */
    private List<String> orderNumbers;

    /** 合同评审状态 0->通过;1->驳回 */
    private Integer auditStatus;

    /** 驳回原因 */
    private String rejectReason;

    public String getUserContractGbId() {
        return userContractGbId;
    }

    public void setUserContractGbId(String userContractGbId) {
        this.userContractGbId = userContractGbId;
    }

    public String getUserContractNumber() {
        return userContractNumber;
    }

    public void setUserContractNumber(String userContractNumber) {
        this.userContractNumber = userContractNumber;
    }

    public List<String> getOrderGbIds() {
        return orderGbIds;
    }

    public void setOrderGbIds(List<String> orderGbIds) {
        this.orderGbIds = orderGbIds;
    }

    public List<String> getOrderNumbers() {
        return orderNumbers;
    }

    public void setOrderNumbers(List<String> orderNumbers) {
        this.orderNumbers = orderNumbers;
    }

    public Integer getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(Integer auditStatus) {
        this.auditStatus = auditStatus;
    }

    public String getRejectReason() {
        return rejectReason;
    }

    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
    }
}
