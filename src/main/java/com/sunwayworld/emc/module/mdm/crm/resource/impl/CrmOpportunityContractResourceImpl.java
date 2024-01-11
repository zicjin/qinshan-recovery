package com.sunwayworld.emc.module.mdm.crm.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.mdm.crm.resource.CrmOpportunityContractResource;
import com.sunwayworld.emc.module.mdm.crm.service.CrmOpportunityContractService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.item.file.bean.CoreFileBean;

/**
* @author zhengxy@sunwayworld.com 2021年08月26日
*/
@GikamBean
@RestController
@LogModule(value = "商机合同")
public class CrmOpportunityContractResourceImpl implements CrmOpportunityContractResource {

    @Autowired
    private CrmOpportunityContractService crmOpportunityContractService;

    @Override
    public CrmOpportunityContractService getService() {
        return crmOpportunityContractService;
    }

    @Override
    @Log(value = "创建任务", type = LogType.SELECT)
    @RequestMapping(value = "/action/create-project", method = RequestMethod.POST)
    public void createProject(RestJsonWrapperBean wrapper) {
        getService().createProject(wrapper);
    }

    @Override
    @Log(value = "查询合同附件", type = LogType.SELECT)
    @RequestMapping(value = "/files/queries", method = RequestMethod.POST)
    public Page<CoreFileBean> selectFilePagination(RestJsonWrapperBean wrapper) {
        return getService().selectFilePagination(wrapper);
    }

    @Override
    @Log(value = "从CRM同步合同附件", type = LogType.INSERT)
    @RequestMapping(value = "/action/sync-contract-file", method = RequestMethod.POST)
    public void syncContractFile(RestJsonWrapperBean wrapper) {
        getService().syncContractFile(wrapper);
    }

}
