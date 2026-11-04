/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.support.tree.bean;

import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractBaseData;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractTreeNode
extends AbstractBaseData
implements Serializable {
    @Transient
    private static final long serialVersionUID = 5018466340869297482L;
    private String id;
    private Integer lvl;
    private String parentId;
    private String text;
    private String href;
    private Boolean lazyLoad = true;
    private String[] tags;
    private Boolean checked = false;
    private Boolean expanded = false;
    private Integer childQty;
    private List<AbstractTreeNode> children;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getLvl() {
        return this.lvl;
    }

    public void setLvl(Integer lvl) {
        this.lvl = lvl;
    }

    public String getParentId() {
        return this.parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getHref() {
        return this.href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public Boolean getLazyLoad() {
        return this.lazyLoad;
    }

    public void setLazyLoad(Boolean lazyLoad) {
        this.lazyLoad = lazyLoad;
    }

    public String[] getTags() {
        return this.tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public Boolean getChecked() {
        return this.checked;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }

    public Boolean getExpanded() {
        return this.expanded;
    }

    public void setExpanded(Boolean expanded) {
        this.expanded = expanded;
    }

    public Integer getChildQty() {
        return this.childQty;
    }

    public void setChildQty(Integer childQty) {
        this.childQty = childQty;
    }

    public List<AbstractTreeNode> getChildren() {
        return this.children;
    }

    public void setChildren(List<AbstractTreeNode> children) {
        this.children = children;
    }

    public void addChild(AbstractTreeNode child) {
        if (this.children == null) {
            this.setChildren(new ArrayList<AbstractTreeNode>());
        }
        this.children.add(child);
    }
}
