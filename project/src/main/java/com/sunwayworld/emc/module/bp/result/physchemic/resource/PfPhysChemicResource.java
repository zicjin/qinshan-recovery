package com.sunwayworld.emc.module.bp.result.physchemic.resource;


import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.bp.result.physchemic.bean.PfPhysChemicBean;
import com.sunwayworld.emc.module.bp.result.physchemic.service.PfPhysChemicService;
import com.sunwayworld.framework.support.base.resource.GenericResource;

@RequestMapping("/secure/emc/module/bp/result/physchemic")
public interface PfPhysChemicResource extends GenericResource<PfPhysChemicService, PfPhysChemicBean, Long> {


}
