package com.sunwayworld.emc.module.item.car.controller.impl;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.item.car.controller.EmcCarController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author cuikx@sunwayworld.com
 * @date 2019-11-12
 */

@Controller
@GikamBean()
public class EmcCarControllerImpl implements EmcCarController {

    @Log(value = "用车申请编制列表页", type = LogType.CONTROLLER)
    @Override
    @RequestMapping("/apply/edit-list")
    public String applyEditListPage() {
        return "emc/module/item/cars/emc-car-apply-edit-list";
    }

    @Log(value = "用车审批编制列表页", type = LogType.CONTROLLER)
    @Override
    @RequestMapping("/audit/edit-list")
    public String auditEditListPage() {
        return "emc/module/item/cars/emc-car-audit-edit-list";
    }

    @Log(value = "用车信息记录编制列表页", type = LogType.CONTROLLER)
    @Override
    @RequestMapping("/record/edit-list")
    public String recordEditListPage() {
        return "emc/module/item/cars/emc-car-record-edit-list";
    }

    @Log(value = "汽车使用归还编制列表页", type = LogType.CONTROLLER)
    @Override
    public String editListPage() {
        return "emc/module/item/cars/emc-car-edit-list";
    }

    @Log(value = "汽车使用归还详情页", type = LogType.CONTROLLER)
    @Override
    public String detailPage() {
        return "emc/module/item/cars/emc-car-detail";
    }

    @Log(value = "汽车使用归还选择页", type = LogType.CONTROLLER)
    @Override
    public String choosePage() {
        return "emc/module/item/cars/emc-car-choose-list";
    }

    @Log(value = "用车申请查询", type = LogType.CONTROLLER)
    @Override
    public String searchListPage() {
        // TODO Auto-generated method stub
        return "emc/module/item/cars/emc-car-search-list";
    }

}
