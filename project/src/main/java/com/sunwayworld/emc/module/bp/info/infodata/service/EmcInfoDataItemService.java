package com.sunwayworld.emc.module.bp.info.infodata.service;

import com.sunwayworld.emc.module.bp.info.infodata.bean.EmcInfoDataItemBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

public interface EmcInfoDataItemService extends GenericService<EmcInfoDataItemBean, Long> {
    Long insert(RestJsonWrapperBean wrapper);
}
