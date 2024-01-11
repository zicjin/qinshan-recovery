package com.sunwayworld.emc.module.item.mtlapply.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.item.mtlapply.controller.EmcMtlApplyController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author zhengxy@sunwayworld.com 2020年02月24日
 */
@Controller
@GikamBean
public class EmcMtlApplyControllerImpl implements EmcMtlApplyController {

    @Override
    @Log(value = "材料领用编制列表页面", type = LogType.CONTROLLER)
    public String editListPage() {
        return "emc/module/item/mtl-applies/emc-mtl-apply-edit-list";
    }

    @Override
    @Log(value = "材料领用审核列表页面", type = LogType.CONTROLLER)
    public String auditListPage() {
        return "emc/module/item/mtl-applies/emc-mtl-apply-audit-list";
    }

    @Override
    @Log(value = "材料领用查询列表页面", type = LogType.CONTROLLER)
    public String searchListPage() {
        return "emc/module/item/mtl-applies/emc-mtl-apply-search-list";
    }
}
