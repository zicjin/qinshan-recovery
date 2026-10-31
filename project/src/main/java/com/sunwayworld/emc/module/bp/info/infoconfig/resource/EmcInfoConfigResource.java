package com.sunwayworld.emc.module.bp.info.infoconfig.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.bp.info.infoconfig.bean.EmcInfoConfigBean;
import com.sunwayworld.emc.module.bp.info.infoconfig.service.EmcInfoConfigService;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.framework.support.tree.resource.GenericTreeResource;

@RequestMapping("/secure/emc/module/bp/info/info-configs")
public interface EmcInfoConfigResource extends GenericResource<EmcInfoConfigService, EmcInfoConfigBean, Long>,GenericTreeResource<EmcInfoConfigService, EmcInfoConfigBean, Long> {

    Long insert(RestJsonWrapperBean wrapper);
    
}
