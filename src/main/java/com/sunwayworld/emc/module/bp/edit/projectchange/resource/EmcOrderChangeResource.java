package com.sunwayworld.emc.module.bp.edit.projectchange.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.edit.projectchange.bean.EmcOrderChangeBean;
import com.sunwayworld.emc.module.bp.edit.projectchange.bean.EmcOrderItemGroupChangeBean;
import com.sunwayworld.emc.module.bp.edit.projectchange.service.EmcOrderChangeService;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcOrderItemBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.framework.support.choosable.resource.GenericChoosableResource;

@RequestMapping("/secure/emc/module/bp/edit/project-change/orders")
public interface EmcOrderChangeResource extends GenericResource<EmcOrderChangeService, EmcOrderChangeBean, Long>, GenericChoosableResource<EmcOrderChangeService, EmcOrderChangeBean, Long> {

    void edit(RestJsonWrapperBean wrapper);

    void editItem(Long orderItemGroupId, RestJsonWrapperBean wrapper);

    void terminateOrder(RestJsonWrapperBean wrapper);

    Page<EmcOrderItemGroupChangeBean> selectItemGroupPagationByOrder(Long id, RestJsonWrapperBean wrapper);

    Page<EmcOrderItemBean> selectItemPagationByOrderItemGroup(Long orderItemGroupId, RestJsonWrapperBean wrapper);

    void updateContainerInfo(RestJsonWrapperBean jsonWrapper);

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

    void editTest(RestJsonWrapperBean wrapper);

    void copyOrders(RestJsonWrapperBean wrapper);

    void setDoneTaskResult(RestJsonWrapperBean wrapper);

    void setDoneOrder(RestJsonWrapperBean wrapper);

    void setDoneSampleOrder(RestJsonWrapperBean wrapper);

}
