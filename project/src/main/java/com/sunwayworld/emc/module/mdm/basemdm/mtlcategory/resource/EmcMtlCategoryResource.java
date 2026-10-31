package com.sunwayworld.emc.module.mdm.basemdm.mtlcategory.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.mdm.basemdm.mtlcategory.bean.EmcMtlCategoryBean;
import com.sunwayworld.emc.module.mdm.basemdm.mtlcategory.service.EmcMtlCategoryService;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.framework.support.selectable.resource.GenericSelectableResource;

/**
 * @author magh@sunwayworld.com
 * @date 2019-09-16
 */
@RequestMapping("/secure/emc/module/mdm/basemdm/mtl-categories")
public interface EmcMtlCategoryResource extends GenericResource<EmcMtlCategoryService, EmcMtlCategoryBean, Long>, GenericSelectableResource<EmcMtlCategoryService, EmcMtlCategoryBean, Long> {

}
