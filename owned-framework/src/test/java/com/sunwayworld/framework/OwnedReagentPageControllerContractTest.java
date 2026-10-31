package com.sunwayworld.framework;

import com.sunwayworld.owned.internal.web.OwnedReagentPageController;
import java.lang.reflect.Method;
import org.springframework.web.bind.annotation.GetMapping;

/** Contract guard for the original reagent page route facade. */
public final class OwnedReagentPageControllerContractTest {
    public static void main(String[] args) throws Exception {
        OwnedReagentPageController controller = new OwnedReagentPageController();
        if (!"emc/module/bp/reagents/emc-reagent-edit-list".equals(controller.emcEditList())
                || !"emc/module/bp/reagents/emc-mtl-stock-choose-list".equals(controller.emcStockChooseList())) {
            throw new AssertionError("reagent page view names changed");
        }
        int mapped = 0;
        for (Method method : OwnedReagentPageController.class.getDeclaredMethods()) {
            if (method.getAnnotation(GetMapping.class) != null) mapped++;
        }
        if (mapped != 4) throw new AssertionError("reagent page route count=" + mapped);
        System.out.println("owned reagent page controller contract checks passed (" + mapped + " routes)");
    }
}
