package com.sunwayworld.emc.module.bp.sample.sample.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.bp.sample.sample.controller.EmcSampleController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author zhangsy@sunwayworld.com
 * @date 2019-10-12
 */

@Controller
@GikamBean
public class EmcSampleControllerImpl implements EmcSampleController{

    @Log(value = "现场采样编制列表页", type = LogType.CONTROLLER)
    @Override
    public String editListPage() {
        return "emc/module/bp/sample/samples/emc-sample-edit-list";
    }

    @Log(value = "现场采样详情页", type = LogType.CONTROLLER)
    @Override
    public String detailPage() {
        return "emc/module/bp/sample/samples/emc-sample-detail";
    }

    @Log(value = "点位选择页", type = LogType.CONTROLLER)
    @Override
    public String choosePage() {
        return "emc/module/bp/sample/samples/emc-sample-choose-list";
    }

    @Log(value = "详情信息详情页", type = LogType.CONTROLLER)
    @Override
    public String sampleInfoDetailPage() {
        return "emc/module/bp/sample/samples/emc-sample-info-detail";
    }

    @Log(value = "数据录入详情页", type = LogType.CONTROLLER)
    @Override
    public String sampleDataInputDetailPage() {
        return "emc/module/bp/sample/samples/emc-sample-data-input-detail";
    }

    @Log(value = "样品批量修改页", type = LogType.CONTROLLER)
    @Override
    public String batchOrderPage() {
        return "emc/module/bp/sample/samples/emc-order-batch";
    }

    @Log(value = "样品和测试批量修改页", type = LogType.CONTROLLER)
    @Override
    public String batchSampleTestPage() {
        return "emc/module/bp/sample/samples/emc-sample-test-batch";
    }

    @Log(value = "采样选择页", type = LogType.CONTROLLER)
    @Override
    public String orderChoosePage() {
        return "emc/module/bp/sample/samples/emc-order-choose-list";
    }

    @Log(value = "样品容器批录入页", type = LogType.CONTROLLER)
    @Override
    public String batchOrderContainerPage() {
        return "emc/module/bp/sample/samples/emc-order-container-batch";
    }

    @Log(value = "样品容器页", type = LogType.CONTROLLER)
    @Override
    public String orderContainerPage() {
        return "emc/module/bp/sample/samples/emc-order-container";
    }

    @Log(value = "采样准备样品容器页", type = LogType.CONTROLLER)
    @Override
    public String orderPrepareContainerPage() {
        return "emc/module/bp/sample/samples/emc-order-prepare-container";
    }

    @Log(value = "采样互审页面", type = LogType.CONTROLLER)
    @Override
    public String auditListPage() {
        return "emc/module/bp/sample/samples/emc-sample-audit-list";
    }

    @Override
    @Log(value = "现场采样-采样单附件信息列表页", type = LogType.CONTROLLER)
    public String sampleAnnexListPage() {
        return "emc/module/bp/sample/samples/emc-sample-annex";
    }

    @Override
    @Log(value = "现场采样-采样单点位示意图列表页", type = LogType.CONTROLLER)
    public String sampleFolderPicPage() {
        return "emc/module/bp/sample/samples/emc-sample-folder-pic";
    }

    @Override
    @Log(value = "现场采样-数据录入-结果-设置重复数", type = LogType.CONTROLLER)
    public String repeatNumberSet() {
        return "emc/module/bp/sample/samples/emc-task-result-repeat-number";
    }

    @Override
    @Log(value = "现场采样-数据录入-结果-设置完成-无效结果", type = LogType.CONTROLLER)
    public String invalidResult() {
        return "emc/module/bp/sample/samples/emc-task-result-invalid-result";
    }

    @Override
    @Log(value = "编辑分析项", type = LogType.CONTROLLER)
    public String editItem() {
        return "emc/module/bp/sample/samples/emc-sample-edit-item-list";
    }

    @Log(value = "现场数据修改-详情信息详情页", type = LogType.CONTROLLER)
    @Override
    public String sampleEditInfoDetailPage() {
        return "emc/module/bp/edit/sample-edits/emc-sample-edit-info-detail";
    }

    @Log(value = "现场数据修改-数据录入详情页", type = LogType.CONTROLLER)
    @Override
    public String sampleEditDataInputDetailPage() {
        return "emc/module/bp/edit/sample-edits/emc-sample-edit-data-input-detail";
    }

    @Override
    @Log(value = "现场采样-数据录入样品描述详情页", type = LogType.CONTROLLER)
    public String orderDescDetailPage() {
        return "emc/module/bp/sample/samples/emc-sample-order-desc-detail";
    }

    @Override
    @Log(value = "现场采样-数据录入批录入页", type = LogType.CONTROLLER)
    public String orderResultBatchInputPage() {
        return "emc/module/bp/sample/samples/emc-order-result-batch-input";
    }
    

    @Override
    @Log(value = "结果录入-按采样单页面", type = LogType.CONTROLLER)
    public String resultInputPage() {
        return "emc/module/bp/sample/samples/emc-sample-result-input-list";
    }

    @Override
    @Log(value = "采样设备页面", type = LogType.CONTROLLER)
    public String samplingEquiptPage() {
        return "emc/module/bp/sample/samples/emc-sample-sampling-equipt-list";
    }

    @Override
    @Log(value = "采样详情批录入页面", type = LogType.CONTROLLER)
    public String sampleDetailPage() {
        return "emc/module/bp/sample/samples/emc-sample-detail-list";
    }

    @Override
    @Log(value = "采样时间批录入页面", type = LogType.CONTROLLER)
    public String sampleDatePage() {
        return "emc/module/bp/sample/samples/emc-sample-date-list";
    }

}
