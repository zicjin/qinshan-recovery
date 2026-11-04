/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.ai.template.resource.impl;

import com.sunwayworld.cloud.module.ai.template.bean.CoreAiScanResultDTO;
import com.sunwayworld.cloud.module.ai.template.bean.CoreAiTemplateBean;
import com.sunwayworld.cloud.module.ai.template.bean.CoreAiTemplateDTO;
import com.sunwayworld.cloud.module.ai.template.bean.CoreAiTemplateRotateResultDTO;
import com.sunwayworld.cloud.module.ai.template.resource.CoreAiTemplateResource;
import com.sunwayworld.cloud.module.ai.template.service.CoreAiTemplateService;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.activatable.resource.AbstractGenericActivatableResource;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="AI\u6a21\u677f")
@RestController
@GikamBean
public class CoreAiTemplateResourceImpl
implements CoreAiTemplateResource,
AbstractGenericResource<CoreAiTemplateService, CoreAiTemplateBean, Long>,
AbstractGenericActivatableResource<CoreAiTemplateService, CoreAiTemplateBean, Long> {
    @Autowired
    private CoreAiTemplateService coreAiTemplateService;

    @Override
    public CoreAiTemplateService getService() {
        return this.coreAiTemplateService;
    }

    @Override
    @Log(value="\u65b0\u589e\u6a21\u677f\u8868", type=LogType.INSERT)
    @RequestMapping(method={RequestMethod.POST})
    public Long insert(RestJsonWrapperBean wrapper) {
        return (Long)this.getService().insert(wrapper);
    }

    @Override
    @Log(value="\u66f4\u6362\u56fe\u7247", type=LogType.UPDATE)
    @RequestMapping(value={"/{id}/action/change-pic"}, method={RequestMethod.POST})
    public CoreAiTemplateDTO changePic(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return this.getService().changePic(id, wrapper);
    }

    @Override
    @Log(value="\u65cb\u8f6c\u56fe\u7247", type=LogType.UPDATE)
    @RequestMapping(value={"/{id}/action/rotate-pic"}, method={RequestMethod.POST})
    public CoreAiTemplateDTO rotatePic(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return this.getService().rotatePic(id, wrapper);
    }

    @Override
    @Log(value="\u6846\u9009\u64cd\u4f5c", type=LogType.SELECT)
    @RequestMapping(value={"/{id}/action/frame-selected-pic"}, method={RequestMethod.POST})
    public CoreAiTemplateRotateResultDTO frameSelectedPic(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return this.getService().frameSelectedPic(id, wrapper);
    }

    @Override
    @Log(value="\u8bd5\u4e00\u8bd5", type=LogType.SELECT)
    @RequestMapping(value={"/{id}/action/test-scan-pic"}, method={RequestMethod.POST})
    public CoreAiScanResultDTO testScanPic(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return this.getService().testScanPic(id, wrapper);
    }

    @Override
    @Log(value="\u83b7\u53d6\u6a21\u677f\u8be6\u60c5", type=LogType.SELECT)
    @RequestMapping(value={"/{id}/detail"}, method={RequestMethod.GET})
    public CoreAiTemplateDTO getTemplateDetail(@PathVariable Long id) {
        return this.getService().getTemplateDetail(id);
    }
}
