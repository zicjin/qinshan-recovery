/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.moduletmpl.bean;

import com.sunwayworld.cloud.module.lcdp.moduletmpl.bean.LcdpModuleCustomReplaceDTO;
import com.sunwayworld.cloud.module.lcdp.moduletmpl.bean.LcdpModuleTmplChildTableDTO;
import com.sunwayworld.framework.support.domain.AbstractBaseData;
import java.util.List;

public class LcdpModuleTmplDTO
extends AbstractBaseData {
    private static final long serialVersionUID = 1201601684826467219L;
    private Long moduleTmplId;
    private String moduleTmplName;
    private String masterTableName;
    private List<LcdpModuleTmplChildTableDTO> childTableList;
    private String bpFlag;
    private String uploaderFlag;
    private String chooseFlag;
    private String moduleName;
    private String categoryName;
    private String modulePath;
    private String masterScriptPath;
    private String detailPagePath;
    private String preInsertFlag;
    private Long resourceId;
    private Long parentId;
    private String resourceName;
    private String resourceDesc;
    private String resourceCategory;
    private String tmplClassId;
    private List<LcdpModuleCustomReplaceDTO> customReplaceList;
    private String dependentWidgetId;
    private List<String> tableNameList;

    public Long getModuleTmplId() {
        return this.moduleTmplId;
    }

    public void setModuleTmplId(Long moduleTmplId) {
        this.moduleTmplId = moduleTmplId;
    }

    public String getModuleTmplName() {
        return this.moduleTmplName;
    }

    public void setModuleTmplName(String moduleTmplName) {
        this.moduleTmplName = moduleTmplName;
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

    public String getBpFlag() {
        return this.bpFlag;
    }

    public void setBpFlag(String bpFlag) {
        this.bpFlag = bpFlag;
    }

    public String getUploaderFlag() {
        return this.uploaderFlag;
    }

    public void setUploaderFlag(String uploaderFlag) {
        this.uploaderFlag = uploaderFlag;
    }

    public String getPreInsertFlag() {
        return this.preInsertFlag;
    }

    public void setPreInsertFlag(String preInsertFlag) {
        this.preInsertFlag = preInsertFlag;
    }

    public String getModuleName() {
        return this.moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getCategoryName() {
        return this.categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getModulePath() {
        return this.modulePath;
    }

    public void setModulePath(String modulePath) {
        this.modulePath = modulePath;
    }

    public String getMasterScriptPath() {
        return this.masterScriptPath;
    }

    public void setMasterScriptPath(String masterScriptPath) {
        this.masterScriptPath = masterScriptPath;
    }

    public String getDetailPagePath() {
        return this.detailPagePath;
    }

    public void setDetailPagePath(String detailPagePath) {
        this.detailPagePath = detailPagePath;
    }

    public Long getResourceId() {
        return this.resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public Long getParentId() {
        return this.parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
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

    public String getResourceCategory() {
        return this.resourceCategory;
    }

    public void setResourceCategory(String resourceCategory) {
        this.resourceCategory = resourceCategory;
    }

    public String getTmplClassId() {
        return this.tmplClassId;
    }

    public void setTmplClassId(String tmplClassId) {
        this.tmplClassId = tmplClassId;
    }

    public List<LcdpModuleCustomReplaceDTO> getCustomReplaceList() {
        return this.customReplaceList;
    }

    public void setCustomReplaceList(List<LcdpModuleCustomReplaceDTO> customReplaceList) {
        this.customReplaceList = customReplaceList;
    }

    public String getDependentWidgetId() {
        return this.dependentWidgetId;
    }

    public void setDependentWidgetId(String dependentWidgetId) {
        this.dependentWidgetId = dependentWidgetId;
    }

    public List<String> getTableNameList() {
        return this.tableNameList;
    }

    public void setTableNameList(List<String> tableNameList) {
        this.tableNameList = tableNameList;
    }

    public String getChooseFlag() {
        return this.chooseFlag;
    }

    public void setChooseFlag(String chooseFlag) {
        this.chooseFlag = chooseFlag;
    }
}
