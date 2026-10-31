package com.sunwayworld.emc.module.item.leavereplenishs.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.item.leavereplenishs.resource.EmcLeaveReplenishResource;
import com.sunwayworld.emc.module.item.leavereplenishs.service.EmcLeaveReplenishService;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
/**
 * @author zhangft@sunwayworld.com
 * @date 2019-11-14
 */

@LogModule("补休假")
@RestController
@GikamBean
public class EmcLeaveReplenishResourceImpl implements EmcLeaveReplenishResource {
    @Autowired
    private EmcLeaveReplenishService leaveReplenishService;
    @Override
    public EmcLeaveReplenishService getService() {
        return leaveReplenishService;
    }
    
    
    @Log(value = "新增补休假", type = LogType.INSERT)
    @Override
    @RequestMapping(method = RequestMethod.POST)
    public String insert(RestJsonWrapperBean wrapper) {
        return getService().insert(wrapper);
    }
}
