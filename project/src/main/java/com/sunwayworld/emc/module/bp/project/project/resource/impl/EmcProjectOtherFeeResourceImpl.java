package com.sunwayworld.emc.module.bp.project.project.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.bp.project.project.resource.EmcProjectOtherFeeResource;
import com.sunwayworld.emc.module.bp.project.project.service.EmcProjectOtherFeeService;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author zhengxy@sunwayworld.com 2023年11月13日
 */
@LogModule("项目其他费用")
@RestController
@GikamBean
public class EmcProjectOtherFeeResourceImpl implements EmcProjectOtherFeeResource {

    @Autowired
    private EmcProjectOtherFeeService projectOtherFeeService;

    @Override
    public EmcProjectOtherFeeService getService() {
        return projectOtherFeeService;
    }
}
