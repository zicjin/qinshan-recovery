package com.sunwayworld.emc.module.item.correction.service;

import com.sunwayworld.emc.module.item.correction.bean.EmcCorrectionBean;
import com.sunwayworld.framework.support.auditable.service.GenericAuditableService;
import com.sunwayworld.framework.support.base.service.GenericService;

/**
 * @author zhangft@sunwayworld.com
 * @date 2019-11-25
 */

public interface EmcCorrectionService extends GenericService<EmcCorrectionBean, Long>, 
                                              GenericAuditableService<EmcCorrectionBean, Long> {
}
