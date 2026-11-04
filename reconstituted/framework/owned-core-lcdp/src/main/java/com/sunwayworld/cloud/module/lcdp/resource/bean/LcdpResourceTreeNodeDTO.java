/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.resource.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpResourceBean;
import com.sunwayworld.cloud.module.lcdp.table.bean.LcdpTableBean;
import com.sunwayworld.cloud.module.lcdp.table.bean.LcdpViewBean;
import com.sunwayworld.framework.support.tree.bean.AbstractTreeNode;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.format.annotation.DateTimeFormat;

public class LcdpResourceTreeNodeDTO
extends AbstractTreeNode {
    private static final long serialVersionUID = -567548463312442378L;
    private String id;
    private String name;
    private String desc;
    private String type;
    private String parentId;
    private Long orderNo;
    private Long version;
    private String path;
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime editTime;
    private String analysisResult;
    private Boolean allowImport;
    private String checkOutFlag;
    private Boolean leafFlag;
    private String resourceStatus;
    private String dataType;
    private String checkoutUser;
    private String otherUserCheckOutFlag;
    private String categoryCheckoutStatus;
    private String moduleCheckoutStatus;
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime checkoutTime;

    public static LcdpResourceTreeNodeDTO of(LcdpResourceBean resource) {
        LcdpResourceTreeNodeDTO instance = new LcdpResourceTreeNodeDTO();
        instance.setId(ObjectUtils.isEmpty(resource.getId()) ? null : resource.getId().toString());
        instance.setName(resource.getResourceName());
        instance.setDesc(resource.getResourceDesc());
        instance.setType(resource.getResourceCategory());
        instance.setParentId(ObjectUtils.isEmpty(resource.getParentId()) ? null : resource.getParentId().toString());
        instance.setOrderNo(resource.getOrderNo());
        instance.setVersion(resource.getEffectVersion());
        instance.setEditTime(StringUtils.isEmpty(resource.getExt$Item("edittime")) ? null : LocalDateTime.parse(resource.getExt$Item("edittime"), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        instance.setPath(resource.getPath());
        instance.setDataType(resource.getDataType());
        instance.setCheckoutTime(resource.getCheckoutTime());
        return instance;
    }

    public static LcdpResourceTreeNodeDTO of(LcdpTableBean table) {
        LcdpResourceTreeNodeDTO instance = new LcdpResourceTreeNodeDTO();
        instance.setId(table.getTableName());
        instance.setName(table.getTableName());
        instance.setDesc(table.getTableDesc());
        instance.setOrderNo(1L);
        instance.setVersion(table.getVersion());
        instance.setEditTime(table.getCreatedTime());
        return instance;
    }

    public static LcdpResourceTreeNodeDTO of(LcdpViewBean view) {
        LcdpResourceTreeNodeDTO instance = new LcdpResourceTreeNodeDTO();
        instance.setId(view.getViewName());
        instance.setName(view.getViewName());
        instance.setDesc(view.getViewName());
        instance.setOrderNo(1L);
        instance.setVersion(view.getVersion());
        instance.setEditTime(view.getCreatedTime());
        return instance;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return this.desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String getParentId() {
        return this.parentId;
    }

    @Override
    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public Long getOrderNo() {
        return this.orderNo;
    }

    public void setOrderNo(Long orderNo) {
        this.orderNo = orderNo;
    }

    public Long getVersion() {
        return this.version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public LocalDateTime getEditTime() {
        return this.editTime;
    }

    public void setEditTime(LocalDateTime editTime) {
        this.editTime = editTime;
    }

    public String getAnalysisResult() {
        return this.analysisResult;
    }

    public void setAnalysisResult(String analysisResult) {
        this.analysisResult = analysisResult;
    }

    public Boolean getAllowImport() {
        return this.allowImport;
    }

    public void setAllowImport(Boolean allowImport) {
        this.allowImport = allowImport;
    }

    public String getCheckOutFlag() {
        return this.checkOutFlag;
    }

    public void setCheckOutFlag(String checkOutFlag) {
        this.checkOutFlag = checkOutFlag;
    }

    public Boolean getLeafFlag() {
        return this.leafFlag;
    }

    public void setLeafFlag(Boolean leafFlag) {
        this.leafFlag = leafFlag;
    }

    public String getResourceStatus() {
        return this.resourceStatus;
    }

    public void setResourceStatus(String resourceStatus) {
        this.resourceStatus = resourceStatus;
    }

    public String getDataType() {
        return this.dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getCheckoutUser() {
        return this.checkoutUser;
    }

    public void setCheckoutUser(String checkoutUser) {
        this.checkoutUser = checkoutUser;
    }

    public String getOtherUserCheckOutFlag() {
        return this.otherUserCheckOutFlag;
    }

    public void setOtherUserCheckOutFlag(String otherUserCheckOutFlag) {
        this.otherUserCheckOutFlag = otherUserCheckOutFlag;
    }

    public String getCategoryCheckoutStatus() {
        return this.categoryCheckoutStatus;
    }

    public void setCategoryCheckoutStatus(String categoryCheckoutStatus) {
        this.categoryCheckoutStatus = categoryCheckoutStatus;
    }

    public String getModuleCheckoutStatus() {
        return this.moduleCheckoutStatus;
    }

    public void setModuleCheckoutStatus(String moduleCheckoutStatus) {
        this.moduleCheckoutStatus = moduleCheckoutStatus;
    }

    public LocalDateTime getCheckoutTime() {
        return this.checkoutTime;
    }

    public void setCheckoutTime(LocalDateTime checkoutTime) {
        this.checkoutTime = checkoutTime;
    }
}
