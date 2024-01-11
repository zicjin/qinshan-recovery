package com.sunwayworld.emc.module.bp.project.project.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.bp.project.project.bean.PfProjectTaskProcessDTO;
import com.sunwayworld.emc.module.bp.project.project.resource.PfProjectResource;
import com.sunwayworld.emc.module.bp.project.project.service.PfOrderItemGroupService;
import com.sunwayworld.emc.module.bp.project.project.service.PfProjectService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.item.file.bean.CoreFileBean;

/**
 * @author luoc@sunwayworld.com
 * @date 2021-05-07
 */

@LogModule("项目")
@RestController
@GikamBean
public class PfProjectResourceImpl extends EmcProjectResourceImpl implements PfProjectResource {

    @Autowired
    private PfProjectService projectService;

    @Autowired
    private PfOrderItemGroupService orderItemGroupService;

    @Override
    @Log(value = "选择材料批次号", type = LogType.UPDATE)
    @RequestMapping(value = "/qcMtlSelect", method = RequestMethod.PUT)
    public void qcMtlSelect(RestJsonWrapperBean wrapper) {
        orderItemGroupService.qcMtlSelect(wrapper);
    }

    @Override
    @Log(value = "送样管理-退回项目", type = LogType.UPDATE)
    @RequestMapping(value = "/action/undo-delivery", method = RequestMethod.POST)
    public void undoDelivery(RestJsonWrapperBean wrapper) {
        projectService.undoDelivery(wrapper);
    }

    @Override
    @Log(value = "项目方案修改-提交", type = LogType.UPDATE)
    @RequestMapping(value = "/action/scheme-edit-submit", method = RequestMethod.POST)
    public void schemeEditSubmit(RestJsonWrapperBean wrapper) {
        projectService.schemeEditSubmit(wrapper);
    }

    @Override
    @Log(value = "项目方案修改-校验金额", type = LogType.UPDATE)
    @RequestMapping(value = "/action/check-money", method = RequestMethod.POST)
    public String checkMoney(RestJsonWrapperBean wrapper) {
        return projectService.checkMoney(wrapper);
    }

    @Override
    @Log(value = "项目方案作废-校验", type = LogType.UPDATE)
    @RequestMapping(value = "/action/cancel-check", method = RequestMethod.POST)
    public String cancelCheck(RestJsonWrapperBean wrapper) {
        return projectService.cancelCheck(wrapper);
    }

    @Override
    @Log(value = "项目方案提交-校验", type = LogType.UPDATE)
    @RequestMapping(value = "/action/submit-before-check", method = RequestMethod.POST)
    public String submitBeforeCheck(RestJsonWrapperBean wrapper) {
        return projectService.submitBeforeCheck(wrapper);
    }

    @Override
    @Log(value = "项目查询-发送报告", type = LogType.UPDATE)
    @RequestMapping(value = "/action/send-report", method = RequestMethod.POST)
    public void sendReport(RestJsonWrapperBean wrapper) {
        projectService.sendReport(wrapper);
    }

    @Override
    @Log(value = "查询项目归档报告", type = LogType.SELECT)
    @RequestMapping(value = "/reports/queries", method = RequestMethod.POST)
    public Page<CoreFileBean> queryReportFile(RestJsonWrapperBean wrapper) {
        return projectService.queryReportFile(wrapper);
    }

    @Log(value = "查询任务进度表", type = LogType.SELECT)
    @RequestMapping(value = "/action/task-process", method = RequestMethod.POST)
    public Page<PfProjectTaskProcessDTO> selectProjectTaskProcess(RestJsonWrapperBean wrapper) {
        return projectService.selectProjectTaskProcess(wrapper);
    }

    @Log(value = "任务指派任务进度表", type = LogType.SELECT)
    @RequestMapping(value = "/action/task-assign-process/{analysisOrgid}", method = RequestMethod.POST)
    public Page<PfProjectTaskProcessDTO> selectAssignProjectTaskProcess( @PathVariable String analysisOrgid, RestJsonWrapperBean wrapper) {
        return projectService.selectAssignProjectTaskProcess(analysisOrgid,wrapper);
    }

    @Override
    @Log(value = "导出现场联系单文件", type = LogType.INSERT)
    @RequestMapping(value = "/action/sample-contract-file", method = RequestMethod.POST)
    public String sampleContractFile(RestJsonWrapperBean wrapper) {
    	return projectService.sampleContractFile(wrapper);
    }

    @Override
    @Log(value = "已完成项目修改金额后项目信息到CRM", type = LogType.UPDATE)
    @RequestMapping(value = "/action/sync-projinfo-after-edit", method = RequestMethod.POST)
    public void syncProjInfoAfterEdit(RestJsonWrapperBean wrapper) {
         projectService.syncProjInfoAfterEdit(wrapper);
    }

    @Override
    @Log(value = "查询项目是否有核销数据", type = LogType.INSERT)
    @RequestMapping(value = "/action/have-write-off", method = RequestMethod.POST)
    public String haveWriteOff(RestJsonWrapperBean wrapper) {
        return projectService.haveWriteOff(wrapper);
    }

    @Override
    @Log(value = "修复项目异常数据", type = LogType.INSERT)
    @RequestMapping(value = "/action/repair", method = RequestMethod.POST)
    public void repairProjectData(RestJsonWrapperBean wrapper) {
        projectService.repairProjectData(wrapper);
    }

    @Override
    @Log(value = "样品上下移动", type = LogType.UPDATE)
    @RequestMapping(value = "/move-order", method = RequestMethod.PUT)
    public void moveOrder(RestJsonWrapperBean wrapper) {
        projectService.moveOrder(wrapper);
    }

    @Override
    @Log(value = "样品分析项组上下移动", type = LogType.UPDATE)
    @RequestMapping(value = "/move-order-item-group", method = RequestMethod.PUT)
    public void moveOrderItemGroup(RestJsonWrapperBean wrapper) {
        projectService.moveOrderItemGroup(wrapper);
    }
}
