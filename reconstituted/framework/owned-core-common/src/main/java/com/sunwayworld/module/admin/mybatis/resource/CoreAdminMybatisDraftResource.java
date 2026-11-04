/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.admin.mybatis.resource;

import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.module.admin.mybatis.bean.CoreAdminMybatisDraftBean;
import com.sunwayworld.module.admin.mybatis.service.CoreAdminMybatisDraftService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping(value={"/secure/core/module/admin/mybatis-drafts"})
public interface CoreAdminMybatisDraftResource
extends GenericResource<CoreAdminMybatisDraftService, CoreAdminMybatisDraftBean, Long> {
    @Log(value="\u67e5\u8be2\u8349\u7a3f", type=LogType.SELECT)
    @RequestMapping(method={RequestMethod.GET})
    public CoreAdminMybatisDraftBean selectDraftForModification(@RequestParam String var1, @RequestParam String var2);

    @Log(value="\u65b0\u589e\u6216\u66f4\u65b0\u8349\u7a3f", type=LogType.UPDATE)
    @RequestMapping(path={"/action/insert-or-update"}, method={RequestMethod.POST})
    public Long insertOrUpdateDraft(RestJsonWrapperBean var1);
}
