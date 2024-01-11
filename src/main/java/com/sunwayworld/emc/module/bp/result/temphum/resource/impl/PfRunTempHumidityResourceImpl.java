package com.sunwayworld.emc.module.bp.result.temphum.resource.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.sunwayworld.emc.module.bp.result.temphum.resource.PfRunTempHumidityResource;
import com.sunwayworld.emc.module.bp.result.temphum.service.PfRunTempHumidityService;
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
public class PfRunTempHumidityResourceImpl implements PfRunTempHumidityResource {

    @Autowired
    private PfRunTempHumidityService pfRunTempHumidityService;

    @Override
    public PfRunTempHumidityService getService() {
        return pfRunTempHumidityService;
    }

    @Override
    @Log(value = "同步温湿度数据", type = LogType.UPDATE)
    @RequestMapping(value = "/action/syn-temp-humidity", method = RequestMethod.POST)
    public void synTempHumidity(RestJsonWrapperBean wrapper) {
    	pfRunTempHumidityService.synTempHumidity(wrapper);
    }  
 
    @Override
    @Log(value = "同步全部温湿度数据", type = LogType.UPDATE)
    @RequestMapping(value = "/action/syn-all-temp-humidity", method = RequestMethod.POST)
    public void synAllTempHumidity(RestJsonWrapperBean wrapper) {
    	pfRunTempHumidityService.synAllTempHumidity(wrapper);
    }  
    
    
    @Override
    @Log(value = "依据样品批录入温湿度及时间", type = LogType.UPDATE)
    @RequestMapping(value = "/action/batch-update-temp-order", method = RequestMethod.POST)
    public void batchUpdateTempOrder(RestJsonWrapperBean wrapper) {
    	pfRunTempHumidityService.batchUpdateTempOrder(wrapper);
    }  
}
