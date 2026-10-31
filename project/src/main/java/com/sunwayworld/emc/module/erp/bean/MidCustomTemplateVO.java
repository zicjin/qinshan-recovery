package com.sunwayworld.emc.module.erp.bean;

import java.io.Serializable;
import java.util.List;

/** 自定义模板 */
public class MidCustomTemplateVO implements Serializable {

    /** 模板信息 */
    private MidTemplateVO template;

    /** 商户模板信息 */
    private MidMerchantCustomTemplateVO merchantCustomTemplateVO;

    /** 字段信息 */
    private List<MidFieldVO> fields;

    /** 模块信息 */
    private List<MidModuleVO> modules;

    public MidMerchantCustomTemplateVO getMerchantCustomTemplateVO() {
        return merchantCustomTemplateVO;
    }

    public void setMerchantCustomTemplateVO(MidMerchantCustomTemplateVO merchantCustomTemplateVO) {
        this.merchantCustomTemplateVO = merchantCustomTemplateVO;
    }

    public MidTemplateVO getTemplate() {
        return template;
    }

    public void setTemplate(MidTemplateVO template) {
        this.template = template;
    }

    public List<MidFieldVO> getFields() {
        return fields;
    }

    public void setFields(List<MidFieldVO> fields) {
        this.fields = fields;
    }

    public List<MidModuleVO> getModules() {
        return modules;
    }

    public void setModules(List<MidModuleVO> modules) {
        this.modules = modules;
    }
}
