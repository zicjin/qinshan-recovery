package com.sunwayworld.emc.module.mdm.basemdm.mtl.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.mdm.basemdm.mtl.controller.EmcMtlUseRecordController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Controller
@GikamBean
public class EmcUseRecordControllerImpl implements EmcMtlUseRecordController {

    @Log(value = "材料使用记录编制列表页", type = LogType.CONTROLLER)
    @Override
    public String editListPage() {
        return "emc/module/mdm/basemdm/mtl-use-records/emc-mtl-use-record-edit-list";
    }

    @Log(value = "材料使用记录详情页", type = LogType.CONTROLLER)
    @Override
    public String detailPage() {
        return "emc/module/mdm/basemdm/mtl-use-records/emc-mtl-use-record-detail";
    }

    @Log(value = "材料使用记录查询列表页", type = LogType.CONTROLLER)
    @Override
    public String searchListPage() {
        return "emc/module/mdm/basemdm/mtl-use-records/emc-mtl-use-record-search-list";
    }

    @Log(value = "材料使用记录选择列表页", type = LogType.CONTROLLER)
    @Override
    public String choosePage() {
        return "emc/module/mdm/basemdm/mtl-use-records/emc-mtl-use-record-choose-list";
    }

    @Log(value = "材料使用记录页", type = LogType.CONTROLLER)
	@Override
	public String applyPage() {
		return "emc/module/mdm/basemdm/mtl-use-records/emc-mtl-use-record-apply";
	}

}
