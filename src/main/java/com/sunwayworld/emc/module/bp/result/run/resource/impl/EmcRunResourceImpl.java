package com.sunwayworld.emc.module.bp.result.run.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.result.run.bean.EmcRunBean;
import com.sunwayworld.emc.module.bp.result.run.resource.EmcRunResource;
import com.sunwayworld.emc.module.bp.result.run.service.EmcRunService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcItemBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@LogModule(value = "样品测试批")
@RestController
@GikamBean
public class EmcRunResourceImpl implements EmcRunResource {

    @Autowired
    private EmcRunService runService;

    @Override
    public EmcRunService getService() {
        return runService;
    }

    @Log(value = "分析录入-添加样品测试", type = LogType.INSERT)
    @Override
    @RequestMapping(value = "/{id}/order-tasks", method = RequestMethod.POST)
    public Long addOrderTask(@PathVariable String id, RestJsonWrapperBean wrapper) {
        return runService.addOrderTask(id, wrapper);
    }

    @Log(value = "分析录入-删除样品测试", type = LogType.DELETE)
    @Override
    @RequestMapping(value = "/order-tasks", method = RequestMethod.DELETE)
    public Long deleteOrderTask(RestJsonWrapperBean wrapper) {
        return runService.deleteOrderTask(wrapper);
    }

    @Log(value = "分析录入-对批 （样品结果）进行初始化", type = LogType.UPDATE)
    @Override
    @RequestMapping(value = "/action/init", method = RequestMethod.PUT)
    public Long init(RestJsonWrapperBean wrapper) {
        return runService.init(wrapper);
    }

    @Log(value = "分析录入-对样品测试初始化", type = LogType.UPDATE)
    @Override
    @RequestMapping(value = "/order-task-results/action/init", method = RequestMethod.PUT)
    public Long sampleTestInitialize(RestJsonWrapperBean wrapper) {
        return runService.sampleTestInitialize(wrapper);
    }

    @Log(value = "分析录入-对批 （样品结果）进行默认结果", type = LogType.UPDATE)
    @Override
    @RequestMapping(value = "/action/updatedefaultresults", method = RequestMethod.PUT)
    public Long updatedefaultresults(RestJsonWrapperBean wrapper) {
        return runService.updatedefaultresults(wrapper);
    }

    @Log(value = "分析录入-添加QC", type = LogType.INSERT)
    @Override
    @RequestMapping(value = "/qc-order-tasks", method = RequestMethod.POST)
    public Long addQc(RestJsonWrapperBean wrapper) {
        return runService.addQc(wrapper);
    }

    @Log(value = "分析录入-删除QC", type = LogType.DELETE)
    @Override
    @RequestMapping(value = "/qc-order-tasks", method = RequestMethod.DELETE)
    public Long delQc(RestJsonWrapperBean wrapper) {
        return runService.delQc(wrapper);
    }

    @Override
    @Log(value = "分析录入-工作流启动", type = LogType.PROCESS)
    @RequestMapping(value = "/action/start-process", method = RequestMethod.PUT)
    public void startProcess(RestJsonWrapperBean wrapper) {
        getService().startProcess(wrapper);
    }

    @Log(value = "分析录入-添加批时选择仪器", type = LogType.SELECT)
    @Override
    @RequestMapping(value = "/equipt/{testMethodId}", method = RequestMethod.POST)
    public Page<EmcEquiptBean> selectEquiptPagination(@PathVariable String testMethodId) {
        return runService.selectEquiptPagination(testMethodId);
    }

    @Log(value = "分析录入-批录入", type = LogType.UPDATE)
    @Override
    @RequestMapping(value = "/action/run-input", method = RequestMethod.POST)
    public Long runInput(RestJsonWrapperBean wrapper) {
        return runService.runInput(wrapper);
    }

    @Log(value = "分析录入-选择分析项", type = LogType.SELECT)
    @Override
    @RequestMapping(value = "/select-item/{id}", method = RequestMethod.POST)
    public Page<EmcItemBean> selectEditItem(@PathVariable Long id,RestJsonWrapperBean wrapper) {
        return runService.selectEditItem(id,wrapper);
    }

    @Log(value = "分析录入-编辑分析项", type = LogType.UPDATE)
    @Override
    @RequestMapping(value = "/edit-item", method = RequestMethod.POST)
    public Long editItem(RestJsonWrapperBean wrapper) {
        return runService.editItem(wrapper);
    }

    @Override
    @Log(value = "分析录入-同步模板信息", type = LogType.UPDATE)
    @RequestMapping(value = "/action/sync-tmpl-infos", method = RequestMethod.POST)
    public void syncTmplInfo(RestJsonWrapperBean wrapper) {
        runService.syncTmplInfo(wrapper);
    }

    @Override
    @Log(value = "分包分析录入-同步测试模板信息", type = LogType.UPDATE)
    @RequestMapping(value = "/action/sync-sub-tmpl-infos", method = RequestMethod.POST)
    public void syncSubTmplInfo(RestJsonWrapperBean wrapper) {
        runService.syncSubTmplInfo(wrapper);
    }

    @Override
    @Log(value = "分析录入-去除公式", type = LogType.UPDATE)
    @RequestMapping(value = "/action/remove-fomulas", method = RequestMethod.POST)
    public void removeFomula(RestJsonWrapperBean wrapper) {
        runService.removeFomula(wrapper);
    }

    @Override
    @Log(value = "分析录入-修改单位", type = LogType.UPDATE)
    @RequestMapping(value = "/action/modify-unit", method = RequestMethod.POST)
    public void modifyUnit(RestJsonWrapperBean wrapper) {
        runService.modifyUnit(wrapper);
    }

    @Override
    @Log(value = "分析录入-批录入检出限", type = LogType.UPDATE)
    @RequestMapping(value = "/action/modify-limit", method = RequestMethod.POST)
    public void modifyLimit(RestJsonWrapperBean wrapper) {
        runService.modifyLimit(wrapper);
    }

    @Override
    @Log(value = "分析录入-批录修约规则", type = LogType.UPDATE)
    @RequestMapping(value = "/action/modify-revision", method = RequestMethod.POST)
    public void modifyRevision(RestJsonWrapperBean wrapper) {
        runService.modifyRevision(wrapper);
    }

    @Override
    @Log(value = "查询所有状态的批数据", type = LogType.UPDATE)
    @RequestMapping(value = "/queries/all", method = RequestMethod.POST)
    public Page<EmcRunBean> selectAllPagination(RestJsonWrapperBean wrapper) {
        return runService.selectAllPagination(wrapper);
    }
    
    @Override
    @Log(value = "查询批结果数据", type = LogType.UPDATE)
    @RequestMapping(value = "/queries/run-result", method = RequestMethod.POST)
    public Page<EmcRunBean> selectRunResult(RestJsonWrapperBean wrapper) {
        return runService.selectRunResult(wrapper);
    }

    @Override
    @Log(value = "修改测试方法", type = LogType.UPDATE)
    @RequestMapping(value = "/action/method-edit", method = RequestMethod.POST)
    public void methodEdit(RestJsonWrapperBean wrapper) {
        runService.methodEdit(wrapper);
    }

    @Override
    @Log(value = "触发公式", type = LogType.UPDATE)
    @RequestMapping(value = "/action/trigger-formlua", method = RequestMethod.POST)
    public void triggerFormlua(RestJsonWrapperBean wrapper) {
        runService.triggerFormlua(wrapper);
    }

    @Override
    @Log(value = "仪器采集", type = LogType.UPDATE)
    @RequestMapping(value = "/{id}/equipt-collects", method = RequestMethod.PUT)
    public void equiptCollect(@PathVariable String id, RestJsonWrapperBean wrapper) {
        runService.equiptCollect(id, wrapper);
    }

    @Log(value = "分析录入-修改仪器时选择仪器", type = LogType.SELECT)
    @Override
    @RequestMapping(value = "/equipt/{testMethodId}/{equiptId}", method = RequestMethod.POST)
    public Page<EmcEquiptBean> selectEditEquiptPagination(@PathVariable String testMethodId, @PathVariable String equiptId) {
        return runService.selectEditEquiptPagination(testMethodId, equiptId);
    }

    @Log(value = "分析录入-修改仪器", type = LogType.UPDATE)
    @Override
    @RequestMapping(value = "/action/edit-equipt", method = RequestMethod.POST)
    public void editEquipt(RestJsonWrapperBean wrapper) {
        runService.editEquipt(wrapper);
    }

    @Override
    @Log(value = "重新合成原始记录PDF", type = LogType.INSERT)
    @RequestMapping(value = "/action/recompose-pdf", method = RequestMethod.POST)
    public void recomposeRunPdf(RestJsonWrapperBean wrapper) {
        getService().recomposeRunPdf(wrapper);
    }

    @Override
    @Log(value = "数据相关性校验", type = LogType.UPDATE)
    @RequestMapping(value = "/correlation-validations", method = RequestMethod.PUT)
    public String correlationValidate(RestJsonWrapperBean wrapper) {
        return getService().validateCorrelation(wrapper);
    }

    @Override
    @Log(value = "分析审核提交", type = LogType.SELECT)
    @RequestMapping(value = "/action/submit", method = RequestMethod.PUT)
    public void submitAnalysisProject(RestJsonWrapperBean wrapper) {
        getService().submitAnalysisProject(wrapper);
    }

    @Override
    @Log(value = "查询其他批次实验室空白样品测试", type = LogType.SELECT)
    @RequestMapping(value = "/blank-order-tasks/queries", method = RequestMethod.POST)
    public Page<EmcOrderTaskBean> selectOrderTaskPagination(RestJsonWrapperBean wrapper) {
        return getService().selectOrderTaskPagination(wrapper);
    }

    @Override
    @Log(value = "复制实验室空白到其他批次", type = LogType.INSERT)
    @RequestMapping(value = "/action/copy-blank-to", method = RequestMethod.POST)
    public void copyBlankTo(RestJsonWrapperBean wrapper) {
        getService().copyBlankTo(wrapper);
    }

    @Override
    @Log(value = "复制空白加标到其他批次", type = LogType.INSERT)
    @RequestMapping(value = "/action/copy-blank-spkc-to", method = RequestMethod.POST)
    public void copyBlankSpkcTo(RestJsonWrapperBean wrapper) {
        getService().copyBlankSpkcTo(wrapper);
    }

    @Override
    @Log(value = "批录入小于检出限结果规则", type = LogType.UPDATE)
    @RequestMapping(value = "/action/batch-input-result-rule", method = RequestMethod.POST)
    public void batchInputResultRule(RestJsonWrapperBean wrapper) {
        getService().batchInputResultRule(wrapper);
    }

    @Override
    @Log(value = "重新合成项目原始记录PDF", type = LogType.UPDATE)
    @RequestMapping(value = "/action/recompose-project-run-pdf", method = RequestMethod.POST)
    public void recomposeProjectRunPdf(RestJsonWrapperBean wrapper) {
        getService().recomposeProjectRunPdf(wrapper);
    }
    
    @Override
    @Log(value = "分析预审数据", type = LogType.SELECT)
    @RequestMapping(value = "/analysis-pretrial/queries", method = RequestMethod.POST)
    public Page<EmcRunBean> analysisPretrial(RestJsonWrapperBean wrapper) {
        return getService().analysisPretrial(wrapper);
    }
    
    @Override
    @Log(value = "预审核通过", type = LogType.UPDATE)
    @RequestMapping(value = "/pre-approval", method = RequestMethod.PUT)
    public void passPreApproval(RestJsonWrapperBean wrapper) {
        getService().passPreApproval(wrapper);
    }
}
