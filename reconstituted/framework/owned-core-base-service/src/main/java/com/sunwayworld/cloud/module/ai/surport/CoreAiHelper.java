/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.ai.surport;

import com.sunwayworld.cloud.module.ai.template.bean.CoreAiScanResultDTO;
import com.sunwayworld.cloud.module.ai.template.service.CoreAiTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CoreAiHelper {
    private static CoreAiTemplateService aiTemplateService;

    @Autowired
    public void setAiTemplateService(CoreAiTemplateService aiTemplateService) {
        CoreAiHelper.aiTemplateService = aiTemplateService;
    }

    public static CoreAiScanResultDTO generateScanResult(Long templateId, Long fileId) {
        return aiTemplateService.generateScanResult(templateId, fileId);
    }
}
