package com.sunwayworld.emc.module.item.leaveapplies.service;

import com.sunwayworld.emc.module.item.leaveapplies.bean.EmcLeaveApplyBean;
import com.sunwayworld.framework.support.auditable.service.GenericAuditableService;
import com.sunwayworld.framework.support.base.service.GenericService;

/**
 * @author zhangft@sunwayworld.com
 * @date 2019-11-12
 */

public interface EmcLeaveApplyService extends GenericService<EmcLeaveApplyBean, String>,
                                              GenericAuditableService<EmcLeaveApplyBean, String>{
}
