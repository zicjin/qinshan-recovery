package com.sunwayworld.emc.module.mdm.basemdm.qccategory.service;

import com.sunwayworld.emc.module.mdm.basemdm.qccategory.bean.EmcQcCategoryBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;

public interface EmcQcCategoryService extends GenericService<EmcQcCategoryBean, Long>,
                                              GenericChoosableService<EmcQcCategoryBean, Long> {

}
