package com.sunwayworld.emc.module.mdm.crm.service;

import com.sunwayworld.emc.module.mdm.crm.bean.CrmOpportunityResponseDTO;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

/**
 * @author zhengxy@sunwayworld.com 2021年11月11日
 */
public interface CrmUserOpenService {

    CrmOpportunityResponseDTO receiveUser(RestJsonWrapperBean wrapper);

    void syncUserInfo(RestJsonWrapperBean wrapper);
}
