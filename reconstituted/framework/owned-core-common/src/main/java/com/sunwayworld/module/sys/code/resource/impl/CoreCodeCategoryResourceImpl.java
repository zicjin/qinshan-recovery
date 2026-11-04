/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.code.resource.impl;

import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.validator.data.annotation.ValidateDataWith;
import com.sunwayworld.module.sys.code.resource.CoreCodeCategoryResource;
import com.sunwayworld.module.sys.code.service.CoreCodeCategoryService;
import com.sunwayworld.module.sys.code.validator.CodeCategoryDeleteValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u7cfb\u7edf\u7f16\u7801\u7c7b\u578b")
@RestController
@GikamBean
public class CoreCodeCategoryResourceImpl
implements CoreCodeCategoryResource {
    @Autowired
    private CoreCodeCategoryService codeCategoryService;

    @Override
    public CoreCodeCategoryService getService() {
        return this.codeCategoryService;
    }

    @Override
    @Log(value="\u65b0\u589e\u5206\u7c7b\u7f16\u7801", type=LogType.INSERT)
    public String insert(RestJsonWrapperBean wrapper) {
        return this.getService().insert(wrapper);
    }

    @Override
    @Log(value="\u5220\u9664\u5206\u7c7b\u7f16\u7801", type=LogType.DELETE)
    @ValidateDataWith(value=CodeCategoryDeleteValidator.class)
    public void delete(RestJsonWrapperBean wrapper) {
        this.getService().delete(wrapper);
    }
}
