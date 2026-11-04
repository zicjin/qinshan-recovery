/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.topology.bean;

import com.sunwayworld.framework.data.annotation.Clob;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.support.domain.AbstractInsertable;

@Table(value="T_CORE_TOPOLOGY")
public class CoreTopologyBean
extends AbstractInsertable<Long> {
    private static final long serialVersionUID = -526942392764011675L;
    @Id
    private Long id;
    private String code;
    private String usage;
    @Clob
    private String json;
    @Clob
    private String svg;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getUsage() {
        return this.usage;
    }

    public void setUsage(String usage) {
        this.usage = usage;
    }

    public String getJson() {
        return this.json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public String getSvg() {
        return this.svg;
    }

    public void setSvg(String svg) {
        this.svg = svg;
    }
}
