package com.sunwayworld.emc.module.erp.bean;

import org.hibernate.validator.constraints.Length;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

public class MidFieldRelationVO implements Serializable {

    /** id */
    private Long id;

    /** 字段id */
    private Long structId;

    /** 触发值 */
    @Length(max = 255, message = "触发值最多支持255个字符")
    @NotBlank
    private String val;

    /** 级联字段结构名称 */
    @Length(max = 255, message = "字段名称最多支持255个字符")
    @NotBlank
    private String name;

    /** 级联字段结构是否必填 */
    private Boolean bRequired;

    /** 级联字段结构类型 */
    private Integer type;

    /** 枚举值 */
    @Valid
    private List<MidEnumVO> enums;

    /** 级联属性形式 */
    @NotNull
    private Integer customModusEnum;

    /** 默认值 */
    private String defaultValue;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getbRequired() {
        return bRequired;
    }

    public void setbRequired(Boolean bRequired) {
        this.bRequired = bRequired;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public List<MidEnumVO> getEnums() {
        return enums;
    }

    public void setEnums(List<MidEnumVO> enums) {
        this.enums = enums;
    }

    public Integer getCustomModusEnum() {
        return customModusEnum;
    }

    public void setCustomModusEnum(Integer customModusEnum) {
        this.customModusEnum = customModusEnum;
    }

    public Long getStructId() {
        return structId;
    }

    public void setStructId(Long structId) {
        this.structId = structId;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }
}
