/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.i18n.resource.impl;

import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.i18n.resource.CoreI18nMessageResource;
import com.sunwayworld.module.sys.i18n.service.CoreI18nMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@GikamBean
@RestController
@LogModule(value="\u7cfb\u7edf\u56fd\u9645\u5316\u4fe1\u606f")
public class CoreI18nMessageResourceImpl
implements CoreI18nMessageResource {
    @Autowired
    private CoreI18nMessageService service;

    @Override
    public CoreI18nMessageService getService() {
        return this.service;
    }
}
