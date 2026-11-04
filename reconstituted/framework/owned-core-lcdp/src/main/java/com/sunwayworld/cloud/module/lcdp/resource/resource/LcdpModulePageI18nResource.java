/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.resource.resource;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import java.util.Map;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping(value={"/secure/core/module/lcdp/resources/i18n-messages"})
public interface LcdpModulePageI18nResource {
    @RequestMapping(value={"/search-by-code"}, method={RequestMethod.POST})
    public Map<String, String> selectI18nMessageByCode(RestJsonWrapperBean var1);

    @RequestMapping(value={"/queries"}, method={RequestMethod.POST})
    public Page<Map<String, Object>> selectAllI18nMessage(RestJsonWrapperBean var1);

    @RequestMapping(method={RequestMethod.PUT})
    public void alterMessage(RestJsonWrapperBean var1);

    @RequestMapping(value={"/action/export"}, method={RequestMethod.POST})
    public String export(RestJsonWrapperBean var1);
}
