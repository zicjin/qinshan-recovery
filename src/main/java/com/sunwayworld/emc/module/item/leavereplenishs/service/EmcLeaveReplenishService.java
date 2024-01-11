package com.sunwayworld.emc.module.item.leavereplenishs.service;

import com.sunwayworld.emc.module.item.leavereplenishs.bean.EmcLeaveReplenishBean;
import com.sunwayworld.framework.support.auditable.service.GenericAuditableService;
import com.sunwayworld.framework.support.base.service.GenericService;

/**
 * @author zhangft@sunwayworld.com
 * @date 2019-11-14
 */

public interface EmcLeaveReplenishService extends GenericService<EmcLeaveReplenishBean, String>,
                                                  GenericAuditableService<EmcLeaveReplenishBean, String>{
}
