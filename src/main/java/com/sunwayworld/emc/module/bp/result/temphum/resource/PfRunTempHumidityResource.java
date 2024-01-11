package com.sunwayworld.emc.module.bp.result.temphum.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.bp.result.temphum.bean.PfRunTempHumidityBean;
import com.sunwayworld.emc.module.bp.result.temphum.service.PfRunTempHumidityService;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericResource;

@RequestMapping("/secure/emc/module/bp/result/temphum")
public interface PfRunTempHumidityResource extends GenericResource<PfRunTempHumidityService, PfRunTempHumidityBean, Long> {

    void synTempHumidity(RestJsonWrapperBean wrapper);

    void synAllTempHumidity(RestJsonWrapperBean wrapper);

    void batchUpdateTempOrder(RestJsonWrapperBean wrapper);

}
