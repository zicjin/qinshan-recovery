package com.sunwayworld.emc.module.erp.bean;

import java.util.List;

public class ContractCustomTemplateInfo {

    /** 字段信息 */
    private List<TemplateFieldsInfo> fields;

    /** 模块信息 */
    private List<TemplateModuleInfo> modules;

    /** 模板信息 */
    private TemplateInfo template;

    /** 商户模板信息 */
    private MerchantCustomTemplateInfo merchantCustomTemplateVO;

    public List<TemplateFieldsInfo> getFields() {
        return fields;
    }

    public void setFields(List<TemplateFieldsInfo> fields) {
        this.fields = fields;
    }

    public List<TemplateModuleInfo> getModules() {
        return modules;
    }

    public void setModules(List<TemplateModuleInfo> modules) {
        this.modules = modules;
    }

    public TemplateInfo getTemplate() {
        return template;
    }

    public void setTemplate(TemplateInfo template) {
        this.template = template;
    }

    public MerchantCustomTemplateInfo getMerchantCustomTemplateVO() {
        return merchantCustomTemplateVO;
    }

    public void setMerchantCustomTemplateVO(MerchantCustomTemplateInfo merchantCustomTemplateVO) {
        this.merchantCustomTemplateVO = merchantCustomTemplateVO;
    }

    @Override
    public String toString() {
        return "ContractCustomTemplateInfo{" +
                "fields=" + fields +
                ", modules=" + modules +
                ", template=" + template +
                '}';
    }
}
