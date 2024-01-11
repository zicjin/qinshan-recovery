package com.sunwayworld.emc.module.mdm.contract.service;

import com.sunwayworld.emc.module.mdm.contract.bean.EmcContractBean;
import com.sunwayworld.emc.module.mdm.contract.bean.EmcExpenseCategoryBean;
import com.sunwayworld.emc.module.mdm.contract.bean.EmcMonitorItemBean;
import com.sunwayworld.emc.module.mdm.contract.bean.EmcReviewBasicInfoBean;
import com.sunwayworld.emc.module.mdm.contract.bean.EmcReviewContentBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.auditable.service.GenericAuditableService;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;

public interface EmcContractService extends GenericService<EmcContractBean, Long>, GenericAuditableService<EmcContractBean, Long>, GenericChoosableService<EmcContractBean, Long> {

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
