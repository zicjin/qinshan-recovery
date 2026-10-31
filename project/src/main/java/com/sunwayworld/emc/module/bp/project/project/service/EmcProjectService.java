package com.sunwayworld.emc.module.bp.project.project.service;

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
import java.util.List;

public interface EmcProjectService
extends GenericService<EmcProjectBean, Long>,
GenericChoosableService<EmcProjectBean, Long>,
GenericAuditableService<EmcProjectBean, Long> {
    public Long insert(RestJsonWrapperBean var1);

    public void copy(RestJsonWrapperBean var1);

    public Page<EmcOrderBean> selectOrderPaginationByProjectId(Long var1, RestJsonWrapperBean var2);

    public Long insertOrder(Long var1, RestJsonWrapperBean var2);

    public void deleteOrder(Long var1, RestJsonWrapperBean var2);

    public Page<EmcOrderTaskBean> selectTaskPaginationByOrderId(Long var1, RestJsonWrapperBean var2);

    public void copeOrder(RestJsonWrapperBean var1);

    public void copeQcOrder(RestJsonWrapperBean var1);

    public void submitToReport(RestJsonWrapperBean var1);

    public Page<EmcProjectBean> selectProjectStatistics(RestJsonWrapperBean var1);

    public Page<EmcProjectBean> selectAnnualProjectStatistics(RestJsonWrapperBean var1);

    public Page<EmcProjectBean> selectByReportEdit(RestJsonWrapperBean var1);

    public Page<EmcProjectBean> selectCrmInvoiceProjPagination(RestJsonWrapperBean var1);

    public Page<EmcProjectBean> selectCrmPaymentProjPagination(RestJsonWrapperBean var1);

    public Page<EmcProjectBean> selectCrmProjListByCondition(RestJsonWrapperBean var1);

    public Page<EmcProjectBean> selectProjListPagination(RestJsonWrapperBean var1);

    public Page<EmcProjectBean> selectAllProjects(RestJsonWrapperBean var1);

    public Long selectIdByKeyword(String var1);

    public Long insertPlan(RestJsonWrapperBean var1);

    public void changeSchedule(RestJsonWrapperBean var1);

    public void startProcessBySchedule(String var1);

    public void copyPlan(RestJsonWrapperBean var1);

    public void submitPlan(RestJsonWrapperBean var1);

    public void stopSchedule(RestJsonWrapperBean var1);

    public String getSequenceNo(String var1);

    public Page<EmcProjectDirectorBean> selectDirectorPaginationByProjectId(Long var1, RestJsonWrapperBean var2);

    public void insertDirector(RestJsonWrapperBean var1);

    public void deleteDirector(RestJsonWrapperBean var1);

    public List<CoreBpmnInstanceStatusDTO<Long>> startProcessBasic(RestJsonWrapperBean var1);

    public List<CoreBpmnInstanceStatusDTO<Long>> completeTaskBasic(RestJsonWrapperBean var1);

    public List<CoreBpmnInstanceStatusDTO<Long>> undoBasic(RestJsonWrapperBean var1);

    public List<CoreBpmnInstanceStatusDTO<Long>> rejectTaskBasic(RestJsonWrapperBean var1);

    public Page<EmcProjectBean> selectAnalysisAuditProject(RestJsonWrapperBean var1);

    public List<EmcProjectBean> selectProjectByContractId(Long var1);

    public EmcProjectBean selectProjectDetail(Long var1, RestJsonWrapperBean var2);

    public List<CoreBpmnInstanceStatusDTO<Long>> startProcessBasic(List<Long> var1, CoreBpmnParameterDTO var2);

    public Page<EmcProjectBean> selectTodoAnalysisAuditProject(RestJsonWrapperBean var1);

    public Page<EmcProjectBean> selectTodoAnalysisPretrialProject(RestJsonWrapperBean var1);
}
