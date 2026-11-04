/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.i18n.resource.impl;

import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.i18n.resource.CoreI18nConfigResource;
import com.sunwayworld.module.sys.i18n.service.CoreI18nConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@GikamBean
@RestController
@LogModule(value="\u7cfb\u7edf\u56fd\u9645\u5316\u914d\u7f6e\u4fe1\u606f")
public class CoreI18nConfigResourceImpl
implements CoreI18nConfigResource {
    @Autowired
    private CoreI18nConfigService service;

    @Override
    public CoreI18nConfigService getService() {
        return this.service;
    }
}
