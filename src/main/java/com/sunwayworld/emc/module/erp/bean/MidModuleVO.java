package com.sunwayworld.emc.module.erp.bean;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class MidModuleVO implements Serializable {

    private Long gbId;

    /**
     * 模块名称
     */
    @Length(max = 255, message = "模块最大长度为255个字符")
    @NotBlank
    private String name;

    /**
     * 所属模板gbId
     */
    private Long templateGbId;

    /**
     * 是否在详情页展示，true为展示
     */
    private Boolean bShow;

    /**
     * 模块在模板中的index
     */
    private Integer orderNum;

    /**
     * 对应核心模块, com.jiancetech.business.model.enumModel.customContractTemplate.CoreModuleEnum
     */
    private Integer coreModuleType;

    /**
     * 核心模块所属标签, com.jiancetech.business.model.enumModel.customContractTemplate.ModuleTabEnum
     */
    @NotNull
    private Integer moduleTabType;

    public Long getGbId() {
        return gbId;
    }

    public void setGbId(Long gbId) {
        this.gbId = gbId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getTemplateGbId() {
        return templateGbId;
    }

    public void setTemplateGbId(Long templateGbId) {
        this.templateGbId = templateGbId;
    }

    public Boolean getbShow() {
        return bShow;
    }

    public void setbShow(Boolean bShow) {
        this.bShow = bShow;
    }

    public Integer getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }

    public Integer getCoreModuleType() {
        return coreModuleType;
    }

    public void setCoreModuleType(Integer coreModuleType) {
        this.coreModuleType = coreModuleType;
    }

    public Integer getModuleTabType() {
        return moduleTabType;
    }

    public void setModuleTabType(Integer moduleTabType) {
        this.moduleTabType = moduleTabType;
    }
}
