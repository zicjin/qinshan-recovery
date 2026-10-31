package com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.controller.EmcMonitorTmplController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author magh@sunwayworld.com
 * @date 2019-09-19
 */

@Controller
@GikamBean
public class EmcMonitorTmplControllerImpl implements EmcMonitorTmplController {

    @Log(value = "监测模板编制列表页", type = LogType.CONTROLLER)
    @Override
    public String editListPage() {
        return "emc/module/mdm/bizmdm/monitor-tmpls/emc-monitor-tmpl-edit-list";
    }

    @Log(value = "监测模板选择页", type = LogType.CONTROLLER)
    @Override
    public String choosePage() {
        return "emc/module/mdm/bizmdm/monitor-tmpls/emc-monitor-tmpl-choose-list";
    }

    @Log(value = "监测方案添加页面", type = LogType.CONTROLLER)
    @Override
    public String schemeAdditionPage() {
        return "emc/module/mdm/bizmdm/monitor-tmpls/emc-monitor-tmpl-scheme-addition";
    }

    @Log(value = "监测方案编辑页面", type = LogType.CONTROLLER)
    @Override
    public String schemeDetailPage() {
        return "emc/module/mdm/bizmdm/monitor-tmpls/emc-monitor-tmpl-scheme-detail";
    }

    @Log(value = "样品类型编制列表页", type = LogType.CONTROLLER)
    @Override
    public String sampleCategoryEditListPage() {
        return "emc/module/mdm/basemdm/sample-categories/emc-sample-category-edit-list";
    }

    @Log(value = "方案选择页", type = LogType.CONTROLLER)
    @Override
    public String schemeChoosePage() {
        return "emc/module/mdm/bizmdm/monitor-tmpls/emc-monitor-tmpl-scheme-choose-list";
    }

    @Log(value = "方案方法选择页", type = LogType.CONTROLLER)
    @Override
    public String schemeMethodChoosePage() {
        return "emc/module/mdm/bizmdm/monitor-tmpls/emc-monitor-tmpl-scheme-method-choose-list";
    }

    @Log(value = "方案分析项组选择页面", type = LogType.CONTROLLER)
    @Override
    public String schemeItemGroupChoosePage() {
        return "emc/module/mdm/bizmdm/monitor-tmpls/emc-monitor-tmpl-scheme-item-group-choose-list";
    }

    @Log(value = "样品测试选择页面", type = LogType.CONTROLLER)
    @Override
    public String orderCategoryChoosePage() {
        return "emc/module/mdm/bizmdm/monitor-tmpls/emc-order-category-choose-list";
    }

    @Override
    @Log(value = "样品类型选择页面", type = LogType.CONTROLLER)
    public String sampleCategoryChoosePage() {
        return "emc/module/mdm/basemdm/sample-categories/emc-sample-category-choose-list";
    }

    @Override
    @Log(value = "合同登记添加测试页面", type = LogType.CONTROLLER)
    public String schemeTestItemChoosePage() {
        return "emc/module/mdm/bizmdm/monitor-tmpls/emc-monitor-tmpl-scheme-test-item-choose-list";
    }
}
