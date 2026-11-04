/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.scriptsource.resource;

import com.sunwayworld.cloud.module.lcdp.scriptsource.bean.LcdpScriptSourceBean;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping(value={"/secure/core/module/lcdp/script-sources"})
public interface LcdpScriptSourceResource
extends GenericCloudResource<LcdpScriptSourceBean, Long> {
    @GetMapping
    public LcdpScriptSourceBean selectByFullName(@RequestParam(value="fullName") String var1);
}
