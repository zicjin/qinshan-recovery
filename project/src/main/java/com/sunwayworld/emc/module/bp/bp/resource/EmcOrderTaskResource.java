package com.sunwayworld.emc.module.bp.bp.resource;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskLogBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskResultBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcResultLogBean;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskService;
import com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.bean.EmcOrderIndexBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.framework.support.choosable.resource.GenericChoosableResource;

@RequestMapping("/secure/emc/module/bp/bp/order-tasks")
public interface EmcOrderTaskResource extends GenericResource<EmcOrderTaskService, EmcOrderTaskBean, Long>, GenericChoosableResource<EmcOrderTaskService, EmcOrderTaskBean, Long> {
    Page<EmcOrderTaskBean> selectRunnableList(RestJsonWrapperBean wrapper);

    Page<EmcOrderTaskBean> selectRunAnalysisOrderTaskList(RestJsonWrapperBean wrapper);

    Page<EmcOrderTaskResultBean> selectOrderTaskResultByOrderTaskId(@PathVariable Long id, RestJsonWrapperBean wrapper);

    Page<EmcOrderTaskResultBean> selectAnalysisProjectTaskResultValidation(RestJsonWrapperBean wrapper);

    Page<EmcOrderIndexBean> selectOrderIndexByIndexCategoryId(@PathVariable Long id, RestJsonWrapperBean wrapper);

    Page<EmcOrderTaskBean> selectOrderTaskList(RestJsonWrapperBean wrapper);

    Long insertResultLog(Long id, RestJsonWrapperBean wrapper);

    Page<EmcResultLogBean> selectResultLogRawPaginationByOrderTaskId(Long id, RestJsonWrapperBean wrapper);

    Page<EmcOrderTaskLogBean> selectOrderTaskLogRawPaginationById(Long id, RestJsonWrapperBean wrapper);

    Page<EmcOrderTaskBean> selectAnalysisDepartmentStatistics(RestJsonWrapperBean wrapper);

    Page<EmcOrderTaskBean> selectPersonnelWorkloadStatistics(RestJsonWrapperBean wrapper);

    Page<EmcOrderTaskResultBean> selectUpdateIndexOrderTaskResult(Long id, RestJsonWrapperBean wrapper);

    void cleanResultIndex(RestJsonWrapperBean wrapper);

    void updateResultIndex(RestJsonWrapperBean wrapper);

    void updateCupNo(RestJsonWrapperBean wrapper);

    void updateEquiptAcquNo(RestJsonWrapperBean wrapper);

    void updateStockBatchNo(RestJsonWrapperBean wrapper);

	Page<EmcOrderTaskBean> selectOrderTaskByRunsList(RestJsonWrapperBean wrapper);

	Page<EmcOrderTaskBean> selectUniqueOrderTask(RestJsonWrapperBean wrapper);

	void editSubcontractor(RestJsonWrapperBean wrapper);

    void deleteSubcontractor(RestJsonWrapperBean wrapper);

    Page<EmcOrderTaskBean> selectSubpackageOrderTask(RestJsonWrapperBean wrapper);

    void syncFolderContainerInfo(RestJsonWrapperBean wrapper);
    
    void syncOrderContainerInfo(RestJsonWrapperBean wrapper);

    void syncContainerInfo(RestJsonWrapperBean wrapper);
    
    void updateSampleData(RestJsonWrapperBean wrapper);
}
