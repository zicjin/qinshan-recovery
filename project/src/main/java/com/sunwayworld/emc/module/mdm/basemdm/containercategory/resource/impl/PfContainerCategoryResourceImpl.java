package com.sunwayworld.emc.module.mdm.basemdm.containercategory.resource.impl;

import com.sunwayworld.emc.module.mdm.basemdm.containercategory.resource.PfContainerCategoryResource;
import com.sunwayworld.emc.module.mdm.basemdm.containercategory.service.PfContainerCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@LogModule(value = "容器类型")
@RestController
@GikamBean
public class PfContainerCategoryResourceImpl extends EmcContainerCategoryResourceImpl implements PfContainerCategoryResource {

    @Autowired
    private PfContainerCategoryService containerCategoryService;

    @Override
    public PfContainerCategoryService getService() {
        return containerCategoryService;
    }

    @Log(value = "修改", type = LogType.UPDATE)
    @Override
    @RequestMapping(value = "/modify/{type}",  method = RequestMethod.POST)
    public void updateOrderCategory(@PathVariable String type, RestJsonWrapperBean wrapper) {
        getService().updateOrderCategory(type,wrapper);
    }
}