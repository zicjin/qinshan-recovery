package com.sunwayworld.emc.module.mdm.purchapply.service;

import com.sunwayworld.emc.module.mdm.purchapply.bean.EmcPurchApplyBean;
import com.sunwayworld.framework.support.auditable.service.GenericAuditableService;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;

/**
 * @author zhanghy@sunwayworld.com 2020-09-03
 */
public interface EmcPurchApplyService extends GenericService<EmcPurchApplyBean, Long>, GenericAuditableService<EmcPurchApplyBean, Long> ,GenericChoosableService<EmcPurchApplyBean, Long> {

}
