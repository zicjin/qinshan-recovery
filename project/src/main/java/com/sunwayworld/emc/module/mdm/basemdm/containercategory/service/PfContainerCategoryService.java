package com.sunwayworld.emc.module.mdm.basemdm.containercategory.service;

import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

public interface PfContainerCategoryService extends EmcContainerCategoryService{
    void updateOrderCategory(String type, RestJsonWrapperBean wrapper);

}
