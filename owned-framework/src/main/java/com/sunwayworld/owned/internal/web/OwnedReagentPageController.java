package com.sunwayworld.owned.internal.web;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/** Source-owned facade for the original reagent page controller entrypoints. */
@Controller
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public final class OwnedReagentPageController {
    @GetMapping("/secure/core/module/bp/reagents/page/edit-list")
    public String coreEditList() {
        return "emc/module/bp/reagents/emc-reagent-edit-list";
    }

    @GetMapping("/secure/emc/module/bp/reagents/page/edit-list")
    public String emcEditList() {
        return "emc/module/bp/reagents/emc-reagent-edit-list";
    }

    @GetMapping("/secure/core/module/bp/reagents/page/stock-choose-list")
    public String coreStockChooseList() {
        return "emc/module/bp/reagents/emc-mtl-stock-choose-list";
    }

    @GetMapping("/secure/emc/module/bp/reagents/page/stock-choose-list")
    public String emcStockChooseList() {
        return "emc/module/bp/reagents/emc-mtl-stock-choose-list";
    }
}
