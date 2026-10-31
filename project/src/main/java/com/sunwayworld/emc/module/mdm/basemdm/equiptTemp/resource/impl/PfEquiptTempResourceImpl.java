package com.sunwayworld.emc.module.mdm.basemdm.equiptTemp.resource.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.sunwayworld.emc.module.mdm.basemdm.equiptTemp.resource.PfEquiptTempResource;
import com.sunwayworld.emc.module.mdm.basemdm.equiptTemp.service.PfEquiptTempService;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author licm@sunwayworld.com 2021年10月24日
 */
@LogModule(value = "temp-humidity")
@RestController
@GikamBean
public class PfEquiptTempResourceImpl implements PfEquiptTempResource {

    @Autowired
    private PfEquiptTempService pfEquiptTempService;

    @Override
    public PfEquiptTempService getService() {
        return pfEquiptTempService;
    }

    @Override
    @Log(value = "批量新增仪器温度数据", type = LogType.INSERT)
    @RequestMapping(value = "/action/batch-insert-temp", method = RequestMethod.POST)
    public void batchInsertTemp(RestJsonWrapperBean wrapper) {
    	pfEquiptTempService.batchInsertTemp(wrapper);
    }  
}
