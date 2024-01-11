package com.sunwayworld.emc.module.mdm.basemdm.mtlcategory.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.mdm.basemdm.mtlcategory.resource.EmcMtlCategoryResource;
import com.sunwayworld.emc.module.mdm.basemdm.mtlcategory.service.EmcMtlCategoryService;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author magh@sunwayworld.com
 * @date 2019-09-16
 */

@LogModule("材料类型")
@RestController
@GikamBean
public class EmcMtlCategoryResourceImpl implements EmcMtlCategoryResource {
    @Autowired
    private EmcMtlCategoryService mtlCategoryService;

    @Override
    public EmcMtlCategoryService getService() {
        return mtlCategoryService;
    }

    @Log(value = "新增材料类型", type = LogType.INSERT)
    @Override
    @RequestMapping(method = RequestMethod.POST)
    public Long insert(RestJsonWrapperBean wrapper) {
        return getService().insert(wrapper);
    }
}
