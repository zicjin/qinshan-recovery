/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.design.studio.service;

import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.module.design.studio.bean.CoreStudioPageBean;
import com.sunwayworld.module.design.studio.bean.CoreStudioPageDraftBean;

public interface CoreStudioPageService
extends GenericService<CoreStudioPageBean, Long> {
    public CoreStudioPageBean selectLatestByUrl(String var1);

    public CoreStudioPageDraftBean selectDraftByUrl(String var1);

    public Long saveDraft(RestJsonWrapperBean var1);

    public void submitDraft(Long var1);

    public String selectJsonByPageUrl(String var1);
}
