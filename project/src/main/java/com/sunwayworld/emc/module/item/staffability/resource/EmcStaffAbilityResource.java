package com.sunwayworld.emc.module.item.staffability.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.item.staffability.bean.EmcStaffAbilityBean;
import com.sunwayworld.emc.module.item.staffability.service.EmcStaffAbilityService;
import com.sunwayworld.framework.support.base.resource.GenericResource;

@RequestMapping("/secure/emc/module/item/staff-abilitys")
public interface EmcStaffAbilityResource extends GenericResource<EmcStaffAbilityService, EmcStaffAbilityBean, Long> {
}
