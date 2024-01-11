package com.sunwayworld.emc.module.mdm.equipt.equipt.controller.impl;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.mdm.equipt.equipt.controller.EmcEquiptController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Controller
@GikamBean
public class EmcEquiptControllerImpl implements EmcEquiptController {

    @Override
    @Log(value = "仪器管理编制列表页面", type = LogType.CONTROLLER)
    public String editListPage() {
        return "emc/module/mdm/equipt/equipts/emc-equipt-edit-list";
    }

    @Override
    @Log(value = "仪器管理详情页面", type = LogType.CONTROLLER)
    public String detailPage() {
        return "emc/module/mdm/equipt/equipts/emc-equipt-detail";
    }

    @Override
    @Log(value = "仪器管理弹出选择页面", type = LogType.CONTROLLER)
    public String choosePage() {
        return "emc/module/mdm/equipt/equipts/emc-equipt-choose-list";
    }

    @Override
    @Log(value = "仪器管理弹出简单选择页面", type = LogType.CONTROLLER)
    public String simpleChoosePage() {
        return "emc/module/mdm/equipt/equipts/emc-equipt-simple-choose-list";
    }

    @Override
    @RequestMapping("/plan-add")
    @Log(value = "维护计划新增弹出页面", type = LogType.CONTROLLER)
    public String planAddPage() {
        return "emc/module/mdm/equipt/equipts/emc-equipt-plan-add";
    }

    @Override
    @RequestMapping("/per-check-plan-add")
    @Log(value = "期间校验计划新增弹出页面", type = LogType.CONTROLLER)
    public String perCheckAddPage() {
        return "emc/module/mdm/equipt/equipts/emc-equipt-per-check-plan-add";
    }

    @Override
    @RequestMapping("/per-check-record-add")
    @Log(value = "期间校验记录新增弹出页面", type = LogType.CONTROLLER)
    public String perCheckRecordAddPage() {
        return "emc/module/mdm/equipt/equipts/emc-equipt-per-check-record-add";
    }

    @Override
    @RequestMapping("/record-add")
    @Log(value = "维护记录新增弹出页面", type = LogType.CONTROLLER)
    public String recordAddPage() {
        return "emc/module/mdm/equipt/equipts/emc-equipt-record-add";
    }

    @Override
    @RequestMapping("/use-record-update")
    @Log(value = "仪器使用记录维护页面", type = LogType.CONTROLLER)
    public String useRecordUpdatePage() {
        return "emc/module/mdm/equipt/equipts/emc-equipt-use-record-update";
    }

    @Override
    @RequestMapping("/batch-update-record-detail")
    @Log(value = "批量修改仪器使用记录页面", type = LogType.CONTROLLER)
    public String batchUpdateRecordDetailPage() {
        return "emc/module/mdm/equipt/equipts/emc-equipt-batch-update-record-detail";
    }

    @Override
    @RequestMapping("/use-record-edit-list")
    @Log(value = "仪器使用记录编制页面", type = LogType.CONTROLLER)
    public String useRecordEditListPage() {
        return "emc/module/mdm/equipt/equipts/emc-equipt-use-record-edit-list";
    }

    @Override
    @RequestMapping("/cali-choose-list")
    @Log(value = "仪器检定选择页面", type = LogType.CONTROLLER)
    public String caliChoosePage() {
        return "emc/module/mdm/equipt/equipts/emc-equipt-cali-choose";
    }
}
