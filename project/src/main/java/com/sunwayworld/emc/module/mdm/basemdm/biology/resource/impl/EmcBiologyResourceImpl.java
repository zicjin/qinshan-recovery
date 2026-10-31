package com.sunwayworld.emc.module.mdm.basemdm.biology.resource.impl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.mdm.basemdm.biology.resource.EmcBiologyResource;
import com.sunwayworld.emc.module.mdm.basemdm.biology.service.EmcBiologyService;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
/**
 * @author cuikx@sunwayworld.com
 * @date 2019-11-28
 */

@LogModule("生物项目分析")
@RestController
@GikamBean
public class EmcBiologyResourceImpl implements EmcBiologyResource {
    @Autowired
    private EmcBiologyService biologyService;
    @Override
    public EmcBiologyService getService() {
        return biologyService;
    }
    
    
    @Log(value = "新增生物项目", type = LogType.INSERT)
    @Override
    @RequestMapping(method = RequestMethod.POST)
    public Long insert(RestJsonWrapperBean wrapper) {
        return getService().insert(wrapper);
    }
    
    
  
}
