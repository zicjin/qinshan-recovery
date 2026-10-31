package com.sunwayworld.emc.module.mdm.basemdm.testcategory.service;

import com.sunwayworld.emc.module.mdm.basemdm.testcategory.bean.EmcTestCategoryBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;
import com.sunwayworld.framework.support.selectable.service.GenericSelectableService;

public interface EmcTestCategoryService extends GenericService<EmcTestCategoryBean, Long>
                                                , GenericChoosableService<EmcTestCategoryBean, Long>
                                                , GenericSelectableService<EmcTestCategoryBean, Long>{

    Long insert(RestJsonWrapperBean wrapper);

}
