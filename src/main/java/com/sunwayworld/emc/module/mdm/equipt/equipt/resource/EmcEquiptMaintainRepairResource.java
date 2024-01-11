package com.sunwayworld.emc.module.mdm.equipt.equipt.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptMaintainRepairBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.service.EmcEquiptMaintainRepairService;
import com.sunwayworld.framework.support.base.resource.GenericResource;

@RequestMapping("/secure/emc/module/mdm/equipt/equipt-maintain-repairs")
public interface EmcEquiptMaintainRepairResource extends GenericResource<EmcEquiptMaintainRepairService, EmcEquiptMaintainRepairBean, Long> {
}
