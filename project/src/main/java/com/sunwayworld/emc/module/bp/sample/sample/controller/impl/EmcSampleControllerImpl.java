package com.sunwayworld.emc.module.bp.sample.sample.controller.impl;

import com.sunwayworld.emc.module.bp.sample.sample.controller.EmcSampleController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.stereotype.Controller;

@Controller
@GikamBean
public class EmcSampleControllerImpl
implements EmcSampleController {
    @Log(value="\u73b0\u573a\u91c7\u6837\u7f16\u5236\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String editListPage() {
        return "emc/module/bp/sample/samples/emc-sample-edit-list";
    }

    @Override
    @Log(value="\u73b0\u573a\u91c7\u6837\u8be6\u60c5\u9875", type=LogType.CONTROLLER)
    public String detailPage() {
        return "emc/module/bp/sample/samples/emc-sample-detail";
    }

    @Override
    @Log(value="\u70b9\u4f4d\u9009\u62e9\u9875", type=LogType.CONTROLLER)
    public String choosePage() {
        return "emc/module/bp/sample/samples/emc-sample-choose-list";
    }

    @Override
    @Log(value="\u8be6\u60c5\u4fe1\u606f\u8be6\u60c5\u9875", type=LogType.CONTROLLER)
    public String sampleInfoDetailPage() {
        return "emc/module/bp/sample/samples/emc-sample-info-detail";
    }

    @Override
    @Log(value="\u6570\u636e\u5f55\u5165\u8be6\u60c5\u9875", type=LogType.CONTROLLER)
    public String sampleDataInputDetailPage() {
        return "emc/module/bp/sample/samples/emc-sample-data-input-detail";
    }

    @Override
    @Log(value="\u6837\u54c1\u6279\u91cf\u4fee\u6539\u9875", type=LogType.CONTROLLER)
    public String batchOrderPage() {
        return "emc/module/bp/sample/samples/emc-order-batch";
    }

    @Override
    @Log(value="\u6837\u54c1\u548c\u6d4b\u8bd5\u6279\u91cf\u4fee\u6539\u9875", type=LogType.CONTROLLER)
    public String batchSampleTestPage() {
        return "emc/module/bp/sample/samples/emc-sample-test-batch";
    }

    @Override
    @Log(value="\u91c7\u6837\u9009\u62e9\u9875", type=LogType.CONTROLLER)
    public String orderChoosePage() {
        return "emc/module/bp/sample/samples/emc-order-choose-list";
    }

    @Override
    @Log(value="\u6837\u54c1\u5bb9\u5668\u6279\u5f55\u5165\u9875", type=LogType.CONTROLLER)
    public String batchOrderContainerPage() {
        return "emc/module/bp/sample/samples/emc-order-container-batch";
    }

    @Override
    @Log(value="\u6837\u54c1\u5bb9\u5668\u9875", type=LogType.CONTROLLER)
    public String orderContainerPage() {
        return "emc/module/bp/sample/samples/emc-order-container";
    }

    @Override
    @Log(value="\u91c7\u6837\u51c6\u5907\u6837\u54c1\u5bb9\u5668\u9875", type=LogType.CONTROLLER)
    public String orderPrepareContainerPage() {
        return "emc/module/bp/sample/samples/emc-order-prepare-container";
    }

    @Override
    @Log(value="\u91c7\u6837\u4e92\u5ba1\u9875\u9762", type=LogType.CONTROLLER)
    public String auditListPage() {
        return "emc/module/bp/sample/samples/emc-sample-audit-list";
    }

    @Override
    @Log(value="\u73b0\u573a\u91c7\u6837-\u91c7\u6837\u5355\u9644\u4ef6\u4fe1\u606f\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String sampleAnnexListPage() {
        return "emc/module/bp/sample/samples/emc-sample-annex";
    }

    @Override
    @Log(value="\u73b0\u573a\u91c7\u6837-\u91c7\u6837\u5355\u70b9\u4f4d\u793a\u610f\u56fe\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String sampleFolderPicPage() {
        return "emc/module/bp/sample/samples/emc-sample-folder-pic";
    }

    @Override
    @Log(value="\u73b0\u573a\u91c7\u6837-\u6570\u636e\u5f55\u5165-\u7ed3\u679c-\u8bbe\u7f6e\u91cd\u590d\u6570", type=LogType.CONTROLLER)
    public String repeatNumberSet() {
        return "emc/module/bp/sample/samples/emc-task-result-repeat-number";
    }

    @Override
    @Log(value="\u73b0\u573a\u91c7\u6837-\u6570\u636e\u5f55\u5165-\u7ed3\u679c-\u8bbe\u7f6e\u5b8c\u6210-\u65e0\u6548\u7ed3\u679c", type=LogType.CONTROLLER)
    public String invalidResult() {
        return "emc/module/bp/sample/samples/emc-task-result-invalid-result";
    }

    @Override
    @Log(value="\u7f16\u8f91\u5206\u6790\u9879", type=LogType.CONTROLLER)
    public String editItem() {
        return "emc/module/bp/sample/samples/emc-sample-edit-item-list";
    }

    @Log(value="\u6837\u54c1\u4ea4\u63a5", type=LogType.CONTROLLER)
    public String handSample() {
        return "emc/module/bp/sample/samples/emc-sample-hand-sample-list";
    }

    @Override
    @Log(value="\u73b0\u573a\u6570\u636e\u4fee\u6539-\u8be6\u60c5\u4fe1\u606f\u8be6\u60c5\u9875", type=LogType.CONTROLLER)
    public String sampleEditInfoDetailPage() {
        return "emc/module/bp/edit/sample-edits/emc-sample-edit-info-detail";
    }

    @Override
    @Log(value="\u73b0\u573a\u6570\u636e\u4fee\u6539-\u6570\u636e\u5f55\u5165\u8be6\u60c5\u9875", type=LogType.CONTROLLER)
    public String sampleEditDataInputDetailPage() {
        return "emc/module/bp/edit/sample-edits/emc-sample-edit-data-input-detail";
    }

    @Override
    @Log(value="\u73b0\u573a\u91c7\u6837-\u6570\u636e\u5f55\u5165\u6837\u54c1\u63cf\u8ff0\u8be6\u60c5\u9875", type=LogType.CONTROLLER)
    public String orderDescDetailPage() {
        return "emc/module/bp/sample/samples/emc-sample-order-desc-detail";
    }

    @Log(value="\u73b0\u573a\u91c7\u6837-\u70b9\u4f4d\u5730\u8868\u6c34\u5f55\u5165", type=LogType.CONTROLLER)
    public String folderDBSDetailPage() {
        return "emc/module/bp/sample/samples/emc-sample-folder-dbs-desc-detail";
    }

    @Log(value="\u73b0\u573a\u91c7\u6837-\u70b9\u4f4d\u5730\u4e0b\u6c34\u5f55\u5165", type=LogType.CONTROLLER)
    public String folderDXSDetailPage() {
        return "emc/module/bp/sample/samples/emc-sample-folder-dxs-desc-detail";
    }

    @Log(value="\u73b0\u573a\u91c7\u6837-\u70b9\u4f4d\u5e9f\u6c34\u5f55\u5165", type=LogType.CONTROLLER)
    public String folderFSDetailPage() {
        return "emc/module/bp/sample/samples/emc-sample-folder-fs-desc-detail";
    }

    @Log(value="\u73b0\u573a\u91c7\u6837-\u70b9\u4f4d\u571f\u58e4\u5f55\u5165", type=LogType.CONTROLLER)
    public String folderTRDetailPage() {
        return "emc/module/bp/sample/samples/emc-sample-folder-tr-desc-detail";
    }

    @Log(value="\u73b0\u573a\u91c7\u6837-\u70b9\u4f4d\u6c89\u79ef\u7269\u5f55\u5165", type=LogType.CONTROLLER)
    public String folderCJWDetailPage() {
        return "emc/module/bp/sample/samples/emc-sample-folder-cjw-desc-detail";
    }

    @Override
    @Log(value="\u73b0\u573a\u91c7\u6837-\u6570\u636e\u5f55\u5165\u6279\u5f55\u5165\u9875", type=LogType.CONTROLLER)
    public String orderResultBatchInputPage() {
        return "emc/module/bp/sample/samples/emc-order-result-batch-input";
    }

    @Override
    @Log(value="\u7ed3\u679c\u5f55\u5165-\u6309\u91c7\u6837\u5355\u9875\u9762", type=LogType.CONTROLLER)
    public String resultInputPage() {
        return "emc/module/bp/sample/samples/emc-sample-result-input-list";
    }

    @Override
    @Log(value="\u91c7\u6837\u8bbe\u5907\u9875\u9762", type=LogType.CONTROLLER)
    public String samplingEquiptPage() {
        return "emc/module/bp/sample/samples/emc-sample-sampling-equipt-list";
    }

    @Override
    @Log(value="\u91c7\u6837\u8be6\u60c5\u6279\u5f55\u5165\u9875\u9762", type=LogType.CONTROLLER)
    public String sampleDetailPage() {
        return "emc/module/bp/sample/samples/emc-sample-detail-list";
    }

    @Override
    @Log(value="\u91c7\u6837\u65f6\u95f4\u6279\u5f55\u5165\u9875\u9762", type=LogType.CONTROLLER)
    public String sampleDatePage() {
        return "emc/module/bp/sample/samples/emc-sample-date-list";
    }
}
