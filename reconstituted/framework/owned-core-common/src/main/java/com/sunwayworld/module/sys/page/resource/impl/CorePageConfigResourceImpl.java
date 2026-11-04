/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.page.resource.impl;

import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.page.bean.CorePageConfigDTO;
import com.sunwayworld.module.sys.page.resource.CorePageConfigResource;
import com.sunwayworld.module.sys.page.service.CorePageConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@GikamBean
@RestController
@LogModule(value="\u9875\u9762\u914d\u7f6e")
public class CorePageConfigResourceImpl
implements CorePageConfigResource {
    @Autowired
    private CorePageConfigService corePageConfigService;

    public CorePageConfigService getService() {
        return this.corePageConfigService;
    }

    @Override
    @Log(value="\u67e5\u8be2\u6307\u5b9a\u9875\u9762\u7684\u5217\u914d\u7f6e\u4fe1\u606f", type=LogType.SELECT)
    @RequestMapping(method={RequestMethod.GET})
    public CorePageConfigDTO selectCurrentPageConfig(String pageId) {
        return this.getService().selectCurrentPageConfig(pageId);
    }
}
