package com.sunwayworld.emc.module.mdm.crm.service;

import com.sunwayworld.emc.module.mdm.crm.bean.CrmOpportunityContractBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.module.item.file.bean.CoreFileBean;

/**
* @author zhengxy@sunwayworld.com 2021年08月26日
*/
public interface CrmOpportunityContractService extends GenericService<CrmOpportunityContractBean, String> {

    void createProject(RestJsonWrapperBean wrapper);

    Page<CoreFileBean> selectFilePagination(RestJsonWrapperBean wrapper);

    void syncContractFile(RestJsonWrapperBean wrapper);
}
