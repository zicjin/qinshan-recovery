package com.sunwayworld.emc.module.bp.bp.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskLogBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskResultBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcResultLogBean;
import com.sunwayworld.emc.module.bp.bp.resource.EmcOrderTaskResource;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskService;
import com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.bean.EmcOrderIndexBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@LogModule("样品测试")
@RestController
@GikamBean
public class EmcOrderTaskResourceImpl implements EmcOrderTaskResource {

    @Autowired
    private EmcOrderTaskService orderTaskService;

    @Override
    public EmcOrderTaskService getService() {
        return orderTaskService;
    }

    @Log(value = "项目样品类型查询", type = LogType.SELECT)
    @RequestMapping(value = "/proj-order-categories/queries", method = RequestMethod.POST)
    public Page<EmcOrderTaskBean> selectProjOrderCategoryPagination(RestJsonWrapperBean wrapper) {
        return getService().selectProjOrderCategoryPagination(wrapper);
    }

    @Log(value = "项目样品类型提交", type = LogType.SELECT)
    @RequestMapping(value = "/proj-order-categories/action/submit", method = RequestMethod.PUT)
    public void submitProjOrderCategory(RestJsonWrapperBean wrapper) {
        getService().submitProjOrderCategory(wrapper);
    }

    @Override
    @Log(value = "查询待处理样品测试", type = LogType.SELECT)
    @RequestMapping(value = "/queries/runnable", method = RequestMethod.POST)
    public Page<EmcOrderTaskBean> selectRunnableList(RestJsonWrapperBean wrapper) {
        return getService().selectRunnableList(wrapper);
    }

    @Log(value = "查询待处理样品测试", type = LogType.SELECT)
    @RequestMapping(value = "/queries/un-runnable", method = RequestMethod.POST)
    public Page<EmcOrderTaskBean> selectUnRunnableList(RestJsonWrapperBean wrapper) {
        return getService().selectUnRunnableList(wrapper);
    }

    @Log(value = "分析指派修改分析科室", type = LogType.SELECT)
    @RequestMapping(value = {"/{ids}/order-task-assign/edit-anaorg"}, method = {RequestMethod.POST})
    public void updateAnaOrg(@PathVariable String ids, RestJsonWrapperBean wrapper) {
        getService().updateAnaOrg(ids, wrapper);
    }

    @Log(value = "分析指派修改录入人", type = LogType.SELECT)
    @RequestMapping(value = {"/{ids}/order-task-assign/edit-anauser"}, method = {RequestMethod.POST})
    public void updateAnaUser(@PathVariable String ids,RestJsonWrapperBean wrapper) {
        getService().updateAnaUser(ids, wrapper);
    }

    @Override
    @Log(value = "查询首页分析录入数据", type = LogType.SELECT)
    @RequestMapping(value = "/queries/analysis", method = RequestMethod.POST)
    public Page<EmcOrderTaskBean> selectRunAnalysisOrderTaskList(RestJsonWrapperBean wrapper) {
        return getService().selectRunAnalysisOrderTaskList(wrapper);
    }

    @Override
    @Log(value = "根据条件查询样品", type = LogType.SELECT)
    @RequestMapping(value = "/queries/order-group", method = RequestMethod.POST)
    public Page<EmcOrderTaskBean> selectOrderTaskList(RestJsonWrapperBean wrapper) {
        return getService().selectOrderTaskList(wrapper);
    }

    @Override
    @Log(value = "切换批根据条件查询样品测试", type = LogType.SELECT)
    @RequestMapping(value = "/queries/order-group-runs", method = RequestMethod.POST)
    public Page<EmcOrderTaskBean> selectOrderTaskByRunsList(RestJsonWrapperBean wrapper) {
        return getService().selectOrderTaskByRunsList(wrapper);
    }

    @Override
    @Log(value = "根据样品查询样品测试", type = LogType.SELECT)
    @RequestMapping(value = "/unique-tasks/queries", method = RequestMethod.POST)
    public Page<EmcOrderTaskBean> selectUniqueOrderTask(RestJsonWrapperBean wrapper) {
        return getService().selectUniqueOrderTask(wrapper);
    }

    @Override
    @Log(value = "设置分包商", type = LogType.UPDATE)
    @RequestMapping(value = "/action/edit-subcontractor", method = RequestMethod.POST)
    public void editSubcontractor(RestJsonWrapperBean wrapper) {
        getService().editSubcontractor(wrapper);
    }

    @Override
    @Log(value = "删除分包商", type = LogType.UPDATE)
    @RequestMapping(value = "/action/delete-subcontractor", method = RequestMethod.POST)
    public void deleteSubcontractor(RestJsonWrapperBean wrapper) {
        getService().deleteSubcontractor(wrapper);
    }

    @Override
    @Log(value = "根据样品查询可现场分包的样品测试", type = LogType.SELECT)
    @RequestMapping(value = "/subpackages/queries", method = RequestMethod.POST)
    public Page<EmcOrderTaskBean> selectSubpackageOrderTask(RestJsonWrapperBean wrapper) {
        return getService().selectSubpackageOrderTask(wrapper);
    }

    @Override
    @Log(value = "按点位同步容器信息", type = LogType.UPDATE)
    @RequestMapping(value = "/action/sync-folder-container-info", method = RequestMethod.POST)
    public void syncFolderContainerInfo(RestJsonWrapperBean wrapper) {
        getService().syncFolderContainerInfo(wrapper);
    }
    
    @Override
    @Log(value = "按样品同步容器信息", type = LogType.UPDATE)
    @RequestMapping(value = "/action/sync-order-container-info", method = RequestMethod.POST)
    public void syncOrderContainerInfo(RestJsonWrapperBean wrapper) {
        getService().syncOrderContainerInfo(wrapper);
    }

    @Override
    @Log(value = "同步容器信息", type = LogType.UPDATE)
    @RequestMapping(value = "/action/sync-container-info", method = RequestMethod.POST)
    public void syncContainerInfo(RestJsonWrapperBean wrapper) {
        getService().syncContainerInfo(wrapper);
    }

    @Override
    @Log(value = "查询样品结果", type = LogType.SELECT)
    @RequestMapping(value = "/{id}/results/queries", method = RequestMethod.POST)
    public Page<EmcOrderTaskResultBean> selectOrderTaskResultByOrderTaskId(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return getService().selectOrderTaskResultByOrderTaskId(id, wrapper);
    }

    @Override
    @Log(value = "查询样品结果数据相关性校验数据", type = LogType.SELECT)
    @RequestMapping(value = "/results/validations", method = RequestMethod.POST)
    public Page<EmcOrderTaskResultBean> selectAnalysisProjectTaskResultValidation(RestJsonWrapperBean wrapper) {
        return getService().selectAnalysisProjectTaskResultValidation(wrapper);
    }

    @Override
    @Log(value = "插入样品结果记录", type = LogType.INSERT)
    @RequestMapping(value = "/{id}/result-logs", method = RequestMethod.POST)
    public Long insertResultLog(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return getService().insertResultLog(id, wrapper);

    }

    @Override
    @Log(value = "查询样品结果记录", type = LogType.SELECT)
    @RequestMapping(value = "/{id}/result-logs/queries", method = RequestMethod.POST)
    public Page<EmcResultLogBean> selectResultLogRawPaginationByOrderTaskId(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return getService().selectResultLogRawPaginationByOrderTaskId(id, wrapper);
    }

    @Override
    @Log(value = "查询样品操作记录", type = LogType.SELECT)
    @RequestMapping(value = "/{id}/logs/queries", method = RequestMethod.POST)
    public Page<EmcOrderTaskLogBean> selectOrderTaskLogRawPaginationById(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return getService().selectOrderTaskLogRawPaginationById(id, wrapper);
    }

    @Override
    @Log(value = "查询分析科室下样品测试数(按分析时间分组)", type = LogType.SELECT)
    @RequestMapping(value = "/analysis-department-statistics/queries", method = RequestMethod.POST)
    public Page<EmcOrderTaskBean> selectAnalysisDepartmentStatistics(RestJsonWrapperBean wrapper) {
        return getService().selectAnalysisDepartmentStatistics(wrapper);
    }

    @Override
    @Log(value = "查询分析科室下样品测试数(按分析人员分组)", type = LogType.SELECT)
    @RequestMapping(value = "/personnel-workload-statistics/queries", method = RequestMethod.POST)
    public Page<EmcOrderTaskBean> selectPersonnelWorkloadStatistics(RestJsonWrapperBean wrapper) {
        return getService().selectPersonnelWorkloadStatistics(wrapper);
    }

    @Override
    @Log(value = "查询样品指标", type = LogType.SELECT)
    @RequestMapping(value = "/{id}/indexs/queries", method = RequestMethod.POST)
    public Page<EmcOrderIndexBean> selectOrderIndexByIndexCategoryId(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return getService().selectOrderIndexByIndexCategoryId(id, wrapper);
    }

    @Override
    @Log(value = "去重查询检测分析项数据", type = LogType.SELECT)
    @RequestMapping(value = "/{id}/results/distinct/queries", method = RequestMethod.POST)
    public Page<EmcOrderTaskResultBean> selectUpdateIndexOrderTaskResult(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return getService().selectUpdateIndexOrderTaskResult(id, wrapper);
    }

    @Override
    @Log(value = "清空样品测试结果指标数据", type = LogType.UPDATE)
    @RequestMapping(value = "/results/action/clean-index", method = RequestMethod.POST)
    public void cleanResultIndex(RestJsonWrapperBean jsonWrapper) {
        getService().cleanResultIndex(jsonWrapper);
    }

    @Override
    @Log(value = "更新样品测试结果指标数据", type = LogType.UPDATE)
    @RequestMapping(value = "/results/action/update-index", method = RequestMethod.POST)
    public void updateResultIndex(RestJsonWrapperBean jsonWrapper) {
        getService().updateResultIndex(jsonWrapper);
    }

    @Override
    @Log(value = "更新杯号", type = LogType.UPDATE)
    @RequestMapping(value = "/action/update-cupno", method = RequestMethod.PUT)
    public void updateCupNo(RestJsonWrapperBean wrapper) {
        getService().updateCupNo(wrapper);
    }

    @Override
    @Log(value = "更新仪器采集编号", type = LogType.UPDATE)
    @RequestMapping(value = "/action/update-equiptacquno", method = RequestMethod.PUT)
    public void updateEquiptAcquNo(RestJsonWrapperBean wrapper) {
        getService().updateEquiptAcquNo(wrapper);
    }

    @Override
    @Log(value = "更新库存批号", type = LogType.UPDATE)
    @RequestMapping(value = "/action/update-stockbatchno", method = RequestMethod.PUT)
    public void updateStockBatchNo(RestJsonWrapperBean wrapper) {
        getService().updateStockBatchNo(wrapper);
    }

    @Override
    @Log(value = "更新容器相关数据", type = LogType.UPDATE)
    @RequestMapping(value = "/action/update-sampledata", method = RequestMethod.PUT)
    public void updateSampleData(RestJsonWrapperBean wrapper) {
        getService().updateSampleData(wrapper);
    }

    @Log(value = "查询待任务指派样品测试", type = LogType.SELECT)
    @RequestMapping(value = "/queries/runnableanalassign", method = RequestMethod.POST)
    public Page<EmcOrderTaskBean> selectRunnableAnalAssignList(RestJsonWrapperBean wrapper) {
        return getService().selectRunnableAnalAssignList(wrapper);
    }
}
