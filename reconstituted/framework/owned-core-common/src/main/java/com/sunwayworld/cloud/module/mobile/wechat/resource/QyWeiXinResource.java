/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

public interface QyWeiXinResource {
    @RequestMapping(value={"/get-openid"}, method={RequestMethod.POST})
    public String getOpenId(@RequestParam String var1);

    @RequestMapping(value={"/get-wxuser"}, method={RequestMethod.POST})
    public String getWxUser(@RequestParam String var1);

    @RequestMapping(value={"/get-jssign"}, method={RequestMethod.POST})
    public String getJsSign(@RequestParam String var1);

    @RequestMapping(value={"/send-msg"}, method={RequestMethod.POST})
    public String sendMsg(@RequestParam String var1, @RequestParam String var2, @RequestParam String var3, @RequestParam String var4, @RequestParam String var5);
}
