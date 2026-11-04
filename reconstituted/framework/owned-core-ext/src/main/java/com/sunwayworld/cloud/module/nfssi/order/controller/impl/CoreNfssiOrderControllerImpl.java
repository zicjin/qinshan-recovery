/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.nfssi.order.controller.impl;

import com.sunwayworld.cloud.module.nfssi.order.controller.CoreNfssiOrderController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.stereotype.Controller;

@LogModule(value="\u62bd\u6837\u5355")
@Controller
@GikamBean
public class CoreNfssiOrderControllerImpl
implements CoreNfssiOrderController {
    @Override
    @Log(value="\u62bd\u6837\u5355\u7f16\u5236\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String editListPage() {
        return "module/nfssi/orders/core-nfssi-order-edit-list";
    }
}
