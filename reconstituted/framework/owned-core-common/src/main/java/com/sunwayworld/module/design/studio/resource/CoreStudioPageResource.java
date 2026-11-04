/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.design.studio.resource;

import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.module.design.studio.bean.CoreStudioPageBean;
import com.sunwayworld.module.design.studio.bean.CoreStudioPageDraftBean;
import com.sunwayworld.module.design.studio.service.CoreStudioPageService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping(value={"/secure/core/module/design/studio-pages"})
public interface CoreStudioPageResource
extends GenericResource<CoreStudioPageService, CoreStudioPageBean, Long> {
    @RequestMapping(path={"/drafts/search-by-url"}, method={RequestMethod.GET})
    public CoreStudioPageDraftBean selectDraftByUrl(@RequestParam(value="url") String var1);

    @RequestMapping(path={"/drafts"}, method={RequestMethod.POST})
    public Long saveDraft(RestJsonWrapperBean var1);

    @RequestMapping(path={"/drafts/{draftId}/action/submit"}, method={RequestMethod.PUT})
    public void submitDraft(@PathVariable(value="draftId") Long var1);

    @RequestMapping(path={"/latest/search-by-url"}, method={RequestMethod.GET})
    public CoreStudioPageBean selectLatestByUrl(@RequestParam(value="url") String var1);
}
