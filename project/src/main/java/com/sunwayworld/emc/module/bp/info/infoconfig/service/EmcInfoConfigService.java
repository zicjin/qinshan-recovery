package com.sunwayworld.emc.module.bp.info.infoconfig.service;

import com.sunwayworld.emc.module.bp.info.infoconfig.bean.EmcInfoConfigBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.tree.service.GenericTreeService;

public interface EmcInfoConfigService extends GenericService<EmcInfoConfigBean, Long>,GenericTreeService<EmcInfoConfigBean, Long> {
    
    Long insert(RestJsonWrapperBean wrapper);

}
