/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.design.studio.resource.impl;

import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.design.studio.bean.CoreStudioPageBean;
import com.sunwayworld.module.design.studio.bean.CoreStudioPageDraftBean;
import com.sunwayworld.module.design.studio.resource.CoreStudioPageResource;
import com.sunwayworld.module.design.studio.service.CoreStudioPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u9875\u9762\u914d\u7f6e")
@GikamBean
@RestController
public class CoreStudioPageResourceImpl
implements CoreStudioPageResource {
    @Autowired
    private CoreStudioPageService pageService;

    @Override
    public CoreStudioPageService getService() {
        return this.pageService;
    }

    @Override
    @Log(value="\u901a\u8fc7\u9875\u9762\u7684\u8bf7\u6c42\u5730\u5740\u67e5\u8be2\u5f53\u524d\u9875\u9762\u7684\u914d\u7f6e\u8349\u7a3f", type=LogType.SELECT)
    public CoreStudioPageDraftBean selectDraftByUrl(@RequestParam(value="url") String url) {
        return this.getService().selectDraftByUrl(url);
    }

    @Override
    @Log(value="\u4fdd\u5b58\u5f53\u524d\u9875\u9762\u7684\u914d\u7f6e\u8349\u7a3f", type=LogType.UPDATE)
    public Long saveDraft(RestJsonWrapperBean wrapper) {
        return this.getService().saveDraft(wrapper);
    }

    @Override
    @Log(value="\u63d0\u4ea4\u914d\u7f6e\u8349\u7a3f", type=LogType.UPDATE)
    public void submitDraft(@PathVariable(value="draftId") Long draftId) {
        this.getService().submitDraft(draftId);
    }

    @Override
    @Log(value="\u901a\u8fc7\u9875\u9762\u7684\u8bf7\u6c42\u5730\u5740\u67e5\u8be2\u5f53\u524d\u9875\u9762\u7684\u6700\u65b0\u914d\u7f6e", type=LogType.SELECT)
    public CoreStudioPageBean selectLatestByUrl(@RequestParam(value="url") String url) {
        return this.getService().selectLatestByUrl(url);
    }
}
