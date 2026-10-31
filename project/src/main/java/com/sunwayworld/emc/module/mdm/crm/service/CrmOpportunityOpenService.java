package com.sunwayworld.emc.module.mdm.crm.service;

import java.util.List;

import com.sunwayworld.emc.module.bp.project.project.bean.EmcProjectBean;
import com.sunwayworld.emc.module.mdm.crm.bean.CrmOpportunityResponseDTO;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.module.item.file.bean.CoreFileBean;

/**
 * @author zhengxy@sunwayworld.com 2021年08月31日
 */
public interface CrmOpportunityOpenService {

    CrmOpportunityResponseDTO receiveOpportunityMain(RestJsonWrapperBean wrapper);

    CrmOpportunityResponseDTO receiveOpportunityContract(RestJsonWrapperBean wrapper);

    CrmOpportunityResponseDTO receiveOpportunityExpenses(RestJsonWrapperBean wrapper);

    CrmOpportunityResponseDTO receiveOpportunityPayment(RestJsonWrapperBean wrapper);

    CrmOpportunityResponseDTO receiveOpportunityFolders(RestJsonWrapperBean wrapper);

    CrmOpportunityResponseDTO receiveOpportunityOrders(RestJsonWrapperBean wrapper);

    CrmOpportunityResponseDTO receiveOpportunityOrdtask(RestJsonWrapperBean wrapper);

    CrmOpportunityResponseDTO receiveOpportunityFiles(RestJsonWrapperBean wrapper);

    void syncProjectInfo(List<EmcProjectBean> projectList, String operateType);

    void syncProjectInfo(List<EmcProjectBean> projectList);

    void downloadAndSaveFile(List<CoreFileBean> fileList);
}
