/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.email.helper;

import com.sunwayworld.cloud.module.email.service.CoreEmailService;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CoreEmailHelper {
    private static CoreEmailService emailService;

    @Autowired
    public void setEmailService(CoreEmailService emailService) {
        CoreEmailHelper.emailService = emailService;
    }

    public static void send(String configId, List<String> receipentIdList, Map<String, Object> replacementContainer, String ... ccUserIds) {
        emailService.insert(configId, receipentIdList, replacementContainer, ccUserIds);
    }

    public static void send(String configId, Map<String, Object> replacementContainer) {
        CoreEmailHelper.send(configId, Collections.emptyList(), replacementContainer, new String[0]);
    }
}
