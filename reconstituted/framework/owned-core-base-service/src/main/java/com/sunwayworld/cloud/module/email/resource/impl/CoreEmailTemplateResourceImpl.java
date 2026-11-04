/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.email.resource.impl;

import com.sunwayworld.cloud.module.email.bean.CoreEmailTemplateBean;
import com.sunwayworld.cloud.module.email.resource.CoreEmailTemplateResource;
import com.sunwayworld.cloud.module.email.service.CoreEmailTemplateService;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import com.sunwayworld.framework.support.choosable.resource.AbstractGenericChoosableResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@LogModule(value="\u90ae\u4ef6\u6a21\u677f\u914d\u7f6e")
@GikamBean
@RequestMapping(value={"/secure/core/module/item/email-templates"})
public class CoreEmailTemplateResourceImpl
implements CoreEmailTemplateResource,
AbstractGenericResource<CoreEmailTemplateService, CoreEmailTemplateBean, String>,
AbstractGenericChoosableResource<CoreEmailTemplateService, CoreEmailTemplateBean, String> {
    @Autowired
    private CoreEmailTemplateService emailTemplateService;

    @Override
    public CoreEmailTemplateService getService() {
        return this.emailTemplateService;
    }
}
