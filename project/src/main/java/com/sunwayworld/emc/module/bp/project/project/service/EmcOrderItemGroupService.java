package com.sunwayworld.emc.module.bp.project.project.service;

import java.util.List;

import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderBean;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcOrderItemGroupBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

public interface EmcOrderItemGroupService extends GenericService<EmcOrderItemGroupBean, Long> {

    void insert(List<EmcOrderBean> orderList, List<EmcOrderItemGroupBean> orderItemGroupList);
    
    void insert(List<EmcOrderBean> orderList, List<EmcOrderItemGroupBean> orderItemGroupList, boolean itemFlag);

    void delete(List<EmcOrderItemGroupBean> orderItemGroupList);

    void editMethod(Long id, RestJsonWrapperBean wrapper);

    void editSubcontractor(RestJsonWrapperBean wrapper);

	void insertByCheckProjectItemGroup(List<EmcOrderBean> orderList, List<EmcOrderItemGroupBean> orderItemGroupList,
			boolean itemFlag);

}
