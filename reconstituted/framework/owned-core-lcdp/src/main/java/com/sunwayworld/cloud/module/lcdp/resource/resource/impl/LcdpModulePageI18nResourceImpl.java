/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.resource.resource.impl;

import com.sunwayworld.cloud.module.lcdp.resource.resource.LcdpModulePageI18nResource;
import com.sunwayworld.cloud.module.lcdp.resource.service.LcdpModulePageI18nService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u4f4e\u4ee3\u7801\u5e73\u53f0\u8d44\u6e90\u68c0\u51fa\u8bb0\u5f55\u8868")
@RestController
@GikamBean
public class LcdpModulePageI18nResourceImpl
implements LcdpModulePageI18nResource {
    @Autowired
    private LcdpModulePageI18nService lcdpModulePageI18nService;

    public LcdpModulePageI18nService getService() {
        return this.lcdpModulePageI18nService;
    }

    @Override
    @Log(value="\u6839\u636e\u56fd\u9645\u5316\u7f16\u7801\u67e5\u8be2\u56fd\u9645\u5316", type=LogType.SELECT)
    public Map<String, String> selectI18nMessageByCode(RestJsonWrapperBean jsonWrapper) {
        return this.getService().selectI18nMessageByCode(jsonWrapper);
    }

    @Override
    @Log(value="\u6839\u636e\u56fd\u9645\u5316\u7f16\u7801\u67e5\u8be2\u56fd\u9645\u5316", type=LogType.SELECT)
    public Page<Map<String, Object>> selectAllI18nMessage(RestJsonWrapperBean jsonWrapper) {
        return this.getService().selectAllI18nMessage(jsonWrapper);
    }

    @Override
    @Log(value="\u53d8\u66f4\u56fd\u9645\u5316\u4fe1\u606f", type=LogType.UPDATE)
    public void alterMessage(RestJsonWrapperBean wrapper) {
        this.getService().alterMessage(wrapper);
    }

    @Override
    @Log(value="\u5bfc\u51fa\u5168\u90e8\u56fd\u9645\u5316\u4fe1\u606f", type=LogType.EXPORT)
    public String export(RestJsonWrapperBean jsonWrapper) {
        return this.getService().export(jsonWrapper);
    }
}
