package com.sunwayworld.emc.module.mdm.basemdm.filecategory.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.mdm.basemdm.filecategory.bean.EmcFileCategoryBean;
import com.sunwayworld.emc.module.mdm.basemdm.filecategory.service.EmcFileCategoryService;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.framework.support.selectable.resource.GenericSelectableResource;

/**
 * @author zhengxy@sunwayworld.com 2019年10月9日
 */
@RequestMapping("/secure/emc/module/mdm/basemdm/file-categories")
public interface EmcFileCategoryResource extends GenericResource<EmcFileCategoryService, EmcFileCategoryBean, Long>,
                                                 GenericSelectableResource<EmcFileCategoryService, EmcFileCategoryBean, Long> {
}
