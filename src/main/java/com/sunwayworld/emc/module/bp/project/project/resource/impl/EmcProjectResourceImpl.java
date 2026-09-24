package com.sunwayworld.emc.module.bp.project.project.resource.impl;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u9879\u76ee")
@RestController
@GikamBean
public class EmcProjectResourceImpl
implements EmcProjectResource {
    @Autowired
    private EmcProjectService projectService;

    public EmcProjectService getService() {
        return this.projectService;
    }

    @Log(value="\u65b0\u589e\u9879\u76ee", type=LogType.INSERT)
    @RequestMapping(method={RequestMethod.POST})
    public Long insert(RestJsonWrapperBean wrapper) {
        return this.getService().insert(wrapper);
    }

    @Log(value="\u590d\u5236\u9879\u76ee", type=LogType.INSERT)
    @RequestMapping(value={"/action/copy"}, method={RequestMethod.POST})
    public void copy(RestJsonWrapperBean wrapper) {
        this.getService().copy(wrapper);
    }

    @Log(value="\u6839\u636e\u9879\u76eeID\u67e5\u8be2\u6837\u54c1", type=LogType.SELECT)
    @RequestMapping(value={"/{id}/orders/queries"}, method={RequestMethod.POST})
    public Page<EmcOrderBean> selectOrderPaginationByProjectId(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return this.getService().selectOrderPaginationByProjectId(id, wrapper);
    }

    @Log(value="\u65b0\u589e\u6837\u54c1", type=LogType.INSERT)
    @RequestMapping(value={"/{id}/orders"}, method={RequestMethod.POST})
    public Long insertOrder(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return this.getService().insertOrder(id, wrapper);
    }

    @Log(value="\u5220\u9664\u6837\u54c1", type=LogType.DELETE)
    @RequestMapping(value={"/{id}/orders"}, method={RequestMethod.DELETE})
    public void deleteOrder(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        this.getService().deleteOrder(id, wrapper);
    }

    @Log(value="\u590d\u5236\u4e3a\u4e00\u822c\u6837\u54c1-\u6837\u54c1\u767b\u8bb0", type=LogType.INSERT)
    @RequestMapping(value={"/orders/actions/copy"}, method={RequestMethod.POST})
    public void copeOrder(RestJsonWrapperBean jsonWrapper) {
        this.getService().copeOrder(jsonWrapper);
    }

    @Log(value="\u590d\u5236\u4e3aQC\u6837\u54c1-\u6837\u54c1\u767b\u8bb0", type=LogType.INSERT)
    @RequestMapping(value={"/orders/actions/copy-qc"}, method={RequestMethod.POST})
    public void copeQcOrder(RestJsonWrapperBean jsonWrapper) {
        this.getService().copeQcOrder(jsonWrapper);
    }

    @Log(value="\u9879\u76ee\u67e5\u8be2", type=LogType.SELECT)
    public Page<EmcProjectBean> selectSearchablePagination(RestJsonWrapperBean wrapper) {
        return this.getService().selectSearchablePagination(wrapper);
    }

    @Log(value="\u63d0\u4ea4\u5230\u62a5\u544a\u5f52\u6863", type=LogType.PROCESS)
    @RequestMapping(value={"/action/submit-to-report"}, method={RequestMethod.POST})
    public void submitToReport(RestJsonWrapperBean wrapper) {
        this.projectService.submitToReport(wrapper);
    }

    @Log(value="\u67e5\u8be2\u9879\u76ee\u6570\u91cf(\u6309\u4e1a\u52a1\u7c7b\u578b\u5206\u7ec4)", type=LogType.SELECT)
    @RequestMapping(value={"/project-statistics/queries"}, method={RequestMethod.POST})
    public Page<EmcProjectBean> selectProjectStatistics(RestJsonWrapperBean wrapper) {
        return this.getService().selectProjectStatistics(wrapper);
    }

    @Log(value="\u6839\u636e\u4e1a\u52a1\u7c7b\u578b\u67e5\u8be2\u672c\u5e74\u5ea6\u9879\u76ee\u6570\u91cf(\u6309\u6708\u4efd\u5206\u7ec4)", type=LogType.SELECT)
    @RequestMapping(value={"/annual-project-statistics/queries"}, method={RequestMethod.POST})
    public Page<EmcProjectBean> selectAnnualProjectStatistics(RestJsonWrapperBean wrapper) {
        return this.getService().selectAnnualProjectStatistics(wrapper);
    }

    @Log(value="\u6839\u636e\u4e1a\u52a1\u7c7b\u578b\u67e5\u8be2\u672c\u5e74\u5ea6\u9879\u76ee\u6570\u91cf(\u6309\u6708\u4efd\u5206\u7ec4)", type=LogType.SELECT)
    @RequestMapping(value={"/select-by-reportedit/queries"}, method={RequestMethod.POST})
    public Page<EmcProjectBean> selectByReportEdit(RestJsonWrapperBean wrapper) {
        return this.getService().selectByReportEdit(wrapper);
    }

    @Log(value="CRM\u5f00\u7968\u4efb\u52a1", type=LogType.SELECT)
    @RequestMapping(value={"/crm/invoice-proj/queries"}, method={RequestMethod.POST})
    public Page<EmcProjectBean> selectCrmInvoiceProjPagination(RestJsonWrapperBean wrapper) {
        return this.getService().selectCrmInvoiceProjPagination(wrapper);
    }

    @Log(value="CRM\u6536\u6b3e\u4efb\u52a1", type=LogType.SELECT)
    @RequestMapping(value={"/crm/payment-proj/queries"}, method={RequestMethod.POST})
    public Page<EmcProjectBean> selectCrmPaymentProjList(RestJsonWrapperBean wrapper) {
        return this.getService().selectCrmPaymentProjPagination(wrapper);
    }

    @Log(value="\u6240\u6709\u7cfb\u7edf\u4efb\u52a1", type=LogType.SELECT)
    @RequestMapping(value={"/all-proj/queries"}, method={RequestMethod.POST})
    public Page<EmcProjectBean> selectProjListPagination(RestJsonWrapperBean wrapper) {
        return this.getService().selectProjListPagination(wrapper);
    }

    @Log(value="\u67e5\u8be2\u6240\u6709\u9879\u76ee", type=LogType.SELECT)
    @RequestMapping(value={"/queries/all"}, method={RequestMethod.POST})
    public Page<EmcProjectBean> selectAllProjects(RestJsonWrapperBean wrapper) {
        return this.getService().selectAllProjects(wrapper);
    }

    @Log(value="\u6839\u636e\u9879\u76ee\u7f16\u53f7/\u540d\u79f0\u67e5\u8be2\u9879\u76ee", type=LogType.SELECT)
    @RequestMapping(value={"/{keyword}/queries"}, method={RequestMethod.GET})
    public Long selectIdByKeyword(@PathVariable String keyword, RestJsonWrapperBean wrapper) {
        return this.getService().selectIdByKeyword(keyword);
    }

    @Log(value="\u65b0\u589e\u4efb\u52a1\u8ba1\u5212", type=LogType.INSERT)
    @RequestMapping(value={"/plans"}, method={RequestMethod.POST})
    public Long insertPlan(RestJsonWrapperBean wrapper) {
        return this.getService().insertPlan(wrapper);
    }

    @Log(value="\u4fee\u6539\u5e76\u542f\u7528\u5b9a\u65f6\u5668", type=LogType.UPDATE)
    @RequestMapping(value={"/schedules/change"}, method={RequestMethod.PUT})
    public void changeSchedule(RestJsonWrapperBean wrapper) {
        this.getService().changeSchedule(wrapper);
    }

    @Log(value="\u590d\u5236\u4efb\u52a1\u8ba1\u5212", type=LogType.INSERT)
    @RequestMapping(value={"/plans/copy"}, method={RequestMethod.POST})
    public void copyPlan(RestJsonWrapperBean wrapper) {
        this.getService().copyPlan(wrapper);
    }

    @Log(value="\u63d0\u4ea4\u4efb\u52a1\u8ba1\u5212", type=LogType.UPDATE)
    @RequestMapping(value={"/plans/submit"}, method={RequestMethod.PUT})
    public void submitPlan(RestJsonWrapperBean wrapper) {
        this.getService().submitPlan(wrapper);
    }

    @Log(value="\u505c\u7528\u5b9a\u65f6\u5668", type=LogType.UPDATE)
    @RequestMapping(value={"/schedules/stop"}, method={RequestMethod.PUT})
    public void stopSchedule(RestJsonWrapperBean wrapper) {
        this.getService().stopSchedule(wrapper);
    }

    @Log(value="\u6839\u636e\u9879\u76ee\u7f16\u53f7\u67e5\u8be2\u9879\u76ee\u8d1f\u8d23\u4eba", type=LogType.SELECT)
    @RequestMapping(value={"/{id}/directors/queries"}, method={RequestMethod.POST})
    public Page<EmcProjectDirectorBean> selectDirectorPaginationByProjectId(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return this.getService().selectDirectorPaginationByProjectId(id, wrapper);
    }

    @Log(value="\u65b0\u589e\u9879\u76ee\u8d1f\u8d23\u4eba", type=LogType.INSERT)
    @RequestMapping(value={"/directors"}, method={RequestMethod.POST})
    public void insertDirector(RestJsonWrapperBean wrapper) {
        this.getService().insertDirector(wrapper);
    }

    @Log(value="\u5220\u9664\u9879\u76ee\u8d1f\u8d23\u4eba", type=LogType.DELETE)
    @RequestMapping(value={"/directors"}, method={RequestMethod.DELETE})
    public void deleteDirector(RestJsonWrapperBean wrapper) {
        this.getService().deleteDirector(wrapper);
    }

    @Log(value="\u5206\u6790\u5ba1\u6838\u3001\u5206\u6790\u9884\u5ba1\u6570\u636e\u6e90", type=LogType.SELECT)
    @RequestMapping(value={"/analysis-projects/queries"}, method={RequestMethod.POST})
    public Page<EmcProjectBean> selectAnalysisAuditProject(RestJsonWrapperBean wrapper) {
        return this.getService().selectAnalysisAuditProject(wrapper);
    }

    @Log(value="\u67e5\u8be2\u9879\u76ee\u8be6\u60c5", type=LogType.SELECT)
    @RequestMapping(value={"/{id}/detail"}, method={RequestMethod.POST})
    public EmcProjectBean selectProjectDetail(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return this.getService().selectProjectDetail(id, wrapper);
    }
}
