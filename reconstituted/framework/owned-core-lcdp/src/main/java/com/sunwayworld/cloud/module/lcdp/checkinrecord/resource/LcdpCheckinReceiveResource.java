/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.checkinrecord.resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping(value={"/core/module/lcdp/receive/checkin-records"})
public interface LcdpCheckinReceiveResource {
    @RequestMapping(value={"/action/receive"}, method={RequestMethod.POST})
    public void receive(MultipartFile var1, String var2);
}
