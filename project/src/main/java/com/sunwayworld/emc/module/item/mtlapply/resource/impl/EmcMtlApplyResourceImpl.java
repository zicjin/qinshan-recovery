package com.sunwayworld.emc.module.item.mtlapply.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.item.mtlapply.resource.EmcMtlApplyResource;
import com.sunwayworld.emc.module.item.mtlapply.service.EmcMtlApplyService;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.data.RestValidationResultBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author zhengxy@sunwayworld.com 2020年02月24日
 */
@GikamBean
@RestController
@LogModule(value = "材料领用")
public class EmcMtlApplyResourceImpl implements EmcMtlApplyResource {

    @Autowired
    private EmcMtlApplyService emcMtlApplyService;

    @Override
    public EmcMtlApplyService getService() {
        return emcMtlApplyService;
    }

    @Override
    @Log(value = "检验库存", type = LogType.SELECT)
    public RestValidationResultBean validateStock(RestJsonWrapperBean wrapper) {
        return getService().validateStock(wrapper);
    }
}
