package com.sunwayworld.emc.module.mdm.equipt.equiptcheck.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.mdm.equipt.equiptcheck.bean.EmcEquipAppearanceBean;
import com.sunwayworld.emc.module.mdm.equipt.equiptcheck.service.EmcEquipAppearanceService;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.framework.support.choosable.resource.GenericChoosableResource;

@RequestMapping("/secure/emc/module/mdm/equipt/equip-appearances")
public interface EmcEquipAppearanceResource extends GenericResource<EmcEquipAppearanceService, EmcEquipAppearanceBean, Long>, GenericChoosableResource<EmcEquipAppearanceService, EmcEquipAppearanceBean, Long> {
}
