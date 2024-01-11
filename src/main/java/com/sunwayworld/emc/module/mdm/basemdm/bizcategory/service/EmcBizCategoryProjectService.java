package com.sunwayworld.emc.module.mdm.basemdm.bizcategory.service;

import com.sunwayworld.emc.module.mdm.basemdm.bizcategory.bean.EmcBizCategoryProjectBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;
import com.sunwayworld.framework.support.selectable.service.GenericSelectableService;

public interface EmcBizCategoryProjectService extends GenericService<EmcBizCategoryProjectBean, Long> ,
                                                      GenericChoosableService<EmcBizCategoryProjectBean, Long> ,
                                                      GenericSelectableService<EmcBizCategoryProjectBean, Long> {

    void insert(Long bizCategoryId, RestJsonWrapperBean wrapper);

}
