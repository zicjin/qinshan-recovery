/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.filemanage.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractPersistable;
import com.sunwayworld.framework.support.domain.Persistable;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

@Table(value="T_CORE_FILE_MANAGE_PRINT_RECOR")
public class CoreFileManagePrintRecorBean
extends AbstractPersistable<Long>
implements Persistable<Long> {
    @Transient
    private static final long serialVersionUID = -82566196618711043L;
    @Id
    private Long id;
    private Long fileManageId;
    private Long fileVersionId;
    private Long fileVersionNo;
    private String printUserId;
    private String printUserName;
    private String printOrgName;
    @JSONField(format="yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime printTime;
    private String remark;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getFileManageId() {
        return this.fileManageId;
    }

    public void setFileManageId(Long fileManageId) {
        this.fileManageId = fileManageId;
    }

    public Long getFileVersionId() {
        return this.fileVersionId;
    }

    public void setFileVersionId(Long fileVersionId) {
        this.fileVersionId = fileVersionId;
    }

    public Long getFileVersionNo() {
        return this.fileVersionNo;
    }

    public void setFileVersionNo(Long fileVersionNo) {
        this.fileVersionNo = fileVersionNo;
    }

    public String getPrintUserId() {
        return this.printUserId;
    }

    public void setPrintUserId(String printUserId) {
        this.printUserId = printUserId;
    }

    public String getPrintUserName() {
        return this.printUserName;
    }

    public void setPrintUserName(String printUserName) {
        this.printUserName = printUserName;
    }

    public String getPrintOrgName() {
        return this.printOrgName;
    }

    public void setPrintOrgName(String printOrgName) {
        this.printOrgName = printOrgName;
    }

    public LocalDateTime getPrintTime() {
        return this.printTime;
    }

    public void setPrintTime(LocalDateTime printTime) {
        this.printTime = printTime;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
