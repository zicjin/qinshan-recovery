/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.admin.mybatis.service;

import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.module.admin.mybatis.bean.CoreAdminMybatisDraftBean;

public interface CoreAdminMybatisDraftService
extends GenericService<CoreAdminMybatisDraftBean, Long> {
    public CoreAdminMybatisDraftBean selectDraftForModification(String var1, String var2);

    public Long insertOrUpdateDraft(RestJsonWrapperBean var1);

    public void release(String var1);
}
