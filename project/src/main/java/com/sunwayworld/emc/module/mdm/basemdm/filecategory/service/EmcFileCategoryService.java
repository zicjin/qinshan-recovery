package com.sunwayworld.emc.module.mdm.basemdm.filecategory.service;

import com.sunwayworld.emc.module.mdm.basemdm.filecategory.bean.EmcFileCategoryBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.selectable.service.GenericSelectableService;

/**
 * @author zhengxy@sunwayworld.com 2019年10月9日
 */
public interface EmcFileCategoryService extends GenericService<EmcFileCategoryBean, Long>,
                                                GenericSelectableService<EmcFileCategoryBean, Long> {
}
