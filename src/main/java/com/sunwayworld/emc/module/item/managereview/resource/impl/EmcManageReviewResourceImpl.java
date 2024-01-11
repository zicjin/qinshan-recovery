package com.sunwayworld.emc.module.item.managereview.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.item.managereview.resource.EmcManageReviewResource;
import com.sunwayworld.emc.module.item.managereview.service.EmcManageReviewService;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@LogModule("管理评审计划")
@RestController
@GikamBean
public class EmcManageReviewResourceImpl implements EmcManageReviewResource {
    @Autowired
    private EmcManageReviewService manageReviewService;

    @Override
    public EmcManageReviewService getService() {
        return manageReviewService;
    }

    @Log(value = "新增管理评审计划", type = LogType.INSERT)
    @Override
    @RequestMapping(method = RequestMethod.POST)
    public Long insert(RestJsonWrapperBean wrapper) {
        return getService().insert(wrapper);
    }
}
