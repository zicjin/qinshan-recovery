package com.sunwayworld.emc.module.bp.bp.service;

import java.util.List;

import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskResultBean;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcOrderItemBean;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcOrderItemGroupBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;

/**
 * @author zhangsy@sunwayworld.com
 * @date 2019-10-12
 */

public interface EmcOrderService extends GenericService<EmcOrderBean, Long>, GenericChoosableService<EmcOrderBean, Long> {
    Long insert(Long projectId, RestJsonWrapperBean wrapper);

    void copyCertTestMethod(RestJsonWrapperBean wrapper);

    void copy(RestJsonWrapperBean jsonWrapper);

    void copyQc(RestJsonWrapperBean jsonWrapper);

    void edit(RestJsonWrapperBean wrapper);

    void editMethod(Long orderItemGroupId, RestJsonWrapperBean wrapper);

    void editItem(Long orderItemGroupId, RestJsonWrapperBean wrapper);

    void terminateOrder(RestJsonWrapperBean wrapper);

    Page<EmcOrderItemGroupBean> selectItemGroupPagationByOrder(Long id, RestJsonWrapperBean wrapper);

    Long addOrder(Long id, RestJsonWrapperBean wrapper);

    void updateOrder(RestJsonWrapperBean wrapper, String ... updateColNames);

    Page<EmcOrderItemBean> selectItemPagationByOrderItemGroup(Long orderItemGroupId, RestJsonWrapperBean wrapper);

    void insertBpDataByProject(List<Long> projectIdList);

    Page<EmcOrderTaskBean> selectContainerInfoByOrderId(Long id, RestJsonWrapperBean jsonWrapper);

    Page<EmcOrderTaskResultBean> selectOrderTaskResult(RestJsonWrapperBean jsonWrapper);

    void updateContainerInfo(RestJsonWrapperBean jsonWrapper);

    void updatePartake(RestJsonWrapperBean jsonWrapper);

    void initTaskResult(Long id, RestJsonWrapperBean wrapper);

    void setDoneTaskResult(RestJsonWrapperBean wrapper);

    void setDoneOrder(RestJsonWrapperBean wrapper);

    void setDataInputDefaultTaskResult(Long id, RestJsonWrapperBean wrapper);

    Page<EmcOrderTaskBean> selectOrderTaskEquipt(RestJsonWrapperBean wrapper);

    Page<EmcOrderTaskBean> selectOrderTaskPagationByOrder(RestJsonWrapperBean wrapper);

    Page<EmcOrderTaskBean> selectOrderTaskPagationByOrderAndTask(RestJsonWrapperBean wrapper);

    void addRepeatNo(Long id, RestJsonWrapperBean wrapper);

    void deleteRepeatNo(RestJsonWrapperBean wrapper);

    void editSubcontractor(RestJsonWrapperBean wrapper);

    void syncTmplInfo(RestJsonWrapperBean wrapper);

    void removeFomula(RestJsonWrapperBean wrapper);

    void setDefaultTaskResult(Long id, RestJsonWrapperBean wrapper);

    void addCheckProjectItemGroup(Long id, RestJsonWrapperBean wrapper);
    
    void delCheckProjectItemGroup(RestJsonWrapperBean wrapper);

    void editCheckProjectItem(Long orderTaskId, RestJsonWrapperBean wrapper);

    void editCheckMethodItem(Long orderTaskId, RestJsonWrapperBean wrapper);

    void editTest(RestJsonWrapperBean wrapper);

    void copyOrders(RestJsonWrapperBean wrapper);

    void setDoneSampleOrder(RestJsonWrapperBean wrapper);

    Page<EmcOrderBean> selectOrderList(RestJsonWrapperBean wrapper);

    void editCheckProjectItemGroup(Long id, RestJsonWrapperBean wrapper);
}
