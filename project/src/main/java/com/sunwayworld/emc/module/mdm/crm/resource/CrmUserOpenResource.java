package com.sunwayworld.emc.module.mdm.crm.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.mdm.crm.bean.CrmOpportunityResponseDTO;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

/**
 * @author zhengxy@sunwayworld.com 2021年11月11日
 */
@RequestMapping("/open/emc/module/mdm/crm/users")
public interface CrmUserOpenResource {

    CrmOpportunityResponseDTO receiveUser(RestJsonWrapperBean wrapper);

    void syncUserInfo(RestJsonWrapperBean wrapper);
}
