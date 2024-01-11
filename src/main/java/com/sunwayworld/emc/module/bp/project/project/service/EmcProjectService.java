package com.sunwayworld.emc.module.bp.project.project.service;

import java.util.List;

import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcProjectBean;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcProjectDirectorBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.auditable.bean.CoreBpmnParameterDTO;
import com.sunwayworld.framework.support.auditable.service.GenericAuditableService;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceStatusDTO;

/**
 * @author magh@sunwayworld.com
 * @date 2019-09-27
 */

public interface EmcProjectService extends GenericService<EmcProjectBean, Long>, GenericChoosableService<EmcProjectBean, Long>, GenericAuditableService<EmcProjectBean, Long> {

    Long insert(RestJsonWrapperBean wrapper);

    void copy(RestJsonWrapperBean wrapper);

    Page<EmcOrderBean> selectOrderPaginationByProjectId(Long id, RestJsonWrapperBean wrapper);

    Long insertOrder(Long id, RestJsonWrapperBean wrapper);

    void deleteOrder(Long id, RestJsonWrapperBean wrapper);

    Page<EmcOrderTaskBean> selectTaskPaginationByOrderId(Long orderId, RestJsonWrapperBean wrapper);

    void copeOrder(RestJsonWrapperBean jsonWrapper);

    void copeQcOrder(RestJsonWrapperBean jsonWrapper);

    void submitToReport(RestJsonWrapperBean wrapper);

    Page<EmcProjectBean> selectProjectStatistics(RestJsonWrapperBean wrapper);

    Page<EmcProjectBean> selectAnnualProjectStatistics(RestJsonWrapperBean wrapper);

    Page<EmcProjectBean> selectAllProjects(RestJsonWrapperBean wrapper);

    Long selectIdByKeyword(String keyword);

    Long insertPlan(RestJsonWrapperBean wrapper);

    void changeSchedule(RestJsonWrapperBean wrapper);

    void startProcessBySchedule(String id);

    void copyPlan(RestJsonWrapperBean wrapper);

    void submitPlan(RestJsonWrapperBean wrapper);

    void stopSchedule(RestJsonWrapperBean wrapper);

    String getSequenceNo(String bizCategoryCode);

    Page<EmcProjectDirectorBean> selectDirectorPaginationByProjectId(Long id, RestJsonWrapperBean wrapper);

    void insertDirector(RestJsonWrapperBean wrapper);

    void deleteDirector(RestJsonWrapperBean wrapper);

    List<CoreBpmnInstanceStatusDTO<Long>> startProcessBasic(RestJsonWrapperBean wrapper);

    List<CoreBpmnInstanceStatusDTO<Long>> completeTaskBasic(RestJsonWrapperBean wrapper);

    List<CoreBpmnInstanceStatusDTO<Long>> undoBasic(RestJsonWrapperBean wrapper);

    List<CoreBpmnInstanceStatusDTO<Long>> rejectTaskBasic(RestJsonWrapperBean wrapper);

    Page<EmcProjectBean> selectAnalysisAuditProject(RestJsonWrapperBean wrapper);

    List<EmcProjectBean> selectProjectByContractId(Long contractId);

    EmcProjectBean selectProjectDetail(Long id, RestJsonWrapperBean wrapper);

    List<CoreBpmnInstanceStatusDTO<Long>> startProcessBasic(List<Long> projIdList, CoreBpmnParameterDTO parameter);

    Page<EmcProjectBean> selectTodoAnalysisAuditProject(RestJsonWrapperBean wrapper);

    Page<EmcProjectBean> selectTodoAnalysisPretrialProject(RestJsonWrapperBean wrapper);
}
