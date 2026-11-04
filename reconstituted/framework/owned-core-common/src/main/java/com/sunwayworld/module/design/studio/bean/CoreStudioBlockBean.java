/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.design.studio.bean;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.NotNull;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.support.domain.AbstractPersistable;
import com.sunwayworld.framework.support.domain.Persistable;

@Table(value="T_CORE_STUDIO_BLOCK")
public class CoreStudioBlockBean
extends AbstractPersistable<Long>
implements Persistable<Long> {
    private static final long serialVersionUID = -3368961083142579323L;
    @Id
    private Long id;
    private String url;
    private String blockId;
    private String blockName;
    private String tableName;
    private String category;
    @NotNull(defaultValue="1")
    private String visible;
    private Integer formColumnQty;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getBlockId() {
        return this.blockId;
    }

    public void setBlockId(String blockId) {
        this.blockId = blockId;
    }

    public String getBlockName() {
        return this.blockName;
    }

    public void setBlockName(String blockName) {
        this.blockName = blockName;
    }

    public String getTableName() {
        return this.tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getVisible() {
        return this.visible;
    }

    public void setVisible(String visible) {
        this.visible = visible;
    }

    public Integer getFormColumnQty() {
        return this.formColumnQty;
    }

    public void setFormColumnQty(Integer formColumnQty) {
        this.formColumnQty = formColumnQty;
    }
}
