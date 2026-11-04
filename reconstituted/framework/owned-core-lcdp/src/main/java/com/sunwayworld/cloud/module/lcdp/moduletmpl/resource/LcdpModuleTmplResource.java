/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.moduletmpl.resource;

import com.sunwayworld.cloud.module.lcdp.moduletmpl.bean.LcdpModuleTmplBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping(value={"/secure/core/module/lcdp/module-tmpls"})
public interface LcdpModuleTmplResource
extends GenericCloudResource<LcdpModuleTmplBean, Long> {
    @RequestMapping(value={"/save-custom-template"}, method={RequestMethod.POST})
    public void saveCustomTemplate(RestJsonWrapperBean var1);
}
