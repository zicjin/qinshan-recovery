/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.i18n.resource;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.module.sys.i18n.bean.CoreI18nBean;
import com.sunwayworld.module.sys.i18n.bean.CoreI18nMessageBean;
import com.sunwayworld.module.sys.i18n.service.CoreI18nService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping(value={"/secure/core/module/sys/i18ns"})
public interface CoreI18nResource
extends GenericResource<CoreI18nService, CoreI18nBean, Long> {
    @Override
    @RequestMapping(method={RequestMethod.POST})
    public Long insert(RestJsonWrapperBean var1);

    @RequestMapping(value={"/{id}/i18n-messages/queries"}, method={RequestMethod.POST})
    public Page<CoreI18nMessageBean> selectMessagePaginationByI18nId(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/{id}/i18n-messages/action/init"}, method={RequestMethod.PUT})
    public void initMessagesByI18nId(@PathVariable Long var1);
}
