package com.sunwayworld.emc.module.mdm.basemdm.area.service;

import com.sunwayworld.emc.module.mdm.basemdm.area.bean.EmcAreaBean;
import com.sunwayworld.framework.support.activatable.service.GenericActivatableService;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;
import com.sunwayworld.framework.support.tree.service.GenericTreeService;

/**
 * @author yuanhh@sunwayworld.com 2019年9月16日
 */

public interface EmcAreaService
        extends GenericService<EmcAreaBean, Long>, GenericActivatableService<EmcAreaBean, Long>, GenericChoosableService<EmcAreaBean, Long>, GenericTreeService<EmcAreaBean, Long> {
}
