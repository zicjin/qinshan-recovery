package com.sunwayworld.emc.module.erp.bean.contract;

import java.io.Serializable;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/** 附件VO */
public class AttachVO implements Serializable {

    @JsonSerialize(using = ToStringSerializer.class)
    /** id */
    private Long id;

    /** 附件url */
    private String url;

    /** 附件名称 */
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
