package com.sunwayworld.emc.module.erp.bean;

import java.io.Serializable;

public class MidTemplateVO implements Serializable {

    /** 模板标识 */
    private Long gbId;

    /** 模板名称 */
    private String name;


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

}
