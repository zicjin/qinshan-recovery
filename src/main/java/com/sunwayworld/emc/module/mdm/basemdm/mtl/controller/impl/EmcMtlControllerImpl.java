package com.sunwayworld.emc.module.mdm.basemdm.mtl.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.mdm.basemdm.mtl.controller.EmcMtlController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Controller
@GikamBean
public class EmcMtlControllerImpl implements EmcMtlController {

    @Override
    @Log(value = "材料管理列表页", type = LogType.CONTROLLER)
    public String editListPage() {
        return "emc/module/mdm/basemdm/mtls/emc-mtl-edit-list";
    }


    @Override
    @Log(value = "材料管理查询页", type = LogType.CONTROLLER)
    public String searchListPage() {
        return "emc/module/mdm/basemdm/mtls/emc-mtl-search-list";
    }

    @Override
    @Log(value = "库存入库页", type = LogType.CONTROLLER)
    public String storagePage() {
        return "emc/module/mdm/basemdm/mtls/emc-mtl-stock-storage";
    }

    @Override
    @Log(value = "库存盘存页", type = LogType.CONTROLLER)
    public String inventoryPage() {
        return "emc/module/mdm/basemdm/mtls/emc-mtl-stock-inventory";
    }

    @Override
    @Log(value = "库存废弃页", type = LogType.CONTROLLER)
    public String abandonPage() {
        return "emc/module/mdm/basemdm/mtls/emc-mtl-stock-abandon";
    }

    @Override
    @Log(value = "库存领取页", type = LogType.CONTROLLER)
    public String recievePage() {
        return "emc/module/mdm/basemdm/mtls/emc-mtl-stock-recieve";
    }

    @Override
    @Log(value = "材料管理选择页", type = LogType.CONTROLLER)
    public String choosePage() {
        return "emc/module/mdm/basemdm/mtls/emc-mtl-choose-list";
    }

    @Override
    @Log(value = "材料管理详情选择页", type = LogType.CONTROLLER)
    public String detailChoosePage() {
        return "emc/module/mdm/basemdm/mtls/emc-mtl-detail-choose-list";
    }

    @Override
    @Log(value = "材料库存选择页", type = LogType.CONTROLLER)
    public String stockChoosePage() {
        return "emc/module/mdm/basemdm/mtls/emc-mtl-stock-choose-list";
    }

    @Override
    @Log(value = "一般试剂管理列表页", type = LogType.CONTROLLER)
    public String generalEditListPage() {
        return "emc/module/mdm/basemdm/mtls/emc-mtl-general-edit-list";
    }
}
