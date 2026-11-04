/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.support.helper;

import com.sunwayworld.cloud.module.item.message.service.CoreMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CoreMessageHelper {
    private static CoreMessageService messageService;

    @Autowired
    public void setMessageService(CoreMessageService messageService) {
        CoreMessageHelper.messageService = messageService;
    }

    public static void send(String subject, String content, String ... receipents) {
        messageService.send(subject, content, receipents);
    }
}
