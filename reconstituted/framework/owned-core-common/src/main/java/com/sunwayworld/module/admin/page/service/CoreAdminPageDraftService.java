/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.admin.page.service;

import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.module.admin.page.bean.CoreAdminPageDraftBean;
import org.springframework.lang.Nullable;

public interface CoreAdminPageDraftService
extends GenericService<CoreAdminPageDraftBean, Long> {
    public CoreAdminPageDraftBean selectDraftIfPresent(@Nullable String var1, String var2);

    public Long insertOrUpdateDraft(RestJsonWrapperBean var1);

    public void release(Long var1);
}
