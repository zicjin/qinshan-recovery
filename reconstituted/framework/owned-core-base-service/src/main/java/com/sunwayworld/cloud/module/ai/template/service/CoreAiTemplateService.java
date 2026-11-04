/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.ai.template.service;

import com.sunwayworld.cloud.module.ai.template.bean.CoreAiScanResultDTO;
import com.sunwayworld.cloud.module.ai.template.bean.CoreAiTemplateBean;
import com.sunwayworld.cloud.module.ai.template.bean.CoreAiTemplateDTO;
import com.sunwayworld.cloud.module.ai.template.bean.CoreAiTemplateRotateResultDTO;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.activatable.service.GenericActivatableService;
import com.sunwayworld.framework.support.base.service.GenericService;
import java.io.File;

public interface CoreAiTemplateService
extends GenericService<CoreAiTemplateBean, Long>,
GenericActivatableService<CoreAiTemplateBean, Long> {
    public CoreAiTemplateDTO changePic(Long var1, RestJsonWrapperBean var2);

    public CoreAiScanResultDTO testScanPic(Long var1, RestJsonWrapperBean var2);

    public CoreAiTemplateDTO rotatePic(Long var1, RestJsonWrapperBean var2);

    public CoreAiTemplateRotateResultDTO frameSelectedPic(Long var1, RestJsonWrapperBean var2);

    public CoreAiTemplateDTO getTemplateDetail(Long var1);

    public CoreAiScanResultDTO generateScanResult(Long var1, Long var2);

    public File downloadPicFile(CoreAiTemplateBean var1);
}
