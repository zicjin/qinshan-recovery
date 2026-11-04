/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.admin.page.resource;

import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.module.admin.page.bean.CoreAdminPageDraftBean;
import com.sunwayworld.module.admin.page.service.CoreAdminPageDraftService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping(value={"/secure/core/module/admin/page-drafts"})
public interface CoreAdminPageDraftResource
extends GenericResource<CoreAdminPageDraftService, CoreAdminPageDraftBean, Long> {
    @RequestMapping(path={"/action/insert-or-update"}, method={RequestMethod.POST})
    public Long insertOrUpdateDraft(RestJsonWrapperBean var1);

    @RequestMapping(path={"/{id}/action/release"}, method={RequestMethod.PUT})
    public void release(@PathVariable Long var1);
}
