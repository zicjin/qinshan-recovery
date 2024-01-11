package com.sunwayworld.emc.module.bp.bp.service;

import java.util.List;

import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskLogBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskResultBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcResultLogBean;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcOrderItemGroupBean;
import com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.bean.EmcOrderIndexBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;

/**
 * @author zhangsy@sunwayworld.com
 * @date 2019-10-12
 */

public interface EmcOrderTaskService extends GenericService<EmcOrderTaskBean, Long>, GenericChoosableService<EmcOrderTaskBean, Long> {
    Long insert(RestJsonWrapperBean wrapper);

    Page<EmcOrderTaskBean> selectContainerInfoByOrderId(Long id, RestJsonWrapperBean wrapper);

    Page<EmcOrderTaskResultBean> selectOrderTaskResultByOrderTaskId(Long orderTaskId, RestJsonWrapperBean wrapper);

    Page<EmcOrderIndexBean> selectOrderIndexByIndexCategoryId(Long indexCategoryId, RestJsonWrapperBean wrapper);

    void insertByOrder(List<EmcOrderBean> orderList);

    void updateContainerInfo(RestJsonWrapperBean jsonWrapper);

    void updatePartake(RestJsonWrapperBean jsonWrapper);

    Page<EmcOrderTaskBean> selectRunnableList(RestJsonWrapperBean wrapper);

    Page<EmcOrderTaskBean> selectRunAnalysisOrderTaskList(RestJsonWrapperBean wrapper);

    Page<EmcOrderTaskBean> selectProjOrderCategoryPagination(RestJsonWrapperBean wrapper);

    void submitProjOrderCategory(RestJsonWrapperBean wrapper);

    Page<EmcOrderTaskBean> selectOrderTaskPagationByOrder(RestJsonWrapperBean wrapper);

    Page<EmcOrderTaskBean> selectOrderTaskList(RestJsonWrapperBean wrapper);

    void editResult(Long orderTaskId, RestJsonWrapperBean wrapper);

    void editMethod(Long orderTaskId, RestJsonWrapperBean wrapper);

    Long insertResultLog(Long id, RestJsonWrapperBean wrapper);

    Page<EmcResultLogBean> selectResultLogRawPaginationByOrderTaskId(Long id, RestJsonWrapperBean wrapper);

    Page<EmcOrderTaskLogBean> selectOrderTaskLogRawPaginationById(Long id, RestJsonWrapperBean wrapper);

    Page<EmcOrderTaskBean> selectAnalysisDepartmentStatistics(RestJsonWrapperBean wrapper);

    Page<EmcOrderTaskBean> selectPersonnelWorkloadStatistics(RestJsonWrapperBean wrapper);

    void setDataByOrderList(List<EmcOrderBean> orderList, List<EmcOrderTaskBean> allOldOrderTaskList, List<EmcOrderItemGroupBean> allNewOrderItemGroupList, String model);

    void setDataByOrderList(List<EmcOrderBean> orderList, List<EmcOrderTaskBean> allOldOrderTaskList, List<EmcOrderItemGroupBean> allNewOrderItemGroupList, String model, String status);

    Page<EmcOrderTaskResultBean> selectUpdateIndexOrderTaskResult(Long orderTaskId, RestJsonWrapperBean wrapper);

    Page<EmcOrderTaskResultBean> selectAnalysisProjectTaskResultValidation(RestJsonWrapperBean wrapper);

    void cleanResultIndex(RestJsonWrapperBean jsonWrapper);

    void updateResultIndex(RestJsonWrapperBean jsonWrapper);

    void updateCupNo(RestJsonWrapperBean wrapper);

    void updateEquiptAcquNo(RestJsonWrapperBean wrapper);

    void updateStockBatchNo(RestJsonWrapperBean wrapper);

	Page<EmcOrderTaskBean> selectOrderTaskByRunsList(RestJsonWrapperBean wrapper);

    Page<EmcOrderTaskBean> selectUniqueOrderTask(RestJsonWrapperBean wrapper);

    void editSubcontractor(RestJsonWrapperBean wrapper);

    void deleteSubcontractor(RestJsonWrapperBean wrapper);

    Page<EmcOrderTaskBean> selectSubpackageOrderTask(RestJsonWrapperBean wrapper);

    void syncOrderContainerInfo(RestJsonWrapperBean wrapper);

    void syncContainerInfo(RestJsonWrapperBean wrapper);

    void syncContainerInfo(List<EmcOrderTaskBean> orderTaskList);

    void updateSampleData(RestJsonWrapperBean wrapper);

    void syncFolderContainerInfo(RestJsonWrapperBean wrapper);

    Page<EmcOrderTaskBean> selectUnRunnableList(RestJsonWrapperBean wrapper);

    void updateAnaOrg(String ids, RestJsonWrapperBean wrapper);

    void updateAnaUser(String ids, RestJsonWrapperBean wrapper);

    Page<EmcOrderTaskBean> selectRunnableAnalAssignList(RestJsonWrapperBean wrapper);
}
