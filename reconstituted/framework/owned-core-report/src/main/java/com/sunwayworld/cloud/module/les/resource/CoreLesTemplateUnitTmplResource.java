/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.les.resource;

import com.sunwayworld.cloud.module.les.bean.CoreLesTemplateUnitTmplBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public interface CoreLesTemplateUnitTmplResource
extends GenericCloudResource<CoreLesTemplateUnitTmplBean, Long> {
    @RequestMapping(value={"/source-template-units/{unitId}"}, method={RequestMethod.POST})
    public void insertByTemplateUnit(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/source-instance-units/{unitId}"}, method={RequestMethod.POST})
    public void insertByInstanceUnit(@PathVariable Long var1, RestJsonWrapperBean var2);
}
