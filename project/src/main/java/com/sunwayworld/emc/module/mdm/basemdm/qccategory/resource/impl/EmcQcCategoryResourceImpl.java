package com.sunwayworld.emc.module.mdm.basemdm.qccategory.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.mdm.basemdm.qccategory.resource.EmcQcCategoryResource;
import com.sunwayworld.emc.module.mdm.basemdm.qccategory.service.EmcQcCategoryService;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@LogModule(value = "QC类型")
@RestController
@GikamBean
public class EmcQcCategoryResourceImpl implements EmcQcCategoryResource {

    @Autowired
    private EmcQcCategoryService qcCategoryService;

    @Override
    public EmcQcCategoryService getService() {
        return qcCategoryService;
    }

    @Log(value = "新增QC类型", type = LogType.INSERT)
    @Override
    public Long insert(RestJsonWrapperBean wrapper) {
        return getService().insert(wrapper);
    }

}
