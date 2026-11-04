/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.dingtalk.resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

public interface DingtalkMessageResource {
    @RequestMapping(value={"/send-msg"}, method={RequestMethod.POST})
    public String sendMsg(@RequestParam String var1, @RequestParam String var2, @RequestParam String var3, @RequestParam String var4, @RequestParam String var5);

    @RequestMapping(value={"/send-notificationmsg"}, method={RequestMethod.POST})
    public String sendNotificationMsg(@RequestParam String var1, @RequestParam String var2, @RequestParam int var3, @RequestParam String var4);

    @RequestMapping(value={"/clear-msg"}, method={RequestMethod.POST})
    public String clearMsg(@RequestParam String var1, @RequestParam String var2, @RequestParam String var3);
}
