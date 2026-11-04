/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.code.resource;

import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.framework.support.selectable.resource.GenericSelectableResource;
import com.sunwayworld.module.sys.code.bean.CoreCodeCategoryBean;
import com.sunwayworld.module.sys.code.service.CoreCodeCategoryService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping(value={"/secure/core/module/sys/code-categories"})
public interface CoreCodeCategoryResource
extends GenericResource<CoreCodeCategoryService, CoreCodeCategoryBean, String>,
GenericSelectableResource<CoreCodeCategoryService, CoreCodeCategoryBean, String> {
    @Override
    @RequestMapping(method={RequestMethod.POST})
    public String insert(RestJsonWrapperBean var1);
}
