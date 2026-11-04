/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.helpdocument.bean;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.tree.bean.AbstractTreeNode;

public class CoreHelpDocumentCatalogDTO
extends AbstractTreeNode {
    @Transient
    private static final long serialVersionUID = 7033513251638841190L;
    @Id
    private String id;
    private String helpDocumentCatalog;
    private String parentId;
    private Long orderNo;

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getHelpDocumentCatalog() {
        return this.helpDocumentCatalog;
    }

    public void setHelpDocumentCatalog(String helpDocumentCatalog) {
        this.helpDocumentCatalog = helpDocumentCatalog;
    }

    @Override
    public String getParentId() {
        return this.parentId;
    }

    @Override
    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public Long getOrderNo() {
        return this.orderNo;
    }

    public void setOrderNo(Long orderNo) {
        this.orderNo = orderNo;
    }
}
