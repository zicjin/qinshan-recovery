package com.sunwayworld.emc.module.mdm.basemdm.area.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.mdm.basemdm.area.resource.EmcAreaResource;
import com.sunwayworld.emc.module.mdm.basemdm.area.service.EmcAreaService;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
/**
 * @author yuanhh@sunwayworld.com 2019年9月16日
 */

@LogModule("区域")
@RestController
@GikamBean
public class EmcAreaResourceImpl implements EmcAreaResource {
    @Autowired
    private EmcAreaService areaService;
    @Override
    public EmcAreaService getService() {
        return areaService;
    }
    
    
    @Log(value = "新增区域表", type = LogType.INSERT)
    @Override
    @RequestMapping(method = RequestMethod.POST)
    public Long insert(RestJsonWrapperBean wrapper) {
        return getService().insert(wrapper);
    }
}
