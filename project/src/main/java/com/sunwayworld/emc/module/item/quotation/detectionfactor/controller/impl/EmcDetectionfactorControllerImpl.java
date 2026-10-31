package com.sunwayworld.emc.module.item.quotation.detectionfactor.controller.impl;

import org.springframework.stereotype.Controller;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.emc.module.item.quotation.detectionfactor.controller.EmcDetectionfactorController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;

@Controller
@GikamBean
public class EmcDetectionfactorControllerImpl implements EmcDetectionfactorController {

    @Log(value = "检测因子价格管理表编制列表页", type = LogType.CONTROLLER)
    @Override
    public String editListPage() {
        return "emc/module/item/quotation/detectionfactors/emc-detectionfactor-edit-list";
    }

    @Log(value = "检测因子价格表选择列表页", type = LogType.CONTROLLER)
    @Override
    public String choosePage() {
        return "emc/module/item/quotation/detectionfactors/emc-detectionfactor-choose-list";
    }

    @Log(value = "信息批量修改页", type = LogType.CONTROLLER)
    @Override
    public String batchUpdateDetectionPage() {
        return "emc/module/item/quotation/detectionfactors/emc-detectionfactor-detection-batch-update";
    }

    @Log(value = "增加自定义加个", type = LogType.CONTROLLER)
    @Override
    public String addCustomPricePage() {
        return "emc/module/item/quotation/detectionfactors/emc-detectionfactor-add-custom-price";
    }
}
