/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.design.studio.resource;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.module.design.studio.bean.CoreStudioBlockBean;
import com.sunwayworld.module.design.studio.bean.CoreStudioBlockLineBean;
import com.sunwayworld.module.design.studio.service.CoreStudioBlockService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping(value={"/secure/core/module/design/studio-blocks"})
public interface CoreStudioBlockResource
extends GenericResource<CoreStudioBlockService, CoreStudioBlockBean, Long> {
    @RequestMapping(value={"/queries/by-page-url"}, method={RequestMethod.POST})
    public Page<CoreStudioBlockBean> selectPaginationByPageUrl(RestJsonWrapperBean var1);

    @RequestMapping(value={"/{id}/lines/queries"}, method={RequestMethod.POST})
    public Page<CoreStudioBlockLineBean> selectLinePaginationByMasterId(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(method={RequestMethod.PUT})
    public Long save(RestJsonWrapperBean var1);
}
