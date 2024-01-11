package com.sunwayworld.emc.module.erp.bean;

import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

public class MidEnumVO implements Serializable {
    /** id */
    private Long id;

    /** 名字 */
    @Length(max = 256, message = "枚举值字段名称最大256位")
    private String name;

    /** 是否已经删除 */
    private Boolean bDel;

    /** 默认值 */
    private String defaultValue;

    public Boolean getbDel() {
        return bDel;
    }

    public void setbDel(Boolean bDel) {
        this.bDel = bDel;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }
}
