package com.sunwayworld.emc.module.erp.bean;

import org.hibernate.validator.constraints.Length;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

public class MidFieldVO implements Serializable {

    /** 所属模板标识 */
    private Long templateGbId;

    /** 所属模块标识 */
    private Long customFieldModuleGbId;

    /** 所属模块tab, com.jiancetech.business.model.enumModel.customContractTemplate.ModuleTabEnum */
    private Integer moduleTabType;

    /** 模板字段的标识 */
    private Long gbId;

    /** 字段id */
    private Long structId;

    /** 字段名称 */
    @Length(max = 255, message = "字段最大长度为255个字符")
    @NotBlank
    private String name;

    /** 字段类型, com.jiancetech.business.model.enumModel.CustomFieldStructType */
    @NotNull
    private Integer type;

    /** 是否为必填 */
    @NotNull
    private Boolean bRequired;

    /** 字段顺序 */
    private Integer orderNum;

    /** 所属核心字段, com.jiancetech.business.model.enumModel.customContractTemplate.CoreFieldEnum */
    private Integer coreFieldType;

    /** 是否为必填 */
    private Boolean bEditable;

    /** 枚举值 */
    @Valid
    private List<MidEnumVO> enums;

    /** 字段结构关系 */
    @Valid
    @NotNull
    private List<MidFieldRelationVO> customFieldRelationDtos;

    /** 默认值 */
    private String defaultValue;

    public Long getTemplateGbId() {
        return templateGbId;
    }

    public void setTemplateGbId(Long templateGbId) {
        this.templateGbId = templateGbId;
    }

    public Long getCustomFieldModuleGbId() {
        return customFieldModuleGbId;
    }

    public void setCustomFieldModuleGbId(Long customFieldModuleGbId) {
        this.customFieldModuleGbId = customFieldModuleGbId;
    }

    public Integer getModuleTabType() {
        return moduleTabType;
    }

    public void setModuleTabType(Integer moduleTabType) {
        this.moduleTabType = moduleTabType;
    }

    public Long getGbId() {
        return gbId;
    }

    public void setGbId(Long gbId) {
        this.gbId = gbId;
    }

    public Long getStructId() {
        return structId;
    }

    public void setStructId(Long structId) {
        this.structId = structId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Boolean getbRequired() {
        return bRequired;
    }

    public void setbRequired(Boolean bRequired) {
        this.bRequired = bRequired;
    }

    public Integer getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }

    public Integer getCoreFieldType() {
        return coreFieldType;
    }

    public void setCoreFieldType(Integer coreFieldType) {
        this.coreFieldType = coreFieldType;
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

    public Boolean getbEditable() {
        return bEditable;
    }

    public void setbEditable(Boolean bEditable) {
        this.bEditable = bEditable;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }
}
