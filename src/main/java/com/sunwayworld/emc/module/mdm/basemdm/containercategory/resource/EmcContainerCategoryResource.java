package com.sunwayworld.emc.module.mdm.basemdm.containercategory.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.mdm.basemdm.containercategory.bean.EmcContainerCategoryBean;
import com.sunwayworld.emc.module.mdm.basemdm.containercategory.service.EmcContainerCategoryService;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.framework.support.choosable.resource.GenericChoosableResource;

@RequestMapping("/secure/emc/module/mdm/basemdm/container-categories")
public interface EmcContainerCategoryResource extends GenericResource<EmcContainerCategoryService, EmcContainerCategoryBean, Long>, GenericChoosableResource<EmcContainerCategoryService, EmcContainerCategoryBean, Long> {
	
	void insertOrderCategory(String ids, RestJsonWrapperBean wrapper);

    void deleteOrderCategory(String ids, RestJsonWrapperBean wrapper);

    void copyOrderCategory(String ids, String monitortmplid,RestJsonWrapperBean wrapper);

    void insertByTMId(String testmethodids, RestJsonWrapperBean wrapper);

    void delOrderCategory(String ids, String testmethodids);

    void moveToOther(String oldIds, String moveIds, RestJsonWrapperBean wrapper);

    void updateOrderCategory(String type, String ids, RestJsonWrapperBean wrapper);

}
