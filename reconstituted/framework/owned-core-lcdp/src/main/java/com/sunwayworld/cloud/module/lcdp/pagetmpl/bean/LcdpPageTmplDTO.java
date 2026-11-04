/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.pagetmpl.bean;

import com.sunwayworld.cloud.module.lcdp.moduletmpl.bean.LcdpModuleTmplChildTableDTO;
import com.sunwayworld.framework.support.domain.AbstractBaseData;
import java.util.List;

public class LcdpPageTmplDTO
extends AbstractBaseData {
    private static final long serialVersionUID = -5337848429570664980L;
    private Long pageTmplId;
    private String pageTmplName;
    private String masterTableName;
    private List<LcdpModuleTmplChildTableDTO> childTableList;
    private String uploaderFlag;
    private String resourceName;
    private String resourceDesc;
    private Long parentId;
    private String modulePath;
    private Long viewId;
    private String viewPath;
    private String moduleName;
    private String dependentWidgetId;
    private String masterScriptPath;
    private String bpFlag;
    private String preInsertFlag;

    public Long getPageTmplId() {
        return this.pageTmplId;
    }

    public void setPageTmplId(Long pageTmplId) {
        this.pageTmplId = pageTmplId;
    }

    public String getPageTmplName() {
        return this.pageTmplName;
    }

    public void setPageTmplName(String pageTmplName) {
        this.pageTmplName = pageTmplName;
    }

    public String getResourceName() {
        return this.resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getResourceDesc() {
        return this.resourceDesc;
    }

    public void setResourceDesc(String resourceDesc) {
        this.resourceDesc = resourceDesc;
    }

    public Long getParentId() {
        return this.parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getMasterTableName() {
        return this.masterTableName;
    }

    public void setMasterTableName(String masterTableName) {
        this.masterTableName = masterTableName;
    }

    public List<LcdpModuleTmplChildTableDTO> getChildTableList() {
        return this.childTableList;
    }

    public void setChildTableList(List<LcdpModuleTmplChildTableDTO> childTableList) {
        this.childTableList = childTableList;
    }

    public String getUploaderFlag() {
        return this.uploaderFlag;
    }

    public void setUploaderFlag(String uploaderFlag) {
        this.uploaderFlag = uploaderFlag;
    }

    public String getModulePath() {
        return this.modulePath;
    }

    public void setModulePath(String modulePath) {
        this.modulePath = modulePath;
    }

    public Long getViewId() {
        return this.viewId;
    }

    public void setViewId(Long viewId) {
        this.viewId = viewId;
    }

    public String getViewPath() {
        return this.viewPath;
    }

    public void setViewPath(String viewPath) {
        this.viewPath = viewPath;
    }

    public String getModuleName() {
        return this.moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getDependentWidgetId() {
        return this.dependentWidgetId;
    }

    public void setDependentWidgetId(String dependentWidgetId) {
        this.dependentWidgetId = dependentWidgetId;
    }

    public String getMasterScriptPath() {
        return this.masterScriptPath;
    }

    public void setMasterScriptPath(String masterScriptPath) {
        this.masterScriptPath = masterScriptPath;
    }

    public String getBpFlag() {
        return this.bpFlag;
    }

    public void setBpFlag(String bpFlag) {
        this.bpFlag = bpFlag;
    }

    public String getPreInsertFlag() {
        return this.preInsertFlag;
    }

    public void setPreInsertFlag(String preInsertFlag) {
        this.preInsertFlag = preInsertFlag;
    }
}
