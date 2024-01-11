package com.sunwayworld.emc.module.item.staffabilitycheck.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.item.staffabilitycheck.bean.EmcStaffAbalityCheckBean;
import com.sunwayworld.emc.module.item.staffabilitycheck.service.EmcStaffAbalityCheckService;
import com.sunwayworld.framework.support.base.resource.GenericResource;

@RequestMapping("/secure/emc/module/item/staff-abality-checks")
public interface EmcStaffAbalityCheckResource extends GenericResource<EmcStaffAbalityCheckService, EmcStaffAbalityCheckBean, Long> {
}
