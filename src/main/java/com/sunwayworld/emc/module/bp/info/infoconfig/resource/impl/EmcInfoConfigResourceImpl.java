package com.sunwayworld.emc.module.bp.info.infoconfig.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.bp.info.infoconfig.resource.EmcInfoConfigResource;
import com.sunwayworld.emc.module.bp.info.infoconfig.service.EmcInfoConfigService;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@LogModule("综合信息配置")
@RestController
@GikamBean
public class EmcInfoConfigResourceImpl implements EmcInfoConfigResource {
    @Autowired
    private EmcInfoConfigService infoConfigService;

    @Override
    public EmcInfoConfigService getService() {
        return infoConfigService;
    }

    @Log(value = "新增综合信息配置", type = LogType.INSERT)
    @Override
    @RequestMapping(method = RequestMethod.POST)
    public Long insert(RestJsonWrapperBean wrapper) {
        return getService().insert(wrapper);
    }

}
