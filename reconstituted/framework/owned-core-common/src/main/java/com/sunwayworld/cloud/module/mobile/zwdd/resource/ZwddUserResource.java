/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.zwdd.resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

public interface ZwddUserResource {
    @RequestMapping(value={"/get-openid"}, method={RequestMethod.POST})
    public String getOpenId(@RequestParam String var1, @RequestParam String var2);

    @RequestMapping(value={"/get-dtuser"}, method={RequestMethod.POST})
    public String getDtUser(@RequestParam String var1, @RequestParam String var2);

    @RequestMapping(value={"/get-jssign"}, method={RequestMethod.POST})
    public String getJsSign(@RequestParam String var1);
}
