package com.sunwayworld.emc.module.item.improvement.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.item.improvement.resource.EmcImprovementResource;
import com.sunwayworld.emc.module.item.improvement.service.EmcImprovementService;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author guok@sunwayworld.com
 * @date 2019-12-10
 */

@LogModule("改进措施")
@RestController
@GikamBean
public class EmcImprovementResourceImpl implements EmcImprovementResource {

    @Autowired
    private EmcImprovementService improvementService;

    @Override
    public EmcImprovementService getService() {
        return improvementService;
    }

    @Log(value = "改进措施新增", type = LogType.INSERT)
    @Override
    @RequestMapping(method = RequestMethod.POST)
    public Long insert(RestJsonWrapperBean wrapper) {
        return getService().insert(wrapper);
    }
}
