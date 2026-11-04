/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.design.studio.resource;

import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.module.design.studio.bean.CoreStudioAdvSearchConfigBean;
import com.sunwayworld.module.design.studio.service.CoreStudioAdvSearchConfigService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping(value={"/secure/core/module/design/studio-advsearch-configs"})
public interface CoreStudioAdvSearchConfigResource
extends GenericResource<CoreStudioAdvSearchConfigService, CoreStudioAdvSearchConfigBean, Long> {
    @RequestMapping(method={RequestMethod.PUT})
    public Long save(RestJsonWrapperBean var1);
}
