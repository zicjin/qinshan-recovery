package com.sunwayworld.emc.module.mdm.crm.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.mdm.crm.bean.CrmOpportunityResponseDTO;
import com.sunwayworld.emc.module.mdm.crm.resource.CrmOrgOpenResource;
import com.sunwayworld.emc.module.mdm.crm.service.CrmOrgOpenService;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author zhengxy@sunwayworld.com 2022年08月25日
 */
@GikamBean
@RestController
@LogModule(value = "CRM组织")
public class CrmOrgOpenResourceImpl implements CrmOrgOpenResource {

    @Autowired
    private CrmOrgOpenService orgOpenService;

    @Override
    @Log(value = "接收CRM组织信息", type = LogType.INSERT)
    @RequestMapping(value = "/action/receive", method = RequestMethod.POST)
    public CrmOpportunityResponseDTO receiveOrg(RestJsonWrapperBean wrapper) {
        return orgOpenService.receiveOrg(wrapper);
    }

    @Override
    @Log(value = "同步组织信息", type = LogType.UPDATE)
    @RequestMapping(value = "/action/sync", method = RequestMethod.POST)
    public void syncOrgInfo(RestJsonWrapperBean wrapper) {
        orgOpenService.syncOrgInfo(wrapper);
    }
}
