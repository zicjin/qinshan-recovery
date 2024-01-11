package com.sunwayworld.emc.module.bp.result.pretreatment.controller.impl;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.bp.result.pretreatment.controller.EmcPretreatmentController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author zhengxy@sunwayworld.com 2023年08月14日
 */
@Controller
@GikamBean
public class EmcPretreatmentControllerImpl implements EmcPretreatmentController {

    @Override
    @Log(value = "前处理编制列表页", type = LogType.CONTROLLER)
    public String editListPage() {
        return "emc/module/bp/result/pretreatments/emc-pretreatment-edit-list";
    }

    @Override
    @Log(value = "前处理选择列表页", type = LogType.CONTROLLER)
    public String choosePage() {
        return "emc/module/bp/result/pretreatments/emc-pretreatment-choose-list";
    }

    @Override
    @RequestMapping("/extract-method-choose-list")
    @Log(value = "提取方法选择列表页", type = LogType.CONTROLLER)
    public String extractMethodChoosePage() {
        return "emc/module/bp/result/pretreatments/emc-extract-method-choose-list";
    }

    @Override
    @RequestMapping("/purify-method-choose-list")
    @Log(value = "净化方法选择列表页", type = LogType.CONTROLLER)
    public String purifyMethodChoosePage() {
        return "emc/module/bp/result/pretreatments/emc-purify-method-choose-list";
    }

}
