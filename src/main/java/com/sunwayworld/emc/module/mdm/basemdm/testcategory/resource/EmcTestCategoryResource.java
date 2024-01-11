package com.sunwayworld.emc.module.mdm.basemdm.testcategory.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.mdm.basemdm.testcategory.bean.EmcTestCategoryBean;
import com.sunwayworld.emc.module.mdm.basemdm.testcategory.service.EmcTestCategoryService;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.framework.support.choosable.resource.GenericChoosableResource;
import com.sunwayworld.framework.support.selectable.resource.GenericSelectableResource;

@RequestMapping("/secure/emc/module/mdm/basemdm/test-categories")
public interface EmcTestCategoryResource extends GenericResource<EmcTestCategoryService, EmcTestCategoryBean, Long>
                                                , GenericChoosableResource<EmcTestCategoryService, EmcTestCategoryBean, Long>
                                                , GenericSelectableResource<EmcTestCategoryService, EmcTestCategoryBean, Long>{

}
