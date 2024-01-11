package com.sunwayworld.emc.module.mdm.basemdm.filecategory.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.mdm.basemdm.filecategory.resource.EmcFileCategoryResource;
import com.sunwayworld.emc.module.mdm.basemdm.filecategory.service.EmcFileCategoryService;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author zhengxy@sunwayworld.com 2019年10月9日
 */
@LogModule(value = "文件类型")
@RestController
@GikamBean
public class EmcFileCategoryResourceImpl implements EmcFileCategoryResource {

    @Autowired
    private EmcFileCategoryService fileCategoryService;

    @Override
    public EmcFileCategoryService getService() {
        return fileCategoryService;
    }
}
