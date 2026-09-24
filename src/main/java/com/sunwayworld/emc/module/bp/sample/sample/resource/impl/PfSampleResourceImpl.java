package com.sunwayworld.emc.module.bp.sample.sample.resource.impl;

import com.sunwayworld.emc.module.bp.sample.sample.bean.EmcSampleBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.bp.sample.sample.resource.PfSampleResource;
import com.sunwayworld.emc.module.bp.sample.sample.service.PfSampleService;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author luoc@sunwayworld.com 2021/3/26
 */
@LogModule("采样单")
@RestController
@GikamBean
public class PfSampleResourceImpl extends EmcSampleResourceImpl implements PfSampleResource {

    @Autowired
    private PfSampleService pfSampleService;

    @Override
    public PfSampleService getService() {
        return pfSampleService;
    }

    @Override
    public void completeTask(RestJsonWrapperBean wrapper) {
        this.getService().completeTask(wrapper);
    }

    @Override
    @Log(value = "样品接收-回退", type = LogType.PROCESS)
    @RequestMapping(value = "/action/undo", method = RequestMethod.PUT)
    public void undo(RestJsonWrapperBean wrapper) {
        getService().bizUndo(wrapper);
    }

    @Override
    @Log(value = "查询模板类型ID", type = LogType.SELECT)
    @RequestMapping(value = "/action/select-category-code", method = RequestMethod.POST)
    public String selectCategoryCode(RestJsonWrapperBean wrapper) {
    	return getService().selectCategoryCode(wrapper);
    }

    @Override
    @Log(value = "同步分析项", type = LogType.SELECT)
    @RequestMapping(value = "/action/sync-test-item", method = RequestMethod.POST)
    public void syncTestItem(RestJsonWrapperBean wrapper) {
        getService().syncTestItem(wrapper);
    }

    @Override
    @Log(value = "提示信息", type = LogType.SELECT)
    @RequestMapping(value = "/action/submit-reminds", method = RequestMethod.POST)
    public String submitReminds(RestJsonWrapperBean wrapper) {
    	return getService().submitReminds(wrapper);
    }

    @Override
    @Log(value = "提交送样单", type = LogType.UPDATE)
    @RequestMapping(value = "/action/submit-delivery", method = RequestMethod.POST)
    public void submitDelivery(RestJsonWrapperBean wrapper) {
        getService().submitDelivery(wrapper);
    }

    @Override
    @Log(value = "样品称重无效结果", type = LogType.UPDATE)
    @RequestMapping(value = "/action/invalid-weight-result", method = RequestMethod.POST)
    public void invalidWeightResult(RestJsonWrapperBean wrapper) {
        getService().invalidWeightResult(wrapper);
    }

    @Override
    @Log(value = "退回至样品接收", type = LogType.UPDATE)
    @RequestMapping(value = "/action/undo-accept", method = RequestMethod.POST)
    public void undoAccept(RestJsonWrapperBean wrapper) {
        getService().undoAccept(wrapper);
    }

    @Override
    @Log(value = "同步指派日期到测试", type = LogType.UPDATE)
    @RequestMapping(value = "/action/sync-assign-date", method = RequestMethod.POST)
    public void syncAssignDate(RestJsonWrapperBean wrapper) {
        getService().syncAssignDate(wrapper);
    }

    @Override
    @Log(value = "加测确定", type = LogType.UPDATE)
    @RequestMapping(value = "/action/confirm-add-test", method = RequestMethod.POST)
    public void confirmAddTest(RestJsonWrapperBean wrapper) {
        getService().confirmAddTest(wrapper);
    }

    @Override
    @Log(value = "采样审核-提示信息", type = LogType.SELECT)
    @RequestMapping(value = "/action/pass-reminds", method = RequestMethod.POST)
    public String passReminds(RestJsonWrapperBean wrapper) {
        return getService().passReminds(wrapper);
    }

    @Log(value = "现场采样提交到样品接收", type = LogType.INSERT)
    @Override
    @RequestMapping(value = "/action/handsample", method = RequestMethod.POST)
    public void handSample(RestJsonWrapperBean wrapper) {
        getService().handSample(wrapper);
    }

    @Override
    @Log(value = "现场采样-部分交接", type = LogType.SELECT)
    @RequestMapping(value = "/action/part-handover", method = RequestMethod.POST)
    public void partHandover(RestJsonWrapperBean wrapper) {
        getService().partHandover(wrapper);
    }

    @Override
    @Log(value = "查询采样记录", type = LogType.SELECT)
    @RequestMapping(value = "/queries/sample-record", method = RequestMethod.POST)
    public EmcSampleBean selectSampleRecordDetail(RestJsonWrapperBean wrapper) {
        return getService().selectSampleRecordDetail(wrapper);
    }
    @Override
    @Log(value = "批量导出采样原始记录Excel", type = LogType.SELECT)
    @RequestMapping(value = "/action/export-original-record-excel-zip", method = RequestMethod.POST)
    public String exportOriginalRecordExcelZip(RestJsonWrapperBean wrapper) {
        return getService().exportOriginalRecordExcelZip(wrapper);
    }

}
