/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.bp.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.sunwayworld.framework.data.annotation.Clob;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.NotNull;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.support.domain.Insertable;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

@Table(value="T_LCDP_BIZ_PROCESS_NODE_ACTION")
public class LcdpBizProcessNodeActionBean
extends AbstractInsertable<Long>
implements Insertable<Long> {
    @Transient
    private static final long serialVersionUID = 652094739447744277L;
    @Id
    private Long id;
    private Long bpId;
    private Long bpNodeId;
    @NotNull(defaultValue="0")
    private String rejectFlag;
    private String bpActionCode;
    private String bpActionName;
    @NotNull(defaultValue="1")
    private Long orderNo;
    private Long nextBpNodeId;
    private String nextBpNodeCode;
    private String nextBpNodeName;
    @Clob
    private String actionScript;
    @Clob
    private String actionScriptClassContent;
    @Clob
    private String checkScriptJs;
    @Clob
    private String checkScriptJava;
    @Clob
    private String checkScriptClassContent;
    @NotNull(defaultValue="0")
    private String counterSignFlag;
    @NotNull(defaultValue="0")
    private String elecSignFlag;
    private String remark;
    @NotNull(defaultValue="0")
    private String defaultFlag;
    private String createdById;
    private String createdByName;
    @JSONField(format="yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;
    private String createdByOrgId;
    private String createdByOrgName;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getBpId() {
        return this.bpId;
    }

    public void setBpId(Long bpId) {
        this.bpId = bpId;
    }

    public Long getBpNodeId() {
        return this.bpNodeId;
    }

    public void setBpNodeId(Long bpNodeId) {
        this.bpNodeId = bpNodeId;
    }

    public String getRejectFlag() {
        return this.rejectFlag;
    }

    public void setRejectFlag(String rejectFlag) {
        this.rejectFlag = rejectFlag;
    }

    public String getBpActionCode() {
        return this.bpActionCode;
    }

    public void setBpActionCode(String bpActionCode) {
        this.bpActionCode = bpActionCode;
    }

    public String getBpActionName() {
        return this.bpActionName;
    }

    public Long getOrderNo() {
        return this.orderNo;
    }

    public void setOrderNo(Long orderNo) {
        this.orderNo = orderNo;
    }

    public void setBpActionName(String bpActionName) {
        this.bpActionName = bpActionName;
    }

    public Long getNextBpNodeId() {
        return this.nextBpNodeId;
    }

    public void setNextBpNodeId(Long nextBpNodeId) {
        this.nextBpNodeId = nextBpNodeId;
    }

    public String getNextBpNodeCode() {
        return this.nextBpNodeCode;
    }

    public void setNextBpNodeCode(String nextBpNodeCode) {
        this.nextBpNodeCode = nextBpNodeCode;
    }

    public String getNextBpNodeName() {
        return this.nextBpNodeName;
    }

    public void setNextBpNodeName(String nextBpNodeName) {
        this.nextBpNodeName = nextBpNodeName;
    }

    public String getActionScript() {
        return this.actionScript;
    }

    public void setActionScript(String actionScript) {
        this.actionScript = actionScript;
    }

    public String getCheckScriptJs() {
        return this.checkScriptJs;
    }

    public void setCheckScriptJs(String checkScriptJs) {
        this.checkScriptJs = checkScriptJs;
    }

    public String getCheckScriptJava() {
        return this.checkScriptJava;
    }

    public String getActionScriptClassContent() {
        return this.actionScriptClassContent;
    }

    public void setActionScriptClassContent(String actionScriptClassContent) {
        this.actionScriptClassContent = actionScriptClassContent;
    }

    public String getCheckScriptClassContent() {
        return this.checkScriptClassContent;
    }

    public void setCheckScriptClassContent(String checkScriptClassContent) {
        this.checkScriptClassContent = checkScriptClassContent;
    }

    public void setCheckScriptJava(String checkScriptJava) {
        this.checkScriptJava = checkScriptJava;
    }

    public String getCounterSignFlag() {
        return this.counterSignFlag;
    }

    public void setCounterSignFlag(String counterSignFlag) {
        this.counterSignFlag = counterSignFlag;
    }

    public String getElecSignFlag() {
        return this.elecSignFlag;
    }

    public void setElecSignFlag(String elecSignFlag) {
        this.elecSignFlag = elecSignFlag;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getDefaultFlag() {
        return this.defaultFlag;
    }

    public void setDefaultFlag(String defaultFlag) {
        this.defaultFlag = defaultFlag;
    }

    @Override
    public String getCreatedById() {
        return this.createdById;
    }

    @Override
    public void setCreatedById(String createdById) {
        this.createdById = createdById;
    }

    @Override
    public String getCreatedByName() {
        return this.createdByName;
    }

    @Override
    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    @Override
    public LocalDateTime getCreatedTime() {
        return this.createdTime;
    }

    @Override
    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    @Override
    public String getCreatedByOrgId() {
        return this.createdByOrgId;
    }

    @Override
    public void setCreatedByOrgId(String createdByOrgId) {
        this.createdByOrgId = createdByOrgId;
    }

    @Override
    public String getCreatedByOrgName() {
        return this.createdByOrgName;
    }

    @Override
    public void setCreatedByOrgName(String createdByOrgName) {
        this.createdByOrgName = createdByOrgName;
    }
}
