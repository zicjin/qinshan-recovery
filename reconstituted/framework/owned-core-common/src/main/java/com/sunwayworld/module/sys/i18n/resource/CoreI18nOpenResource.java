/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.i18n.resource;

import com.sunwayworld.module.sys.i18n.bean.CoreI18nConfigBean;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping(value={"/open/core/module/sys/i18ns"})
public interface CoreI18nOpenResource {
    @RequestMapping(path={"/configs"}, method={RequestMethod.GET})
    public List<CoreI18nConfigBean> selectConfigList();

    @RequestMapping(path={"/messages"}, method={RequestMethod.GET})
    public Map<String, String> selectMessageMapByLocale(@RequestParam String var1);
}
