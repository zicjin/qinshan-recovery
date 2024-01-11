package com.sunwayworld.emc.module.item.quotation.quotationregister.controller.impl;

import org.springframework.stereotype.Controller;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.emc.module.item.quotation.quotationregister.controller.EmcQuotationregisterController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;

@Controller
@GikamBean
public class EmcQuotationregisterControllerImpl implements EmcQuotationregisterController {

    @Log(value = "报价单登记表编制列表页", type = LogType.CONTROLLER)
    @Override
    public String editListPage() {
        return "emc/module/item/quotation/quotationregisters/emc-quotationregister-edit-list";
    }

    @Log(value = "报价单登记表详情页", type = LogType.CONTROLLER)
    @Override
    public String detailPage() {
        return "emc/module/item/quotation/quotationregisters/emc-quotationregister-detail";
    }

    @Log(value = "报价单登记表审核页", type = LogType.CONTROLLER)
    @Override
    public String auditListPage() {
        return "emc/module/item/quotation/quotationregisters/emc-quotationregister-audit-list";
    }

    @Log(value = "报价单登记表查询页", type = LogType.CONTROLLER)
    @Override
    public String searchListPage() {
        return "emc/module/item/quotation/quotationregisters/emc-quotationregister-search-list";
    }

    @Log(value = "信息批量修改页", type = LogType.CONTROLLER)
    @Override
    public String batchUpdateInformationPage() {
        return "emc/module/item/quotation/quotationregisters/emc-quotationregister-information-batch-update";
    }
}
