package com.sunwayworld.emc.module.bp.equiptcrves.resource.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.bp.equiptcrves.resource.PfAnalyticalEquiptDetailResource;
import com.sunwayworld.emc.module.bp.equiptcrves.service.PfAnalyticalEquiptDetailService;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
/**
 * @author licm@sunwayworld.com 2021年10月24日
 */
@LogModule(value = "equipt-crves-detail")
@RestController
@GikamBean
public class PfAnalyticalEquiptDetailResourceImpl implements PfAnalyticalEquiptDetailResource {

    @Autowired
    private PfAnalyticalEquiptDetailService pfAnalyticalEquiptDetailService;

    @Override
    public PfAnalyticalEquiptDetailService getService() {
        return pfAnalyticalEquiptDetailService;
    }
    
    @Log(value = "分析仪器添加", type = LogType.INSERT)
	@Override
    @RequestMapping(value = "/insert", method = RequestMethod.POST)
	public Long insert(RestJsonWrapperBean wrapper) {
        return getService().insert(wrapper);
	}
    
    @Log(value = "分析仪器曲线添加", type = LogType.INSERT)
	@Override
    @RequestMapping(value = "/add-equipt-info", method = RequestMethod.POST)
	public Long addEquiptInfo(RestJsonWrapperBean wrapper) {
        return getService().addEquiptInfo(wrapper);
	}

    
}