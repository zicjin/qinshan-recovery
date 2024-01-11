package com.sunwayworld.emc.module.mdm.contract.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.mdm.contract.bean.EmcContractBean;
import com.sunwayworld.emc.module.mdm.contract.bean.EmcExpenseCategoryBean;
import com.sunwayworld.emc.module.mdm.contract.bean.EmcMonitorItemBean;
import com.sunwayworld.emc.module.mdm.contract.bean.EmcReviewBasicInfoBean;
import com.sunwayworld.emc.module.mdm.contract.bean.EmcReviewContentBean;
import com.sunwayworld.emc.module.mdm.contract.service.EmcContractService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.auditable.resource.GenericAuditableResource;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.framework.support.choosable.resource.GenericChoosableResource;

@RequestMapping("/secure/emc/module/mdm/contracts")
public interface EmcContractResource extends GenericResource<EmcContractService, EmcContractBean, Long>, GenericAuditableResource<EmcContractService, EmcContractBean, Long>,
        GenericChoosableResource<EmcContractService, EmcContractBean, Long> {

    Page<EmcReviewContentBean> selectReviewContentPaginationByContractId(Long contractId, RestJsonWrapperBean wrapper);

    void insertExpenseCategory(Long contractId, RestJsonWrapperBean wrapper);

    void deleteExpenseCategory(RestJsonWrapperBean wrapper);

    Page<EmcExpenseCategoryBean> selectExpenseCategoryPaginationByContractId(Long contractId, RestJsonWrapperBean wrapper);

    void insertMonitorItem(RestJsonWrapperBean wrapper);

    void deleteMonitorItem(RestJsonWrapperBean wrapper);

    Page<EmcMonitorItemBean> selectMonitorItemPagination(Long contractId, Long expenseCategoryId, RestJsonWrapperBean wrapper);

    void copyExpenseCategory(RestJsonWrapperBean wrapper);

    void copyContract(RestJsonWrapperBean wrapper);

    void calculateTotalPrice(Long id, RestJsonWrapperBean wrapper);

    EmcReviewBasicInfoBean selectReviewBasicInfo(Long id);
    
    Page<EmcMonitorItemBean> selectMonitorItemPagination(Long contractId, RestJsonWrapperBean wrapper);
}
