package com.sunwayworld.emc.module.mdm.purchapply.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.mdm.purchapply.controller.EmcPurchApplyController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author zhanghy@sunwayworld.com 2020-09-03
 */
@Controller
@GikamBean
public class EmcPurchApplyControllerImpl implements EmcPurchApplyController {

    @Override
    @Log(value = "采购申请页", type = LogType.CONTROLLER)
    public String editListPage() {
        return "emc/module/mdm/purch-applies/emc-purch-apply-edit-list";
    }
    
    @Override
    @Log(value = "采购审核页", type = LogType.CONTROLLER)
    public String auditListPage() {
        return "emc/module/mdm/purch-applies/emc-purch-apply-audit-list";
    }
    
    @Override
    @Log(value = "采购查询页", type = LogType.CONTROLLER)
    public String searchListPage() {
        return "emc/module/mdm/purch-applies/emc-purch-apply-search-list";
    }
    
    @Override
    @Log(value = "采购品类别选择页", type = LogType.CONTROLLER)
    public String choosePage() {
        return "emc/module/mdm/purch-applies/emc-purch-apply-choose-list";
    }
}
