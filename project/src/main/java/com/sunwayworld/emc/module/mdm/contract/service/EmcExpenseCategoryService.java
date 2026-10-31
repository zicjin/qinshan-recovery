package com.sunwayworld.emc.module.mdm.contract.service;

import com.sunwayworld.emc.module.mdm.contract.bean.EmcExpenseCategoryBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

public interface EmcExpenseCategoryService extends GenericService<EmcExpenseCategoryBean, Long> {

    Long insert(Long contractId, RestJsonWrapperBean jsonWrapper);

    void copyExpenseCategory(RestJsonWrapperBean wrapper);

}
