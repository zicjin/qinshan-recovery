/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.ai.template.resource;

import com.sunwayworld.cloud.module.ai.template.bean.CoreAiScanResultDTO;
import com.sunwayworld.cloud.module.ai.template.bean.CoreAiTemplateBean;
import com.sunwayworld.cloud.module.ai.template.bean.CoreAiTemplateDTO;
import com.sunwayworld.cloud.module.ai.template.bean.CoreAiTemplateRotateResultDTO;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.activatable.resource.GenericActivatableCloudResource;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/module/ai/templates"})
public interface CoreAiTemplateResource
extends GenericCloudResource<CoreAiTemplateBean, Long>,
GenericActivatableCloudResource<CoreAiTemplateBean, Long> {
    public CoreAiTemplateDTO changePic(Long var1, RestJsonWrapperBean var2);

    public CoreAiScanResultDTO testScanPic(Long var1, RestJsonWrapperBean var2);

    public CoreAiTemplateDTO rotatePic(Long var1, RestJsonWrapperBean var2);

    public CoreAiTemplateRotateResultDTO frameSelectedPic(Long var1, RestJsonWrapperBean var2);

    public CoreAiTemplateDTO getTemplateDetail(Long var1);
}
