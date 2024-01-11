package com.sunwayworld.emc.module.bp.bp.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskResultBean;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderService;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcOrderItemBean;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcOrderItemGroupBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.framework.support.choosable.resource.GenericChoosableResource;

@RequestMapping("/secure/emc/module/bp/bp/orders")
public interface EmcOrderResource extends GenericResource<EmcOrderService, EmcOrderBean, Long>, GenericChoosableResource<EmcOrderService, EmcOrderBean, Long> {

    void edit(RestJsonWrapperBean wrapper);

    void editMethod(Long orderItemGroupId, RestJsonWrapperBean wrapper);

    void editItem(Long orderItemGroupId, RestJsonWrapperBean wrapper);

    void terminateOrder(RestJsonWrapperBean wrapper);

    Page<EmcOrderItemGroupBean> selectItemGroupPagationByOrder(Long id, RestJsonWrapperBean wrapper);

    Page<EmcOrderItemBean> selectItemPagationByOrderItemGroup(Long orderItemGroupId, RestJsonWrapperBean wrapper);

    Page<EmcOrderTaskBean> selectOrderTaskByOrderId(Long id, RestJsonWrapperBean wrapper);

    void updateContainerInfo(RestJsonWrapperBean jsonWrapper);

    Page<EmcOrderTaskResultBean> selectOrderTaskResult(RestJsonWrapperBean wrapper);

    void updatePartake(RestJsonWrapperBean jsonWrapper);

    void initTaskResult(Long id, RestJsonWrapperBean wrapper);

    void setDataInputDefaultTaskResult(Long id, RestJsonWrapperBean wrapper);

    Page<EmcOrderTaskBean> selectOrderTaskEquipt(RestJsonWrapperBean wrapper);

    Page<EmcOrderTaskBean> selectOrderTaskPagationByOrder(RestJsonWrapperBean wrapper);

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

    void setDoneTaskResult(RestJsonWrapperBean wrapper);

    void setDoneOrder(RestJsonWrapperBean wrapper);

    void setDoneSampleOrder(RestJsonWrapperBean wrapper);

    Page<EmcOrderBean> selectOrderList(RestJsonWrapperBean wrapper);

}
