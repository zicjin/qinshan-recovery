/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.page.bean;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.NotNull;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.support.domain.AbstractPersistable;
import com.sunwayworld.framework.support.domain.Orderable;

@Table(value="T_CORE_PAGE_GF_CONFIG")
public class CorePageGridFieldConfigBean
extends AbstractPersistable<Long>
implements Orderable<Long> {
    private static final long serialVersionUID = 7522956446726701951L;
    @Id
    private Long id;
    private String userId;
    private String pageId;
    private String gridId;
    private String field;
    private Long orderNo;
    private Long width;
    @NotNull(defaultValue="1")
    private String visible;
    @NotNull(defaultValue="0")
    private String fixed;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return this.userId;
    }

    public String getPageId() {
        return this.pageId;
    }

    public void setPageId(String pageId) {
        this.pageId = pageId;
    }

    public String getGridId() {
        return this.gridId;
    }

    public void setGridId(String gridId) {
        this.gridId = gridId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getField() {
        return this.field;
    }

    public void setField(String field) {
        this.field = field;
    }

    @Override
    public Long getOrderNo() {
        return this.orderNo;
    }

    @Override
    public void setOrderNo(Long orderNo) {
        this.orderNo = orderNo;
    }

    public Long getWidth() {
        return this.width;
    }

    public void setWidth(Long width) {
        this.width = width;
    }

    public String getVisible() {
        return this.visible;
    }

    public void setVisible(String visible) {
        this.visible = visible;
    }

    public String getFixed() {
        return this.fixed;
    }

    public void setFixed(String fixed) {
        this.fixed = fixed;
    }
}
