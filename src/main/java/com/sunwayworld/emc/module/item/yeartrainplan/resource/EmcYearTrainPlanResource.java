package com.sunwayworld.emc.module.item.yeartrainplan.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.item.yeartrainplan.bean.EmcYearTrainPlanBean;
import com.sunwayworld.emc.module.item.yeartrainplan.service.EmcYearTrainPlanService;
import com.sunwayworld.framework.support.base.resource.GenericResource;

@RequestMapping("/secure/emc/module/item/year-train-plans")
public interface EmcYearTrainPlanResource extends GenericResource<EmcYearTrainPlanService, EmcYearTrainPlanBean, Long> {
}
