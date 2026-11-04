/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.office.resource.impl;

import com.sunwayworld.cloud.module.office.bean.CoreOfficeCustomizationBean;
import com.sunwayworld.cloud.module.office.resource.CoreOfficeCustomizationResource;
import com.sunwayworld.cloud.module.office.service.CoreOfficeCustomizationService;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="Office\u4e2a\u6027\u5316\u914d\u7f6e")
@RestController
@GikamBean
@RequestMapping(value={"/secure/core/module/office/customizations"})
public class CoreOfficeCustomizationResourceImpl
implements CoreOfficeCustomizationResource,
AbstractGenericResource<CoreOfficeCustomizationService, CoreOfficeCustomizationBean, String> {
    @Autowired
    private CoreOfficeCustomizationService coreOfficeCustomizationService;

    @Override
    public CoreOfficeCustomizationService getService() {
        return this.coreOfficeCustomizationService;
    }

    @Override
    @Log(value="\u67e5\u8be2\u5b9a\u5236\u5316\u914d\u7f6e", type=LogType.SELECT)
    @RequestMapping(value={"/configs"}, method={RequestMethod.GET})
    public CoreOfficeCustomizationBean selectOfficeCustomization() {
        return this.coreOfficeCustomizationService.selectOfficeCustomization();
    }
}
