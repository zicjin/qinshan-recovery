package com.sunwayworld.emc.module.mdm.basemdm.containercategory.service;

import com.sunwayworld.emc.module.mdm.basemdm.containercategory.bean.EmcContainerCategoryBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;

public interface EmcContainerCategoryService  extends GenericService<EmcContainerCategoryBean, Long>, GenericChoosableService<EmcContainerCategoryBean, Long> {

	void insertOrderCategory(String ids, RestJsonWrapperBean wrapper);

    void deleteOrderCategory(String ids);

    void copyOrderCategory(String ids, String monitortmplid,RestJsonWrapperBean wrapper);

    void insertByTMId(String testmethodids, RestJsonWrapperBean wrapper);

    void delOrderCategory(String ids, String testmethodids);

    void moveToOther(String oldIds, String moveIds, RestJsonWrapperBean wrapper);

    void updateOrderCategory(String type, String ids, RestJsonWrapperBean wrapper);

}
