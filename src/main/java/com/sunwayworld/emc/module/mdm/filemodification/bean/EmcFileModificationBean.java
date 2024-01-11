package com.sunwayworld.emc.module.mdm.filemodification.bean;

import java.time.LocalDateTime;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.format.annotation.DateTimeFormat;

import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.support.domain.AbstractAuditable;
import com.sunwayworld.framework.support.domain.Auditable;

/**
 * 文件修改申请
 * @author zhanghy@sunwayworld.com 2020-09-01
 */
@Table("T_DINGAN_FILE_MODIFY")
public class EmcFileModificationBean extends AbstractAuditable<Long> implements Auditable<Long> {

	private static final long serialVersionUID = 4533948665725016249L;

	private Long id; // 主键

	private String fileId; // 文件编号

	private String fileName; // 文件名称

	private String modifyingContent; // 欲修改内容

	private String modifiedContent; // 修改后内容

	private String modifyReason; // 修改原因

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime implementDate; // 实施日期

    private String createdById;// 制单人编码

    private String createdByName;// 制单人名称

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;// 制单时间

	private String auditorId;//审核人编码

	private String auditorName;//审核人名称

	private String confirmerId;//确认人编码

	private String confirmerName;//确认人名称

    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFileId() {
		return fileId;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getModifyingContent() {
		return modifyingContent;
	}

	public void setModifyingContent(String modifyingContent) {
		this.modifyingContent = modifyingContent;
	}

	public String getModifiedContent() {
		return modifiedContent;
	}

	public void setModifiedContent(String modifiedContent) {
		this.modifiedContent = modifiedContent;
	}

	public String getModifyReason() {
		return modifyReason;
	}

	public void setModifyReason(String modifyReason) {
		this.modifyReason = modifyReason;
	}

	public LocalDateTime getImplementDate() {
		return implementDate;
	}

	public void setImplementDate(LocalDateTime implementDate) {
		this.implementDate = implementDate;
	}

	public String getCreatedById() {
		return createdById;
	}

	public void setCreatedById(String createdById) {
		this.createdById = createdById;
	}

	public String getCreatedByName() {
		return createdByName;
	}

	public void setCreatedByName(String createdByName) {
		this.createdByName = createdByName;
	}

	public LocalDateTime getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(LocalDateTime createdTime) {
		this.createdTime = createdTime;
	}

	public String getCreatedByOrgId() {
		return createdByOrgId;
	}

	public void setCreatedByOrgId(String createdByOrgId) {
		this.createdByOrgId = createdByOrgId;
	}

	public String getCreatedByOrgName() {
		return createdByOrgName;
	}

	public void setCreatedByOrgName(String createdByOrgName) {
		this.createdByOrgName = createdByOrgName;
	}

	private String createdByOrgId;// 制单人单位编码

    private String createdByOrgName;// 制单人单位名称

	public String getAuditorId() {
		return auditorId;
	}

	public void setAuditorId(String auditorId) {
		this.auditorId = auditorId;
	}

	public String getAuditorName() {
		return auditorName;
	}

	public void setAuditorName(String auditorName) {
		this.auditorName = auditorName;
	}

	public String getConfirmerId() {
		return confirmerId;
	}

	public void setConfirmerId(String confirmerId) {
		this.confirmerId = confirmerId;
	}

	public String getConfirmerName() {
		return confirmerName;
	}

	public void setConfirmerName(String confirmerName) {
		this.confirmerName = confirmerName;
	}
}
