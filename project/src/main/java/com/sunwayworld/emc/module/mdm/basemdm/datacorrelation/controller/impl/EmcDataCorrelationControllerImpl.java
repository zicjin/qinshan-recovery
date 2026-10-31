package com.sunwayworld.emc.module.mdm.basemdm.datacorrelation.controller.impl;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.mdm.basemdm.datacorrelation.controller.EmcDataCorrelationController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author zhangft@sunwayworld.com@sunwayworld.com
 * @date 2020-01-03
 */

@Controller
@GikamBean
public class EmcDataCorrelationControllerImpl implements EmcDataCorrelationController {

    @Log(value = "数据相关性表编制列表页", type = LogType.CONTROLLER)
    @Override
    public String editListPage() {
        return "emc/module/mdm/basemdm/data-correlations/emc-data-correlation-edit-list";
    }

    @Log(value = "数据相关性表详情页", type = LogType.CONTROLLER)
    @Override
    public String detailPage() {
        return "emc/module/mdm/basemdm/data-correlations/emc-data-correlation-detail";
    }

    @Log(value = "数据相关性弹窗录入计算公式页面跳转", type = LogType.CONTROLLER)
    @Override
    @RequestMapping("/script-detail")
    public String scriptPage() {
        return "emc/module/mdm/basemdm/data-correlations/emc-data-correlation-script-detail";
    }
    
    @Log(value = "数据相关性检查分析项多选页面", type = LogType.CONTROLLER)
    @Override
    @RequestMapping("/item-choose-mult-list")
    public String chooseItemMultPage() {
        return "emc/module/mdm/basemdm/data-correlations/emc-data-correlation-item-choose-mult-list";
    }
}
