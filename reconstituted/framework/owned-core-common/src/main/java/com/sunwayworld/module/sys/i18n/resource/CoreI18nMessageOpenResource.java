/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.i18n.resource;

import java.util.Map;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping(value={"/open/core/module/sys/i18n-messages"})
public interface CoreI18nMessageOpenResource {
    @RequestMapping(method={RequestMethod.GET})
    public Map<String, String> selectMessageMapByLocale(@RequestParam String var1);
}
