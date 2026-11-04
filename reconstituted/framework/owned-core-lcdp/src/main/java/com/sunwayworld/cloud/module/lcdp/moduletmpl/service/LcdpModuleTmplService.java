/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.moduletmpl.service;

import com.sunwayworld.cloud.module.lcdp.moduletmpl.bean.LcdpModuleTmplBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

public interface LcdpModuleTmplService
extends GenericService<LcdpModuleTmplBean, Long> {
    public Long generateCodeSourceMoudleTmpl(RestJsonWrapperBean var1);

    public void saveCustomTemplate(RestJsonWrapperBean var1);
}
