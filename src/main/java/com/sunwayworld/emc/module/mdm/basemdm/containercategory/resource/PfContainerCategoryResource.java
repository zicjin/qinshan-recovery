package com.sunwayworld.emc.module.mdm.basemdm.containercategory.resource;


import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

public interface PfContainerCategoryResource extends EmcContainerCategoryResource{
    void updateOrderCategory(String type, RestJsonWrapperBean wrapper);

}
