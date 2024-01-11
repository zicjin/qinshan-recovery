package com.sunwayworld.emc.module.item.mtlapply.service;

import com.sunwayworld.emc.module.item.mtlapply.bean.EmcMtlApplyBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.data.RestValidationResultBean;
import com.sunwayworld.framework.support.auditable.service.GenericAuditableService;
import com.sunwayworld.framework.support.base.service.GenericService;

/**
 * @author zhengxy@sunwayworld.com 2020年02月24日
 */
public interface EmcMtlApplyService extends GenericService<EmcMtlApplyBean, Long>,
                                            GenericAuditableService<EmcMtlApplyBean, Long> {

    RestValidationResultBean validateStock(RestJsonWrapperBean wrapper);
}
