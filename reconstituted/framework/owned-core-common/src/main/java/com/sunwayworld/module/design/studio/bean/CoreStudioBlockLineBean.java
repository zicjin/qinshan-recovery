/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.design.studio.bean;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.NotNull;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.support.domain.AbstractPersistable;
import com.sunwayworld.framework.support.domain.Orderable;

@Table(value="T_CORE_STUDIO_BLOCK_LINE")
public class CoreStudioBlockLineBean
extends AbstractPersistable<Long>
implements Orderable<Long> {
    private static final long serialVersionUID = -4646706013385052850L;
    @Id
    private Long id;
    private Long masterId;
    private String blockId;
    private String columnName;
    private String type;
    private String json;
    @NotNull(defaultValue="1")
    private Long orderNo;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getMasterId() {
        return this.masterId;
    }

    public void setMasterId(Long masterId) {
        this.masterId = masterId;
    }

    public String getBlockId() {
        return this.blockId;
    }

    public void setBlockId(String blockId) {
        this.blockId = blockId;
    }

    public String getColumnName() {
        return this.columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getJson() {
        return this.json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    @Override
    public Long getOrderNo() {
        return this.orderNo;
    }

    @Override
    public void setOrderNo(Long orderNo) {
        this.orderNo = orderNo;
    }
}
