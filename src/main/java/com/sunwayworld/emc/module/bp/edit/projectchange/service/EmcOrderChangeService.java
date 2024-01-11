package com.sunwayworld.emc.module.bp.edit.projectchange.service;

import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.edit.projectchange.bean.EmcOrderChangeBean;
import com.sunwayworld.emc.module.bp.edit.projectchange.bean.EmcOrderItemGroupChangeBean;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcOrderItemBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;

/**
 * @author zhangsy@sunwayworld.com
 * @date 2019-10-12
 */

public interface EmcOrderChangeService extends GenericService<EmcOrderChangeBean, Long>, GenericChoosableService<EmcOrderChangeBean, Long> {
    Long insert(Long projectId, RestJsonWrapperBean wrapper);

    void copy(RestJsonWrapperBean jsonWrapper);

    void copyQc(RestJsonWrapperBean jsonWrapper);

    void edit(RestJsonWrapperBean wrapper);

    void editItem(Long orderItemGroupId, RestJsonWrapperBean wrapper);

    void terminateOrder(RestJsonWrapperBean wrapper);

    Page<EmcOrderItemGroupChangeBean> selectItemGroupPagationByOrder(Long id, RestJsonWrapperBean wrapper);

    Long addOrder(Long id, RestJsonWrapperBean wrapper);

    void updateOrder(RestJsonWrapperBean wrapper, String... updateColNames);

    Page<EmcOrderItemBean> selectItemPagationByOrderItemGroup(Long orderItemGroupId, RestJsonWrapperBean wrapper);

    void updateContainerInfo(RestJsonWrapperBean jsonWrapper);

    void updatePartake(RestJsonWrapperBean jsonWrapper);

    void initTaskResult(Long id, RestJsonWrapperBean wrapper);

    void setDoneTaskResult(RestJsonWrapperBean wrapper);

    void setDoneOrder(RestJsonWrapperBean wrapper);

    void setDataInputDefaultTaskResult(Long id, RestJsonWrapperBean wrapper);

    Page<EmcOrderTaskBean> selectOrderTaskEquipt(RestJsonWrapperBean wrapper);

    Page<EmcOrderTaskBean> selectOrderTaskPagationByOrder(RestJsonWrapperBean wrapper);

    void addRepeatNo(Long id, RestJsonWrapperBean wrapper);

    void deleteRepeatNo(RestJsonWrapperBean wrapper);

    void editSubcontractor(RestJsonWrapperBean wrapper);

    void syncTmplInfo(RestJsonWrapperBean wrapper);

    void removeFomula(RestJsonWrapperBean wrapper);

    void editTest(RestJsonWrapperBean wrapper);

    void copyOrders(RestJsonWrapperBean wrapper);

    void setDoneSampleOrder(RestJsonWrapperBean wrapper);

    Page<EmcOrderChangeBean> selectOrderList(RestJsonWrapperBean wrapper);
}
