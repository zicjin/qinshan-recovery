package com.sunwayworld.emc.module.mdm.crm.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.mdm.crm.bean.CrmOpportunityResponseDTO;
import com.sunwayworld.emc.module.mdm.crm.resource.CrmUserOpenResource;
import com.sunwayworld.emc.module.mdm.crm.service.CrmUserOpenService;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author zhengxy@sunwayworld.com 2021年11月11日
 */
@GikamBean
@RestController
@LogModule(value = "CRM用户")
public class CrmUserOpenResourceImpl implements CrmUserOpenResource {

    @Autowired
    private CrmUserOpenService userOpenService;

    @Override
    @Log(value = "接收CRM用户信息", type = LogType.INSERT)
    @RequestMapping(value = "/receive/users", method = RequestMethod.POST)
    public CrmOpportunityResponseDTO receiveUser(RestJsonWrapperBean wrapper) {
        return userOpenService.receiveUser(wrapper);
    }

    @Override
    @Log(value = "同步用户信息", type = LogType.UPDATE)
    @RequestMapping(value = "/action/sync", method = RequestMethod.POST)
    public void syncUserInfo(RestJsonWrapperBean wrapper) {
        userOpenService.syncUserInfo(wrapper);
    }
}
