/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.resource.bean;

import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpResourceCheckoutConfigDTO;
import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpResourceTreeNodeDTO;
import com.sunwayworld.framework.support.domain.AbstractBaseData;
import java.util.List;

public class LcdpExportLogFileDTO
extends AbstractBaseData {
    private static final long serialVersionUID = -5415110072520731263L;
    private List<LcdpResourceTreeNodeDTO> treeNodeDTOList;
    private String log;
    private Long fileId;
    private Boolean allowImport;
    private String checkoutRecordNo;
    private LcdpResourceCheckoutConfigDTO resourceCheckoutConfigDTO;

    public List<LcdpResourceTreeNodeDTO> getTreeNodeDTOList() {
        return this.treeNodeDTOList;
    }

    public void setTreeNodeDTOList(List<LcdpResourceTreeNodeDTO> treeNodeDTOList) {
        this.treeNodeDTOList = treeNodeDTOList;
    }

    public String getLog() {
        return this.log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public Long getFileId() {
        return this.fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    public Boolean getAllowImport() {
        return this.allowImport;
    }

    public void setAllowImport(Boolean allowImport) {
        this.allowImport = allowImport;
    }

    public String getCheckoutRecordNo() {
        return this.checkoutRecordNo;
    }

    public void setCheckoutRecordNo(String checkoutRecordNo) {
        this.checkoutRecordNo = checkoutRecordNo;
    }

    public LcdpResourceCheckoutConfigDTO getResourceCheckoutConfigDTO() {
        return this.resourceCheckoutConfigDTO;
    }

    public void setResourceCheckoutConfigDTO(LcdpResourceCheckoutConfigDTO resourceCheckoutConfigDTO) {
        this.resourceCheckoutConfigDTO = resourceCheckoutConfigDTO;
    }
}
