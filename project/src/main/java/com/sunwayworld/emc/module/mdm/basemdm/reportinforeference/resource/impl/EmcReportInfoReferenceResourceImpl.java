package com.sunwayworld.emc.module.mdm.basemdm.reportinforeference.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.mdm.basemdm.reportinforeference.resource.EmcReportInfoReferenceResource;
import com.sunwayworld.emc.module.mdm.basemdm.reportinforeference.service.EmcReportInfoReferenceService;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author zhengxy@sunwayworld.com 2020年03月06日
 */
@GikamBean
@RestController
@LogModule(value = "报告信息对照")
public class EmcReportInfoReferenceResourceImpl implements EmcReportInfoReferenceResource {

    @Autowired
    private EmcReportInfoReferenceService emcReportInfoReferenceService;

    @Override
    public EmcReportInfoReferenceService getService() {
        return emcReportInfoReferenceService;
    }

    @Override
    @Log(value = "复制", type = LogType.INSERT)
    @RequestMapping(value = "/action/copy", method = RequestMethod.POST)
    public void copy(RestJsonWrapperBean wrapper) {
        getService().copy(wrapper);
    }
}
