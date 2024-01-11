package com.sunwayworld.emc.module.bp.bp.service;

import java.util.List;

import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderContainerBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;

/**
 * @author zhangsy@sunwayworld.com
 * @date 2019-10-12
 */

public interface EmcOrderContainerService extends GenericService<EmcOrderContainerBean, Long>, GenericChoosableService<EmcOrderContainerBean, Long> {

    void updateOrder(RestJsonWrapperBean wrapper, String... updateColNames);

    void deleteContainer(RestJsonWrapperBean wrapper);

    Page<EmcOrderContainerBean> selectOrderContainerList(RestJsonWrapperBean wrapper);

    Page<EmcOrderContainerBean> selectOrderReceiveContainerList(RestJsonWrapperBean wrapper);

    List<EmcOrderContainerBean> selectOrderContainerListByRunId(RestJsonWrapperBean wrapper);

    Long updateDataRecordVersionBySample(RestJsonWrapperBean wrapper);

    void createOrderContainer(List<EmcOrderBean> orderList, List<EmcOrderTaskBean> orderTaskList);

    void deleteContainer(List<EmcOrderBean> orderList);
}
