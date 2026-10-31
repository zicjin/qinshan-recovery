package com.sunwayworld.emc.module.bp.edit.projectchange.service;

import java.util.List;

import com.sunwayworld.emc.module.bp.edit.projectchange.bean.EmcOrderChangeBean;
import com.sunwayworld.emc.module.bp.edit.projectchange.bean.EmcOrderItemGroupChangeBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

public interface EmcOrderItemGroupChangeService extends GenericService<EmcOrderItemGroupChangeBean, Long> {

    void insert(List<EmcOrderChangeBean> orderList, List<EmcOrderItemGroupChangeBean> orderItemGroupList);

    void insert(List<EmcOrderChangeBean> orderList, List<EmcOrderItemGroupChangeBean> orderItemGroupList, boolean itemFlag);

    void delete(List<EmcOrderItemGroupChangeBean> orderItemGroupList);

    void editSubcontractor(RestJsonWrapperBean wrapper);

	void insertByCheckProjectItemGroup(List<EmcOrderChangeBean> orderList, List<EmcOrderItemGroupChangeBean> orderItemGroupList, boolean itemFlag);

}
