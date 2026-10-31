package com.sunwayworld.emc.module.bp.info.infodata.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.bp.info.infodata.bean.EmcInfoDataBean;
import com.sunwayworld.emc.module.bp.info.infodata.service.EmcInfoDataService;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericResource;

@RequestMapping("/secure/emc/module/bp/info/info-datas")
public interface EmcInfoDataResource extends GenericResource<EmcInfoDataService, EmcInfoDataBean, Long> {

    void calculate(RestJsonWrapperBean wrapper);
}
