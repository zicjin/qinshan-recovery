package com.sunwayworld.emc.module.bp.project.project.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderBean;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcProjectBean;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcProjectDirectorBean;
import com.sunwayworld.emc.module.bp.project.project.resource.EmcProjectResource;
import com.sunwayworld.emc.module.bp.project.project.service.EmcProjectService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author magh@sunwayworld.com
 * @date 2019-09-27
 */

@LogModule("项目")
@RestController
@GikamBean
public class EmcProjectResourceImpl implements EmcProjectResource {
    @Autowired
    private EmcProjectService projectService;

    @Override
    public EmcProjectService getService() {
        return projectService;
    }

    @Log(value = "新增项目", type = LogType.INSERT)
    @Override
    @RequestMapping(method = RequestMethod.POST)
    public Long insert(RestJsonWrapperBean wrapper) {
        return getService().insert(wrapper);
    }

    @Override
    @Log(value = "复制项目", type = LogType.INSERT)
    @RequestMapping(value = "/action/copy", method = RequestMethod.POST)
    public void copy(RestJsonWrapperBean wrapper) {
        getService().copy(wrapper);
    }

    @Override
    @Log(value = "根据项目ID查询样品", type = LogType.SELECT)
    @RequestMapping(value = "/{id}/orders/queries", method = RequestMethod.POST)
    public Page<EmcOrderBean> selectOrderPaginationByProjectId(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return getService().selectOrderPaginationByProjectId(id, wrapper);
    }

    @Override
    @Log(value = "新增样品", type = LogType.INSERT)
    @RequestMapping(value = "/{id}/orders", method = RequestMethod.POST)
    public Long insertOrder(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return getService().insertOrder(id, wrapper);
    }

    @Override
    @Log(value = "删除样品", type = LogType.DELETE)
    @RequestMapping(value = "/{id}/orders", method = RequestMethod.DELETE)
    public void deleteOrder(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        getService().deleteOrder(id, wrapper);
    }

    @Override
    @Log(value = "复制为一般样品-样品登记", type = LogType.INSERT)
    @RequestMapping(value = "/orders/actions/copy", method = RequestMethod.POST)
    public void copeOrder(RestJsonWrapperBean jsonWrapper) {
        getService().copeOrder(jsonWrapper);
    }

    @Override
    @Log(value = "复制为QC样品-样品登记", type = LogType.INSERT)
    @RequestMapping(value = "/orders/actions/copy-qc", method = RequestMethod.POST)
    public void copeQcOrder(RestJsonWrapperBean jsonWrapper) {
        getService().copeQcOrder(jsonWrapper);
    }

    @Override
    @Log(value = "项目查询", type = LogType.SELECT)
    public Page<EmcProjectBean> selectSearchablePagination(RestJsonWrapperBean wrapper) {
        return getService().selectSearchablePagination(wrapper);
    }

    @Override
    @Log(value = "提交到报告", type = LogType.PROCESS)
    @RequestMapping(value = "/action/submit-to-report", method = RequestMethod.POST)
    public void submitToReport(RestJsonWrapperBean wrapper) {
        projectService.submitToReport(wrapper);
    }

    @Override
    @Log(value = "查询项目数量(按业务类型分组)", type = LogType.SELECT)
    @RequestMapping(value = "/project-statistics/queries", method = RequestMethod.POST)
    public Page<EmcProjectBean> selectProjectStatistics(RestJsonWrapperBean wrapper) {
        return getService().selectProjectStatistics(wrapper);
    }

    @Override
    @Log(value = "根据业务类型查询本年度项目数量(按月份分组)", type = LogType.SELECT)
    @RequestMapping(value = "/annual-project-statistics/queries", method = RequestMethod.POST)
    public Page<EmcProjectBean> selectAnnualProjectStatistics(RestJsonWrapperBean wrapper) {
        return getService().selectAnnualProjectStatistics(wrapper);
    }

    @Override
    @Log(value = "查询所有项目", type = LogType.SELECT)
    @RequestMapping(value = "/queries/all", method = RequestMethod.POST)
    public Page<EmcProjectBean> selectAllProjects(RestJsonWrapperBean wrapper) {
        return getService().selectAllProjects(wrapper);
    }

    @Override
    @Log(value = "根据项目编号/名称查询项目", type = LogType.SELECT)
    @RequestMapping(value = "/{keyword}/queries", method = RequestMethod.GET)
    public Long selectIdByKeyword(@PathVariable String keyword, RestJsonWrapperBean wrapper) {
        return getService().selectIdByKeyword(keyword);
    }

    @Log(value = "新增任务计划", type = LogType.INSERT)
    @Override
    @RequestMapping(value = "/plans", method = RequestMethod.POST)
    public Long insertPlan(RestJsonWrapperBean wrapper) {
        return getService().insertPlan(wrapper);
    }

    @Override
    @Log(value = "修改并启用定时器", type = LogType.UPDATE)
    @RequestMapping(value = "/schedules/change", method = RequestMethod.PUT)
    public void changeSchedule(RestJsonWrapperBean wrapper) {
        this.getService().changeSchedule(wrapper);
    }

    @Override
    @Log(value = "复制任务计划", type = LogType.INSERT)
    @RequestMapping(value = "/plans/copy", method = RequestMethod.POST)
    public void copyPlan(RestJsonWrapperBean wrapper) {
        getService().copyPlan(wrapper);
    }

    @Override
    @Log(value = "提交任务计划", type = LogType.UPDATE)
    @RequestMapping(value = "/plans/submit", method = RequestMethod.PUT)
    public void submitPlan(RestJsonWrapperBean wrapper) {
        getService().submitPlan(wrapper);
    }

    @Override
    @Log(value = "停用定时器", type = LogType.UPDATE)
    @RequestMapping(value = "/schedules/stop", method = RequestMethod.PUT)
    public void stopSchedule(RestJsonWrapperBean wrapper) {
        this.getService().stopSchedule(wrapper);
    }

    @Override
    @Log(value = "根据项目编号查询项目负责人", type = LogType.SELECT)
    @RequestMapping(value = "/{id}/directors/queries", method = RequestMethod.POST)
    public Page<EmcProjectDirectorBean> selectDirectorPaginationByProjectId(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return getService().selectDirectorPaginationByProjectId(id, wrapper);
    }

    @Override
    @Log(value = "新增项目负责人", type = LogType.INSERT)
    @RequestMapping(value = "/directors", method = RequestMethod.POST)
    public void insertDirector(RestJsonWrapperBean wrapper) {
        getService().insertDirector(wrapper);
    }

    @Override
    @Log(value = "删除项目负责人", type = LogType.DELETE)
    @RequestMapping(value = "/directors", method = RequestMethod.DELETE)
    public void deleteDirector(RestJsonWrapperBean wrapper) {
        getService().deleteDirector(wrapper);
    }

    @Override
    @Log(value = "分析审核、分析预审数据源", type = LogType.SELECT)
    @RequestMapping(value = "/analysis-projects/queries", method = RequestMethod.POST)
    public Page<EmcProjectBean> selectAnalysisAuditProject(RestJsonWrapperBean wrapper) {
        return getService().selectAnalysisAuditProject(wrapper);
    }

    @Override
    @Log(value = "查询项目详情", type = LogType.SELECT)
    @RequestMapping(value = "/{id}/detail", method = RequestMethod.POST)
    public EmcProjectBean selectProjectDetail(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return getService().selectProjectDetail(id, wrapper);
    }
}
