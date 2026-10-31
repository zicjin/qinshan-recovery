package com.sunwayworld.emc.module.mdm.basemdm.mtl.controller.impl;


import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.mdm.basemdm.mtl.controller.PfMtlController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Controller
@GikamBean
public class PfMtlControllerImpl extends EmcMtlControllerImpl implements PfMtlController {

    @Override
    @Log(value = "材料库存选择页", type = LogType.CONTROLLER)
    public String newStockChoosePage() {
        return "emc/module/mdm/basemdm/mtls/emc-new-mtl-stock-choose-list";
    }
    @Override
    @Log(value = "材料库存核查台账查询", type = LogType.CONTROLLER)
    public String mtlCheckSearchList() {
        return "emc/module/mdm/basemdm/mtls/emc-mtl-check-search-list";
    }

    @Override
    @Log(value = "实验室标准样品-标样选择页", type = LogType.CONTROLLER)
    public String qcStdStockChoosePage() {
        return "emc/module/mdm/basemdm/mtls/emc-mtl-qc-std-stock-choose-list";
    }
}
