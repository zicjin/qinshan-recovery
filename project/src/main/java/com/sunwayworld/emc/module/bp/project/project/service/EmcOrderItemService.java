package com.sunwayworld.emc.module.bp.project.project.service;

import com.sunwayworld.emc.module.bp.project.project.bean.EmcOrderItemBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

public interface EmcOrderItemService extends GenericService<EmcOrderItemBean, Long> {

    void editItem(Long orderItemGroupId, RestJsonWrapperBean wrapper);

}
