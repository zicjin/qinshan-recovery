package com.sunwayworld.emc.module.erp.bean;

import java.io.Serializable;

/**
 * @author zhengxy@sunwayworld.com 2023年10月16日
 */
public class TemplateModuleInfo implements Serializable {

    private boolean bShow;
    private int coreModuleType;
    private String gbId;
    private int moduleTabType;
    private String name;
    private int orderNum;
    private String templateGbId;

    public boolean isBShow() {
        return bShow;
    }

    public void setBShow(boolean bShow) {
        this.bShow = bShow;
    }

    public int getCoreModuleType() {
        return coreModuleType;
    }

    public void setCoreModuleType(int coreModuleType) {
        this.coreModuleType = coreModuleType;
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

    public String getTemplateGbId() {
        return templateGbId;
    }

    public void setTemplateGbId(String templateGbId) {
        this.templateGbId = templateGbId;
    }
}
