package com.sunwayworld.emc.module.item.reportqueries.resource.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.item.reportqueries.resource.EmcReportQueryResource;
import com.sunwayworld.emc.module.item.reportqueries.service.EmcReportQueryService;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author zhangft@sunwayworld.com
 * @date 2019-11-07
 */
@LogModule("数据报告查用申请")
@RestController
@GikamBean
public class EmcReportQueryResourceImpl implements EmcReportQueryResource {
    @Autowired
    private EmcReportQueryService reportQueryService;
    @Override
    public EmcReportQueryService getService() {
        return reportQueryService;
    }
    
}
