package com.sunwayworld.emc.module.item.consume.consume.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.item.consume.consume.controller.EmcConsumeController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Controller
@GikamBean
public class EmcConsumeControllerImpl implements EmcConsumeController {

    @Log(value = "耗材管理列表页", type = LogType.CONTROLLER)
    @Override
    public String editListPage() {
        return "emc/module/item/consume/consumes/emc-consume-edit-list";
    }

    @Log(value = "库存入库页", type = LogType.CONTROLLER)
    public String storagePage() {
        return "emc/module/item/consume/consumes/emc-consume-stock-storage";
    }

    @Log(value = "库存盘存页", type = LogType.CONTROLLER)
    public String inventoryPage() {
        return "emc/module/item/consume/consumes/emc-consume-stock-inventory";
    }

    @Log(value = "库存废弃页", type = LogType.CONTROLLER)
    public String abandonPage() {
        return "emc/module/item/consume/consumes/emc-consume-stock-abandon";
    }

    @Log(value = "库存领取页", type = LogType.CONTROLLER)
    public String recievePage() {
        return "emc/module/item/consume/consumes/emc-consume-stock-recieve";
    }
}
