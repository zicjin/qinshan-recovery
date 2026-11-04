/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.i18n.resource.impl;

import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.i18n.CoreI18nCache;
import com.sunwayworld.module.sys.i18n.bean.CoreI18nConfigBean;
import com.sunwayworld.module.sys.i18n.resource.CoreI18nOpenResource;
import com.sunwayworld.module.sys.i18n.service.CoreI18nConfigService;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@GikamBean
@RestController
@LogModule(value="\u7cfb\u7edf\u56fd\u9645\u5316\u4fe1\u606fApi")
public class CoreI18nOpenResourceImpl
implements CoreI18nOpenResource {
    @Autowired
    private CoreI18nConfigService configService;

    @Override
    public List<CoreI18nConfigBean> selectConfigList() {
        return this.configService.selectAll();
    }

    @Override
    public Map<String, String> selectMessageMapByLocale(@RequestParam String locale) {
        return CoreI18nCache.instance().getI18nLocaleMap(locale);
    }
}
