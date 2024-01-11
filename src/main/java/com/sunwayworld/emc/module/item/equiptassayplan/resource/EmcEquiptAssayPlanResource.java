package com.sunwayworld.emc.module.item.equiptassayplan.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.item.equiptassayplan.bean.EmcEquiptAssayPlanBean;
import com.sunwayworld.emc.module.item.equiptassayplan.service.EmcEquiptAssayPlanService;
import com.sunwayworld.framework.support.base.resource.GenericResource;

@RequestMapping("/secure/emc/module/item/equipt-assay-plans")
public interface EmcEquiptAssayPlanResource extends GenericResource<EmcEquiptAssayPlanService, EmcEquiptAssayPlanBean, Long> {
}
