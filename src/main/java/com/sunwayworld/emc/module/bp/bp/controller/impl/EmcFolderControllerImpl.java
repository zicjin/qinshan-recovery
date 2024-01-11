package com.sunwayworld.emc.module.bp.bp.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.bp.bp.controller.EmcFolderController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;

/**
 * @author zhangsy@sunwayworld.com
 * @date 2019-10-12
 */

@Controller
public class EmcFolderControllerImpl implements EmcFolderController {

    @Log(value = "点位编制列表页", type = LogType.CONTROLLER)
    @Override
    public String editListPage() {
        return "emc/module/bp/folders/emc-folder-edit-list";
    }

    @Log(value = "点位详情页", type = LogType.CONTROLLER)
    @Override
    public String detailPage() {
        return "emc/module/bp/folders/emc-folder-detail";
    }

    @Log(value = "点位选择页", type = LogType.CONTROLLER)
    @Override
    public String choosePage() {
        return "emc/module/bp/folders/emc-folder-choose-list";
    }

    @Log(value = "详情信息详情页", type = LogType.CONTROLLER)
    @Override
    public String sampleInfoDetailPage() {
        return "emc/module/bp/folders/emc-sample-info-detail";
    }

    @Log(value = "数据录入详情页", type = LogType.CONTROLLER)
    @Override
    public String sampleDataInputDetailPage() {
        return "emc/module/bp/folders/emc-sample-data-input-detail";
    }

    @Log(value = "样品批量修改页", type = LogType.CONTROLLER)
    @Override
    public String batchOrderPage() {
        return "emc/module/bp/folders/emc-order-batch";
    }

    @Log(value = "样品和测试批量修改页", type = LogType.CONTROLLER)
    @Override
    public String batchSampleTestPage() {
        return "emc/module/bp/folders/emc-sample-test-batch";
    }

    @Log(value = "采样选择页", type = LogType.CONTROLLER)
    @Override
    public String orderChoosePage() {
        return "emc/module/bp/folders/emc-order-choose-list";
    }

    @Log(value = "样品容器选择页", type = LogType.CONTROLLER)
    @Override
    public String batchOrderContainerTestPage() {
        return "emc/module/bp/folders/emc-order-container-batch";
    }

    @Log(value = "样品编辑测试页", type = LogType.CONTROLLER)
    @Override
    public String orderItemGroupEditListPage() {
        return "emc/module/bp/folders/emc-order-item-group-edit-list";
    }

    @Log(value = "样品编辑分析项页", type = LogType.CONTROLLER)
    @Override
    public String orderItemEditListPage() {
        return "emc/module/bp/folders/emc-order-item-edit-list";
    }

    @Log(value = "检测项目添加测试页", type = LogType.CONTROLLER)
    @Override
    public String checkProjectOrderItemGroupAdditionPage() {
        return "emc/module/bp/folders/emc-check-project-order-item-group-addition";
    }

    @Log(value = "检测项目编辑分析项页", type = LogType.CONTROLLER)
    @Override
    public String checkProjectOrderItemEditListPage() {
        return "emc/module/bp/folders/emc-check-project-order-item-edit-list";
    }

    @Log(value = "追溯页", type = LogType.CONTROLLER)
    @Override
    public String reviewPage() {
        return "emc/module/bp/folders/emc-folder-review-list";
    }

    @Log(value = "采样准备-样品编辑测试页", type = LogType.CONTROLLER)
    @Override
    public String orderItemGroupSampleEditListPage() {
        return "emc/module/bp/folders/emc-order-item-group-sample-edit-list";
    }

}
