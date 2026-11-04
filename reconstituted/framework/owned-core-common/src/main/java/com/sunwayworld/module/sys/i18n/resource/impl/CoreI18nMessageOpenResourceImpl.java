/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.i18n.resource.impl;

import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.i18n.CoreI18nCache;
import com.sunwayworld.module.sys.i18n.resource.CoreI18nMessageOpenResource;
import java.util.Map;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@GikamBean
@RestController
@LogModule(value="\u7cfb\u7edf\u56fd\u9645\u5316\u4fe1\u606fApi")
public class CoreI18nMessageOpenResourceImpl
implements CoreI18nMessageOpenResource {
    @Override
    public Map<String, String> selectMessageMapByLocale(@RequestParam String locale) {
        return CoreI18nCache.instance().getI18nLocaleMap(locale);
    }
}
