package com.sunwayworld.emc.module.mdm.crm.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.mdm.crm.bean.CrmOpportunityContractBean;
import com.sunwayworld.emc.module.mdm.crm.service.CrmOpportunityContractService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.module.item.file.bean.CoreFileBean;

/**
* @author zhengxy@sunwayworld.com 2021年08月26日
*/
@RequestMapping("/secure/emc/module/mdm/crm/contracts")
public interface CrmOpportunityContractResource extends GenericResource<CrmOpportunityContractService, CrmOpportunityContractBean, String> {

    void createProject(RestJsonWrapperBean wrapper);

    Page<CoreFileBean> selectFilePagination(RestJsonWrapperBean wrapper);

    void syncContractFile(RestJsonWrapperBean wrapper);
}
