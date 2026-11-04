/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.pagetmpl.bean;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractPersistable;
import com.sunwayworld.framework.support.domain.Persistable;

@Table(value="T_LCDP_PAGE_TMPL")
public class LcdpPageTmplBean
extends AbstractPersistable<Long>
implements Persistable<Long> {
    @Transient
    private static final long serialVersionUID = -82566196618711043L;
    @Id
    private Long id;
    private String pageTmplName;
    private String childTableFlag;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getPageTmplName() {
        return this.pageTmplName;
    }

    public void setPageTmplName(String pageTmplName) {
        this.pageTmplName = pageTmplName;
    }

    public String getChildTableFlag() {
        return this.childTableFlag;
    }

    public void setChildTableFlag(String childTableFlag) {
        this.childTableFlag = childTableFlag;
    }
}
