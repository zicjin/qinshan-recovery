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
@Table("T_EMC_FILE")
public class EmcFileBean extends AbstractInsertable<Long> {

    @Transient
    private static final long serialVersionUID = -1715948215253963815L;

    @Id
    private Long id; // 主键
    private String fileNo; // 文件编号
    private String fileName; // 文件名称
    private Long fileCategoryId; // 文件类型
    private String archiveOrgId; // 归档科室
    private String archiveName; // 归档文件柜号及文件夹名称
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate registerDate; // 登记时间
    private String registerUserId; // 登记人
    private String departmentUserId; // 科室负责人
    private String loc; // 文件存放位置
    private String auditUserId; // 审核人
    private String approveUserId; // 批准人
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate approveDate; // 批准发布日期

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getFileNo() {
        return fileNo;
    }

    public void setFileNo(String fileNo) {
        this.fileNo = fileNo;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getFileCategoryId() {
        return fileCategoryId;
    }

    public void setFileCategoryId(Long fileCategoryId) {
        this.fileCategoryId = fileCategoryId;
    }

    public String getArchiveOrgId() {
        return archiveOrgId;
    }

    public void setArchiveOrgId(String archiveOrgId) {
        this.archiveOrgId = archiveOrgId;
    }

    public String getArchiveName() {
        return archiveName;
    }

    public void setArchiveName(String archiveName) {
        this.archiveName = archiveName;
    }

    public LocalDate getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(LocalDate registerDate) {
        this.registerDate = registerDate;
    }

    public String getRegisterUserId() {
        return registerUserId;
    }

    public void setRegisterUserId(String registerUserId) {
        this.registerUserId = registerUserId;
    }

    public String getDepartmentUserId() {
        return departmentUserId;
    }

    public void setDepartmentUserId(String departmentUserId) {
        this.departmentUserId = departmentUserId;
    }

    public String getLoc() {
        return loc;
    }

    public void setLoc(String loc) {
        this.loc = loc;
    }

    public String getAuditUserId() {
        return auditUserId;
    }

    public void setAuditUserId(String auditUserId) {
        this.auditUserId = auditUserId;
    }

    public String getApproveUserId() {
        return approveUserId;
    }

    public void setApproveUserId(String approveUserId) {
        this.approveUserId = approveUserId;
    }

    public LocalDate getApproveDate() {
        return approveDate;
    }

    public void setApproveDate(LocalDate approveDate) {
        this.approveDate = approveDate;
    }
}
