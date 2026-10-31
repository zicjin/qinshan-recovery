package com.sunwayworld.emc.module.bp.info.infodata.resource.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.bp.info.infodata.resource.EmcInfoDataResource;
import com.sunwayworld.emc.module.bp.info.infodata.service.EmcInfoDataService;
import com.sunwayworld.emc.module.bp.info.infodata.service.EmcWaterResultService;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@LogModule("综合信息")
@RestController
@GikamBean
public class EmcInfoDataResourceImpl implements EmcInfoDataResource {
    @Autowired
    private EmcInfoDataService infoDataService;

    @Autowired
    private EmcWaterResultService waterResultService;

    @Override
    public EmcInfoDataService getService() {
        return infoDataService;
    }

    @Log(value = "综合信息计算", type = LogType.INSERT)
    @Override
    @RequestMapping(value = "/action/calculate", method = RequestMethod.POST)
    public void calculate(RestJsonWrapperBean wrapper) {
        waterResultService.calculate(wrapper);
    }

}
