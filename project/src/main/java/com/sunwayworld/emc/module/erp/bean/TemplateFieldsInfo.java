package com.sunwayworld.emc.module.erp.bean;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @author zhengxy@sunwayworld.com 2023年10月16日
 */
public class TemplateFieldsInfo implements Serializable {

    private boolean bRequired;
    private int coreFieldType;
    private String customFieldModuleGbId;
    private String gbId;
    private int moduleTabType;
    private String name;
    private int orderNum;
    private String structId;
    private String templateGbId;
    private int type;
    /** 枚举值 */
    @Valid
    private List<MidEnumVO> enums;

    /** 字段结构关系 */
    @Valid
    @NotNull
    private List<MidFieldRelationVO> customFieldRelationDtos;

    /** 默认值 */
    private String defaultValue;

    public boolean isBRequired() {
        return bRequired;
    }

    public void setBRequired(boolean bRequired) {
        this.bRequired = bRequired;
    }

    public int getCoreFieldType() {
        return coreFieldType;
    }

    public void setCoreFieldType(int coreFieldType) {
        this.coreFieldType = coreFieldType;
    }

    public String getCustomFieldModuleGbId() {
        return customFieldModuleGbId;
    }

    public void setCustomFieldModuleGbId(String customFieldModuleGbId) {
        this.customFieldModuleGbId = customFieldModuleGbId;
    }

    public String getGbId() {
        return gbId;
    }

    public void setGbId(String gbId) {
        this.gbId = gbId;
    }

    public int getModuleTabType() {
        return moduleTabType;
    }

    public void setModuleTabType(int moduleTabType) {
        this.moduleTabType = moduleTabType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(int orderNum) {
        this.orderNum = orderNum;
    }

    public String getStructId() {
        return structId;
    }

    public void setStructId(String structId) {
        this.structId = structId;
    }

    public String getTemplateGbId() {
        return templateGbId;
    }

    public void setTemplateGbId(String templateGbId) {
        this.templateGbId = templateGbId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<MidEnumVO> getEnums() {
        return enums;
    }

    public void setEnums(List<MidEnumVO> enums) {
        this.enums = enums;
    }

    public List<MidFieldRelationVO> getCustomFieldRelationDtos() {
        return customFieldRelationDtos;
    }

    public void setCustomFieldRelationDtos(List<MidFieldRelationVO> customFieldRelationDtos) {
        this.customFieldRelationDtos = customFieldRelationDtos;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }
}
