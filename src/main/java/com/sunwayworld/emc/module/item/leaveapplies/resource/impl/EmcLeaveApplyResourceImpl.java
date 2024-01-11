package com.sunwayworld.emc.module.item.leaveapplies.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.item.leaveapplies.resource.EmcLeaveApplyResource;
import com.sunwayworld.emc.module.item.leaveapplies.service.EmcLeaveApplyService;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.spring.annotation.GikamBean;
/**
 * @author zhangft@sunwayworld.com
 * @date 2019-11-12
 */

@LogModule("日常请假表")
@RestController
@GikamBean
public class EmcLeaveApplyResourceImpl implements EmcLeaveApplyResource {
    @Autowired
    private EmcLeaveApplyService leaveApplyService;
    @Override
    public EmcLeaveApplyService getService() {
        return leaveApplyService;
    }
}
