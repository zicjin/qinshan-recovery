package com.sunwayworld.emc.module.item.staffabilityevaluation.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.item.staffabilityevaluation.bean.EmcStaffAbilityEvaluationBean;
import com.sunwayworld.emc.module.item.staffabilityevaluation.service.EmcStaffAbilityEvaluationService;
import com.sunwayworld.framework.support.base.resource.GenericResource;

@RequestMapping("/secure/emc/module/item/staff-ability-evaluations")
public interface EmcStaffAbilityEvaluationResource extends GenericResource<EmcStaffAbilityEvaluationService, EmcStaffAbilityEvaluationBean, Long> {
}
