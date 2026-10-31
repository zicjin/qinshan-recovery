package com.sunwayworld.emc.module.mdm.basemdm.area.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.mdm.basemdm.area.bean.EmcAreaBean;
import com.sunwayworld.emc.module.mdm.basemdm.area.service.EmcAreaService;
import com.sunwayworld.framework.support.activatable.resource.GenericActivatableResource;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.framework.support.choosable.resource.GenericChoosableResource;
import com.sunwayworld.framework.support.tree.resource.GenericTreeResource;

/**
 * @author yuanhh@sunwayworld.com 2019年9月16日
 */
@RequestMapping("/secure/emc/module/mdm/basemdm/areas")
public interface EmcAreaResource extends GenericResource<EmcAreaService, EmcAreaBean, Long>, GenericActivatableResource<EmcAreaService, EmcAreaBean, Long>,
        GenericChoosableResource<EmcAreaService, EmcAreaBean, Long>, GenericTreeResource<EmcAreaService, EmcAreaBean, Long> {

}
