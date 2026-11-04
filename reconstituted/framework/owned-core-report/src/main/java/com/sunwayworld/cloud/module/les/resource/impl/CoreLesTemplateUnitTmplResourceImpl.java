/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.les.resource.impl;

import com.sunwayworld.cloud.module.les.bean.CoreLesTemplateUnitTmplBean;
import com.sunwayworld.cloud.module.les.resource.CoreLesTemplateUnitTmplResource;
import com.sunwayworld.cloud.module.les.service.CoreLesTemplateUnitTmplService;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@LogModule(value="LES\u6a21\u677f")
@GikamBean
@RequestMapping(value={"/secure/core/les/les-template-unit-tmpls"})
public class CoreLesTemplateUnitTmplResourceImpl
implements CoreLesTemplateUnitTmplResource,
AbstractGenericResource<CoreLesTemplateUnitTmplService, CoreLesTemplateUnitTmplBean, Long> {
    @Autowired
    private CoreLesTemplateUnitTmplService unitTmplService;

    @Override
    public CoreLesTemplateUnitTmplService getService() {
        return this.unitTmplService;
    }

    @Override
    @Log(value="\u901a\u8fc7LES\u6a21\u677f\u7ec4\u4ef6\u6765\u65b0\u589e\u7ec4\u4ef6\u6a21\u677f", type=LogType.INSERT)
    public void insertByTemplateUnit(@PathVariable Long unitId, RestJsonWrapperBean wrapper) {
        this.getService().insertByTemplateUnit(unitId, wrapper);
    }

    @Override
    @Log(value="\u901a\u8fc7LES\u5b9e\u4f8b\u7ec4\u4ef6\u6765\u65b0\u589e\u7ec4\u4ef6\u6a21\u677f", type=LogType.INSERT)
    public void insertByInstanceUnit(@PathVariable Long unitId, RestJsonWrapperBean wrapper) {
        this.getService().insertByInstanceUnit(unitId, wrapper);
    }
}
