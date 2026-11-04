/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.code.service;

import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.selectable.service.GenericSelectableService;
import com.sunwayworld.module.sys.code.bean.CoreCodeCategoryBean;

public interface CoreCodeCategoryService
extends GenericService<CoreCodeCategoryBean, String>,
GenericSelectableService<CoreCodeCategoryBean, String> {
    @Override
    public String insert(RestJsonWrapperBean var1);
}
