package com.sunwayworld.emc.module.mdm.file.file.bean;

import java.time.LocalDate;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.format.annotation.DateTimeFormat;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;

/**
 * @author zhengxy@sunwayworld.com 2019年10月09日
 */
@Table("T_EMC_FILE_VERSION_DIST")
public class EmcFileVersionDistBean extends AbstractInsertable<Long> {

    @Transient
    private static final long serialVersionUID = 8321987301365594003L;

    @Id
    private Long id; // 主键
    private Long fileVersionId; // 文件版本编码
    private String controlled; // 是否受控
    private String distNo; // 发放编号
    private String operationType; // 操作类型
    private String approverUserId; // 批准人
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate reqDate; // 领用日期
    private String reqUserId; // 领用人
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate recycleDate; // 回收时间
    private String recycleUserId; // 回收人
    private String recycleState; // 回收状况

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getFileVersionId() {
        return fileVersionId;
    }

    public void setFileVersionId(Long fileVersionId) {
        this.fileVersionId = fileVersionId;
    }

    public String getControlled() {
        return controlled;
    }

    public void setControlled(String controlled) {
        this.controlled = controlled;
    }

    public String getDistNo() {
        return distNo;
    }

    public void setDistNo(String distNo) {
        this.distNo = distNo;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public String getApproverUserId() {
        return approverUserId;
    }

    public void setApproverUserId(String approverUserId) {
        this.approverUserId = approverUserId;
    }

    public LocalDate getReqDate() {
        return reqDate;
    }

    public void setReqDate(LocalDate reqDate) {
        this.reqDate = reqDate;
    }

    public String getReqUserId() {
        return reqUserId;
    }

    public void setReqUserId(String reqUserId) {
        this.reqUserId = reqUserId;
    }

    public LocalDate getRecycleDate() {
        return recycleDate;
    }

    public void setRecycleDate(LocalDate recycleDate) {
        this.recycleDate = recycleDate;
    }

    public String getRecycleUserId() {
        return recycleUserId;
    }

    public void setRecycleUserId(String recycleUserId) {
        this.recycleUserId = recycleUserId;
    }

    public String getRecycleState() {
        return recycleState;
    }

    public void setRecycleState(String recycleState) {
        this.recycleState = recycleState;
    }
}
