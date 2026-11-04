/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.admin.page.resource.impl;

import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.admin.page.resource.CoreAdminPageDraftResource;
import com.sunwayworld.module.admin.page.service.CoreAdminPageDraftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@GikamBean
@RestController
public class CoreAdminPageDraftResourceImpl
implements CoreAdminPageDraftResource {
    @Autowired
    private CoreAdminPageDraftService draftService;

    @Override
    public CoreAdminPageDraftService getService() {
        return this.draftService;
    }

    @Override
    @Log(value="\u53d1\u5e03\u9875\u9762\u914d\u7f6e\u8349\u7a3f", type=LogType.SELECT)
    public void release(@PathVariable Long id) {
        this.draftService.release(id);
    }

    @Override
    @Log(value="\u65b0\u589e\u6216\u66f4\u65b0\u9875\u9762\u914d\u7f6e\u8349\u7a3f", type=LogType.UPDATE)
    public Long insertOrUpdateDraft(RestJsonWrapperBean wrapper) {
        return this.draftService.insertOrUpdateDraft(wrapper);
    }
}
