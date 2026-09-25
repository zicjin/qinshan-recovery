package com.sunwayworld.emc.module.mdm.bizmdm.test.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.mdm.bizmdm.test.controller.EmcTestMethodController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author yuanhh@sunwayworld.com
 * @date 2019-09-19
 */

@Controller
@GikamBean
public class EmcTestMethodControllerImpl implements EmcTestMethodController {

    @Log(value = "测试方法编制列表页", type = LogType.CONTROLLER)
    @Override
    public String editListPage() {
        return "emc/module/mdm/bizmdm/tests/emc-test-method-edit-list";
    }

    @Log(value = "修改静态数据列表页", type = LogType.CONTROLLER)
    @Override
    public String updateListPage() {
        return "emc/module/mdm/bizmdm/tests/emc-test-method-update-list";
    }

    @Log(value = "测试方法选择页", type = LogType.CONTROLLER)
    @Override
    public String choosePage() {
        return "emc/module/mdm/bizmdm/tests/emc-test-method-choose-list";
    }

    @Log(value = "分析项选择页", type = LogType.CONTROLLER)
    @Override
    public String itemChoosePage() {
        return "emc/module/mdm/bizmdm/tests/emc-item-choose-list";
    }

    @Log(value = "分析项组选择页", type = LogType.CONTROLLER)
    @Override
    public String itemGroupChoosePage() {
        return "emc/module/mdm/bizmdm/tests/emc-item-group-choose-list";
    }

    @Log(value = "分析项结果选择页", type = LogType.CONTROLLER)
    @Override
    public String itemResultChoosePage() {
        return "emc/module/mdm/bizmdm/tests/emc-item-result-choose-list";
    }

    @Log(value = "组分析项选择页", type = LogType.CONTROLLER)
    @Override
    public String groupItemChoosePage() {
        return "emc/module/mdm/bizmdm/tests/emc-group-item-choose-list";
    }

    @Log(value = "测试方法详情页", type = LogType.CONTROLLER)
    @Override
    public String detailPage() {
        return "emc/module/mdm/bizmdm/tests/emc-test-method-detail";
    }

    @Log(value = "测试分析项详情页", type = LogType.CONTROLLER)
    @Override
    public String itemDetailPage() {
        return "emc/module/mdm/bizmdm/tests/emc-item-detail";
    }

    @Log(value = "弹窗录入计算公式页面跳转", type = LogType.CONTROLLER)
    @Override
    public String scriptPage() {
        return "emc/module/mdm/bizmdm/tests/emc-item-script-detail";
    }

    @Log(value = "弹窗录入可能的结果页面跳转", type = LogType.CONTROLLER)
    @Override
    public String resultListPage() {
        return "emc/module/mdm/bizmdm/tests/emc-item-result-list";
    }

    @Log(value = "弹窗录入QC指标和分段修约页面跳转", type = LogType.CONTROLLER)
    @Override
    public String qcindexRevisionListPage() {
        return "emc/module/mdm/bizmdm/tests/emc-item-qcindex-revision-list";
    }

    @Log(value = "测试方法复制页", type = LogType.CONTROLLER)
    @Override
    public String copyPage() {
        return "emc/module/mdm/bizmdm/tests/emc-test-method-copy";
    }

    @Log(value = "测试分析项复制页", type = LogType.CONTROLLER)
    @Override
    public String copyItemPage() {
        return "emc/module/mdm/bizmdm/tests/emc-item-copy";
    }

    @Log(value = "测试分析项批量修改页", type = LogType.CONTROLLER)
    @Override
    public String batchItemPage() {
        return "emc/module/mdm/bizmdm/tests/emc-item-batch";
    }

    @Log(value = "测试库编辑页", type = LogType.CONTROLLER)
    @Override
    public String testEditListPage() {
        return "emc/module/mdm/bizmdm/tests/emc-test-edit-list";
    }

    @Log(value = "测试库选择页", type = LogType.CONTROLLER)
    @Override
    public String testChoosePage() {
        return "emc/module/mdm/bizmdm/tests/emc-test-choose-list";
    }

    @Log(value = "测试分析项复制到选择页", type = LogType.CONTROLLER)
    @Override
    public String itemCopyToPage() {
        return "emc/module/mdm/bizmdm/tests/emc-item-copy-to";
    }

    @Log(value = "培训课程测试方法选择页", type = LogType.CONTROLLER)
    @Override
    public String tcTestMethodChoosePage() {
        return "emc/module/mdm/bizmdm/tests/emc-tc-test-method-choose-list";
    }

    @Log(value = "检测方法修改测试方法选择页", type = LogType.CONTROLLER)
    @Override
    public String checkMethodEditChoosePage() {
        return "emc/module/mdm/bizmdm/tests/emc-check-method-edit-test-method-choose-list";
    }

    @Log(value = "测试管理方法参数设置页", type = LogType.CONTROLLER)
    @Override
    public String methodParamDetailPage() {
        return "emc/module/mdm/bizmdm/tests/emc-method-param-edit-test-method-list";
    }

    @Log(value = "分析录入方法参数设置页", type = LogType.CONTROLLER)
    @Override
    public String resultRunMethodParamDetailPage() {
        return "emc/module/mdm/bizmdm/tests/emc-result-run-method-param-edit-test-method-list";
    }
    
    @Log(value = "修改静态数据分析项详情页", type = LogType.CONTROLLER)
    @Override
    public String itemUpdateDetailPage() {
        return "emc/module/mdm/bizmdm/tests/emc-item-update-detail";
    }
    
    @Log(value = "测试管理历史版本页", type = LogType.CONTROLLER)
    @Override
    public String testMethodLogPage() {
        return "emc/module/mdm/bizmdm/tests/emc-test-method-log-list";
    }
    
    @Log(value = "测试分析项批量修改页", type = LogType.CONTROLLER)
    @Override
    public String batchQcIndexPage() {
        return "emc/module/mdm/bizmdm/tests/emc-qcindex-batch";
    }

    @Log(value = "测试方法前处理选择页", type = LogType.CONTROLLER)
    @Override
    public String testMethodBeforeHandleChoosePage() {
        return "emc/module/mdm/bizmdm/tests/emc-testmethod-beforehandle-choose-list";
    }

    @Log(value = "测试方法编辑前处理弹出框", type = LogType.CONTROLLER)
    @Override
    public String testMethodBeforeHandleEditPage() {
        return "emc/module/mdm/bizmdm/tests/emc-testmethod-beforehandle-edit-list";
    }
}
