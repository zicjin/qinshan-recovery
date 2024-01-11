package com.sunwayworld.emc.module.mdm.basemdm.testcategory.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.mdm.basemdm.testcategory.resource.EmcTestCategoryResource;
import com.sunwayworld.emc.module.mdm.basemdm.testcategory.service.EmcTestCategoryService;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@LogModule(value = "测试类型")
@RestController
@GikamBean
public class EmcTestCategoryResourceImpl implements EmcTestCategoryResource {

    @Autowired
    private EmcTestCategoryService emcTestCategoryService;

    @Override
    public EmcTestCategoryService getService() {
        return emcTestCategoryService;
    }

    @Log(value = "新增测试类型", type = LogType.INSERT)
    @Override
    public Long insert(RestJsonWrapperBean wrapper) {
        return getService().insert(wrapper);
    }

}
