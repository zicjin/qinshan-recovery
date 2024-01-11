package com.sunwayworld.emc.module.mdm.basemdm.fixedasset.resource.impl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.mdm.basemdm.fixedasset.resource.EmcFixedassetResource;
import com.sunwayworld.emc.module.mdm.basemdm.fixedasset.service.EmcFixedassetService;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
/**
 * @author cuikx@sunwayworld.com
 * @date 2019-11-25
 */

@LogModule("固定资产管理表")
@RestController
@GikamBean
public class EmcFixedassetResourceImpl implements EmcFixedassetResource {
    @Autowired
    private EmcFixedassetService fixedassetService;
    @Override
    public EmcFixedassetService getService() {
        return fixedassetService;
    }
    
    
    @Log(value = "新增固定资产管理表", type = LogType.INSERT)
    @Override
    @RequestMapping(method = RequestMethod.POST)
    public Long insert(RestJsonWrapperBean wrapper) {
        return getService().insert(wrapper);
    }
}
