package com.sunwayworld.emc.module.item.leaveapplies.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.item.leaveapplies.bean.EmcLeaveApplyBean;
import com.sunwayworld.emc.module.item.leaveapplies.service.EmcLeaveApplyService;
import com.sunwayworld.framework.support.auditable.resource.GenericAuditableResource;
import com.sunwayworld.framework.support.base.resource.GenericResource;

/**
 * @author zhangft@sunwayworld.com
 * @date 2019-11-12
 */
@RequestMapping("/secure/emc/module/item/leave-applies")
public interface EmcLeaveApplyResource extends GenericResource<EmcLeaveApplyService, EmcLeaveApplyBean, String>,
                                               GenericAuditableResource<EmcLeaveApplyService, EmcLeaveApplyBean, String> {
                
}
