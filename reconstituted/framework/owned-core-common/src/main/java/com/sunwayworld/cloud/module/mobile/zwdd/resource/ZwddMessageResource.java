/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.zwdd.resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

public interface ZwddMessageResource {
    @RequestMapping(value={"/send-msg"}, method={RequestMethod.POST})
    public String sendMsg(@RequestParam String var1, @RequestParam String var2, @RequestParam String var3, @RequestParam String var4, @RequestParam String var5);
}
