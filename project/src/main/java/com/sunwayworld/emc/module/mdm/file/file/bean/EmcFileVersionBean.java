package com.sunwayworld.emc.module.mdm.file.file.bean;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;

/**
 * @author zhengxy@sunwayworld.com 2019年10月09日
 */
@Table("T_EMC_FILE_VERSION")
public class EmcFileVersionBean extends AbstractInsertable<Long> {

    @Transient
    private static final long serialVersionUID = 8024794141018565128L;

    @Id
    private Long id; // 主键
    private Long fileId; // 文件编码
    private String versionNo; // 版本编号
    private String applyUserId; // 申请人
    private String usedUserId; // 文件使用人
    private String usedUserName; // 文件使用人
    private String operationType; // 操作类型
    private String modifyReason; // 更改替换理由
    private String beforeModifyContent; // 修改/置换前内容
    private String afterModifyContent; // 修改/置换后内容

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    public String getVersionNo() {
        return versionNo;
    }

    public void setVersionNo(String versionNo) {
        this.versionNo = versionNo;
    }

    public String getApplyUserId() {
        return applyUserId;
    }

    public void setApplyUserId(String applyUserId) {
        this.applyUserId = applyUserId;
    }

    public String getUsedUserId() {
        return usedUserId;
    }

    public void setUsedUserId(String usedUserId) {
        this.usedUserId = usedUserId;
    }
    
    public String getUsedUserName() {
        return usedUserName;
    }

    public void setUsedUserName(String usedUserName) {
        this.usedUserName = usedUserName;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public String getModifyReason() {
        return modifyReason;
    }

    public void setModifyReason(String modifyReason) {
        this.modifyReason = modifyReason;
    }

    public String getBeforeModifyContent() {
        return beforeModifyContent;
    }

    public void setBeforeModifyContent(String beforeModifyContent) {
        this.beforeModifyContent = beforeModifyContent;
    }

    public String getAfterModifyContent() {
        return afterModifyContent;
    }

    public void setAfterModifyContent(String afterModifyContent) {
        this.afterModifyContent = afterModifyContent;
    }

}
