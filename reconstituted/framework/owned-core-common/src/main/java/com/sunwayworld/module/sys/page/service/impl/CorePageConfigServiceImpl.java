/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.page.service.impl;

import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.page.bean.CorePageConfigDTO;
import com.sunwayworld.module.sys.page.bean.CorePageGridFieldConfigBean;
import com.sunwayworld.module.sys.page.service.CorePageConfigService;
import com.sunwayworld.module.sys.page.service.CorePageGridFieldConfigService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CorePageConfigServiceImpl
implements CorePageConfigService {
    @Lazy
    @Autowired
    private CorePageGridFieldConfigService gridConfigService;

    @Override
    @Cacheable(value={"CORE_PAGE_CONFIG"}, key="T(com.sunwayworld.framework.context.LocalContextHelper).getLoginUserId() + '.' + #pageId")
    public CorePageConfigDTO selectCurrentPageConfig(String pageId) {
        CorePageGridFieldConfigBean filter = new CorePageGridFieldConfigBean();
        filter.setPageId(pageId);
        filter.setUserId(LocalContextHelper.getLoginUserId());
        List<CorePageGridFieldConfigBean> configList = this.gridConfigService.selectList(filter, Order.asc("ORDERNO"));
        CorePageConfigDTO config = new CorePageConfigDTO();
        configList.forEach(c -> config.addGrid(c.getGridId(), (CorePageGridFieldConfigBean)c));
        return config;
    }
}
