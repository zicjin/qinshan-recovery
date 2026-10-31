package com.sunwayworld.emc.module.bp.edit.projectchange.service;

import java.util.List;

import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.edit.projectchange.bean.EmcOrderChangeBean;
import com.sunwayworld.emc.module.bp.edit.projectchange.bean.EmcProjectChangeBean;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcOrderItemGroupBean;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcProjectDirectorBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.auditable.bean.CoreBpmnParameterDTO;
import com.sunwayworld.framework.support.auditable.service.GenericAuditableService;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceStatusDTO;

/**
 * @author magh@sunwayworld.com
 * @date 2019-09-27
 */

public interface EmcProjectChangeService extends GenericService<EmcProjectChangeBean, Long>, GenericAuditableService<EmcProjectChangeBean, Long> {

    Page<EmcOrderChangeBean> selectOrderPaginationByProjectId(Long id, RestJsonWrapperBean wrapper);

    Long insertOrder(Long id, RestJsonWrapperBean wrapper);

    void deleteOrder(Long id, RestJsonWrapperBean wrapper);

    Page<EmcOrderTaskBean> selectTaskPaginationByOrderId(Long orderId, RestJsonWrapperBean wrapper);

    void copeOrder(RestJsonWrapperBean jsonWrapper);

    void copeQcOrder(RestJsonWrapperBean jsonWrapper);

    Long selectIdByKeyword(String keyword);

    String getSequenceNo(String bizCategoryCode);

    Page<EmcProjectDirectorBean> selectDirectorPaginationByProjectId(Long id, RestJsonWrapperBean wrapper);

    void insertDirector(RestJsonWrapperBean wrapper);

    void deleteDirector(RestJsonWrapperBean wrapper);

    List<CoreBpmnInstanceStatusDTO<Long>> startProcessBasic(RestJsonWrapperBean wrapper);

    List<CoreBpmnInstanceStatusDTO<Long>> completeTaskBasic(RestJsonWrapperBean wrapper);

    List<CoreBpmnInstanceStatusDTO<Long>> undoBasic(RestJsonWrapperBean wrapper);

    List<CoreBpmnInstanceStatusDTO<Long>> rejectTaskBasic(RestJsonWrapperBean wrapper);

    EmcProjectChangeBean selectProjectDetail(Long id, RestJsonWrapperBean wrapper);

    List<CoreBpmnInstanceStatusDTO<Long>> startProcessBasic(List<Long> projIdList, CoreBpmnParameterDTO parameter);

    void changeProject(List<EmcProjectChangeBean> projectChangeList);

    void syncNewOrder(List<EmcOrderBean> orderList);

    void syncNewTest(List<EmcOrderBean> orderList);

    void deleteSyncOrder(List<EmcOrderBean> orderList);

    void deleteSyncTest(List<EmcOrderItemGroupBean> orderItemGroupList);

}
