package com.sunwayworld.emc.module.erp.bean;

import java.io.Serializable;

public class MidOrderChangeVo implements Serializable
{
    /** 变更类型，com.jiancetech.business.model.enumModel.ServiceChangeType */
    private Integer serviceChangeType;

    /** 变更状态，com.jiancetech.business.model.enumModel.ApplyState */
    private Integer applyState;

    /** 申请时间 */
    private Long applyTime;

    /** 审核时间 */
    private Long auditTime;

    /** 申请gbId */
    private Long applyGbId;

    /** 审核gbId */
    private Long auditGbId;

    public Integer getServiceChangeType()
    {
        return serviceChangeType;
    }

    public void setServiceChangeType(Integer serviceChangeType)
    {
        this.serviceChangeType = serviceChangeType;
    }

    public Integer getApplyState()
    {
        return applyState;
    }

    public void setApplyState(Integer applyState)
    {
        this.applyState = applyState;
    }

    public Long getApplyTime()
    {
        return applyTime;
    }

    public void setApplyTime(Long applyTime)
    {
        this.applyTime = applyTime;
    }

    public Long getAuditTime()
    {
        return auditTime;
    }

    public void setAuditTime(Long auditTime)
    {
        this.auditTime = auditTime;
    }

    public Long getApplyGbId()
    {
        return applyGbId;
    }

    public void setApplyGbId(Long applyGbId)
    {
        this.applyGbId = applyGbId;
    }

    public Long getAuditGbId()
    {
        return auditGbId;
    }

    public void setAuditGbId(Long auditGbId)
    {
        this.auditGbId = auditGbId;
    }
}
