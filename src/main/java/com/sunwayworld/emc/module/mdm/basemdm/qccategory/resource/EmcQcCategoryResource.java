package com.sunwayworld.emc.module.mdm.basemdm.qccategory.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.mdm.basemdm.qccategory.bean.EmcQcCategoryBean;
import com.sunwayworld.emc.module.mdm.basemdm.qccategory.service.EmcQcCategoryService;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.framework.support.choosable.resource.GenericChoosableResource;

@RequestMapping("/secure/emc/module/mdm/basemdm/qc-categories")
public interface EmcQcCategoryResource extends GenericResource<EmcQcCategoryService, EmcQcCategoryBean, Long>, 
                                               GenericChoosableResource<EmcQcCategoryService, EmcQcCategoryBean, Long> {

}
