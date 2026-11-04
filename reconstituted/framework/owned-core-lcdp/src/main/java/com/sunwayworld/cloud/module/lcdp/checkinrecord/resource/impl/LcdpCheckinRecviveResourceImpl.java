/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.checkinrecord.resource.impl;

import com.sunwayworld.cloud.module.lcdp.checkinrecord.resource.LcdpCheckinReceiveResource;
import com.sunwayworld.cloud.module.lcdp.checkinrecord.service.LcdpCheckinRecordService;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@LogModule(value="\u8fc1\u5165\u8bb0\u5f55\u8868")
@RestController
@GikamBean
public class LcdpCheckinRecviveResourceImpl
implements LcdpCheckinReceiveResource {
    @Autowired
    private LcdpCheckinRecordService lcdpCheckinRecordService;

    public LcdpCheckinRecordService getService() {
        return this.lcdpCheckinRecordService;
    }

    @Override
    @Log(value="\u63a5\u6536\u8fc1\u5165\u8d44\u6e90", type=LogType.INSERT)
    public void receive(MultipartFile file, String checkoutRecord) {
        this.getService().receive(file, checkoutRecord);
    }
}
