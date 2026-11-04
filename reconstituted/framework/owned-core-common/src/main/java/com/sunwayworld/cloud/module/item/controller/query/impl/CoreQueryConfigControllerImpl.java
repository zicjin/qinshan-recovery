/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.controller.query.impl;

import com.sunwayworld.cloud.module.item.controller.query.CoreQueryConfigController;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@GikamBean
public class CoreQueryConfigControllerImpl
implements CoreQueryConfigController {
    @Override
    public String editListPage() {
        return "module/item/query-configs/core-query-config-edit-list";
    }

    @Override
    public String detailPage() {
        return "module/item/query-configs/core-query-config-detail";
    }

    @Override
    public String searchListPage() {
        return "module/item/query-configs/core-query-config-search-list";
    }

    @Override
    @RequestMapping(value={"/single-search-list"})
    public String singleSearchListPage() {
        return "module/item/query-configs/core-query-config-single-search-list";
    }
}
