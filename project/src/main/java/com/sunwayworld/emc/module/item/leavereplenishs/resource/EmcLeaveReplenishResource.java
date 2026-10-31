package com.sunwayworld.emc.module.item.leavereplenishs.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.item.leavereplenishs.bean.EmcLeaveReplenishBean;
import com.sunwayworld.emc.module.item.leavereplenishs.service.EmcLeaveReplenishService;
import com.sunwayworld.framework.support.auditable.resource.GenericAuditableResource;
import com.sunwayworld.framework.support.base.resource.GenericResource;

/**
 * @author zhangft@sunwayworld.com
 * @date 2019-11-14
 */
@RequestMapping("/secure/emc/module/item/leave-replenishs")
public interface EmcLeaveReplenishResource extends GenericResource<EmcLeaveReplenishService, EmcLeaveReplenishBean, String>,
                                                   GenericAuditableResource<EmcLeaveReplenishService, EmcLeaveReplenishBean, String> {
        
}
