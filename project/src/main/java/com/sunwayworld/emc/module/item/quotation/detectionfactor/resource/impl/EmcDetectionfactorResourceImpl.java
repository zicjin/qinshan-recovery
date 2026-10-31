package com.sunwayworld.emc.module.item.quotation.detectionfactor.resource.impl;

import com.sunwayworld.emc.module.item.quotation.detectionfactor.resource.EmcDetectionfactorResource;
import com.sunwayworld.emc.module.item.quotation.detectionfactor.service.EmcDetectionfactorService;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u68c0\u6d4b\u56e0\u5b50\u4ef7\u683c\u7ba1\u7406\u8868")
@RestController
@GikamBean
public class EmcDetectionfactorResourceImpl
implements EmcDetectionfactorResource {
    @Autowired
    private EmcDetectionfactorService emcDetectionfactorService;

    public EmcDetectionfactorService getService() {
        return this.emcDetectionfactorService;
    }

    @Log(value="\u65b0\u589e\u68c0\u6d4b\u56e0\u5b50\u4ef7\u683c\u7ba1\u7406\u8868", type=LogType.INSERT)
    @RequestMapping(method={RequestMethod.POST})
    public Long insert(RestJsonWrapperBean wrapper) {
        return (Long)this.getService().insert(wrapper);
    }

    @Log(value="\u6309\u5206\u6790\u9879\u7ec4-\u6dfb\u52a0\u68c0\u6d4b\u56e0\u5b50", type=LogType.INSERT)
    @RequestMapping(value={"/insertByItemGroup"}, method={RequestMethod.POST})
    public Long insertByItemGroup(RestJsonWrapperBean wrapper) {
        return this.getService().insertByItemGroup(wrapper);
    }
}
