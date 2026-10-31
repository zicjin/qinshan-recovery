package com.sunwayworld.emc.module.erp.bean;

import java.io.Serializable;

public class MidMerchantCustomAttributesDto implements Serializable {

    /** 标识 */
    private Long id;

    /** 字段名称 */
    private String fileName;

    /** 字段内容 */
    private String value;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
