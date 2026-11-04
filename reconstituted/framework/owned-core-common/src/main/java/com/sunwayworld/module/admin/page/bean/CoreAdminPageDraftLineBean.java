/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.admin.page.bean;

import com.sunwayworld.framework.data.annotation.Clob;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.support.domain.AbstractPersistable;

@Table(value="T_CORE_ADMIN_PAGE_DRAFT_LINE")
public class CoreAdminPageDraftLineBean
extends AbstractPersistable<Long> {
    private static final long serialVersionUID = 1270607983066253665L;
    @Id
    private Long id;
    private Long draftId;
    private String jsUri;
    @Clob
    private String content;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getDraftId() {
        return this.draftId;
    }

    public void setDraftId(Long draftId) {
        this.draftId = draftId;
    }

    public String getJsUri() {
        return this.jsUri;
    }

    public void setJsUri(String jsUri) {
        this.jsUri = jsUri;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
