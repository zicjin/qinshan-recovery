package com.sunwayworld.emc.module.mdm.basemdm.monitorability.bean;

import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.NotNull;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;

/**
 * @author magt@sunwayworld.com
 * @date 2019-10-09
 */

@Table("T_EMC_MONITOR_ABILITY")
public class EmcMonitorAbilityBean extends AbstractInsertable<Long> {

    @Transient
    private static final long serialVersionUID = -1793211426593037210L;

    @Id
    private Long id;// 监测代码（主键）

    private String monitorName;// 监测站名称

    private String monitorObjId;// 监测对象编码

    private String monitorObjName;// 监测对象名称

    private String projCategory;// 项目类别

    private String methodSource;// 方法来源

    private String authentication;// 是否认证

    private String approval;// 是否认可

    private String instruction;// 使用说明

    private String remark;// 备注

    @NotNull(defaultValue = Constant.YES)
    private String status;// 状态

    private String monitorAbilityNo; // 监测代码序号

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMonitorName() {
        return monitorName;
    }

    public void setMonitorName(String monitorName) {
        this.monitorName = monitorName;
    }

    public String getMonitorObjId() {
        return monitorObjId;
    }

    public void setMonitorObjId(String monitorObjId) {
        this.monitorObjId = monitorObjId;
    }

    public String getMonitorObjName() {
        return monitorObjName;
    }

    public void setMonitorObjName(String monitorObjName) {
        this.monitorObjName = monitorObjName;
    }

    public String getProjCategory() {
        return projCategory;
    }

    public void setProjCategory(String projCategory) {
        this.projCategory = projCategory;
    }

    public String getMethodSource() {
        return methodSource;
    }

    public void setMethodSource(String methodSource) {
        this.methodSource = methodSource;
    }

    public String getAuthentication() {
        return authentication;
    }

    public void setAuthentication(String authentication) {
        this.authentication = authentication;
    }

    public String getApproval() {
        return approval;
    }

    public void setApproval(String approval) {
        this.approval = approval;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
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

    public String getMonitorAbilityNo() {
        return monitorAbilityNo;
    }

    public void setMonitorAbilityNo(String monitorAbilityNo) {
        this.monitorAbilityNo = monitorAbilityNo;
    }
}
