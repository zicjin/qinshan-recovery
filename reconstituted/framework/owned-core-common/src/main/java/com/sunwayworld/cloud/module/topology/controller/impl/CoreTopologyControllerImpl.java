/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.topology.controller.impl;

import com.sunwayworld.cloud.module.topology.controller.CoreTopologyController;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.stereotype.Controller;

@GikamBean
@Controller
public class CoreTopologyControllerImpl
implements CoreTopologyController {
    @Override
    public String editListPage() {
        return "module/topologies/core-topology-edit-list";
    }
}
