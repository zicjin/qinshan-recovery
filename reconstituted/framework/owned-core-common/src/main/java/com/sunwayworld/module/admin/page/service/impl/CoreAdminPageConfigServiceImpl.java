/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.admin.page.service.impl;

import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.module.admin.page.bean.CoreAdminPageConfigBean;
import com.sunwayworld.module.admin.page.bean.CoreAdminPageConfigLineBean;
import com.sunwayworld.module.admin.page.dao.CoreAdminPageConfigDao;
import com.sunwayworld.module.admin.page.service.CoreAdminPageConfigLineService;
import com.sunwayworld.module.admin.page.service.CoreAdminPageConfigService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreAdminPageConfigServiceImpl
implements CoreAdminPageConfigService {
    @Autowired
    private CoreAdminPageConfigDao dao;
    @Lazy
    @Autowired
    private CoreAdminPageConfigLineService configLineService;

    @Override
    public CoreAdminPageConfigDao getDao() {
        return this.dao;
    }

    @Override
    @Cacheable(value={"T_CORE_ADMIN_PAGE_CONFIG_LINE.ACTIVATED"}, key="#menuId + '-' + #uri")
    public List<CoreAdminPageConfigLineBean> selectActivatedConfigLineList(String menuId, String uri) {
        CoreAdminPageConfigBean filter = BeanUtils.getPropertyListenerProxy(CoreAdminPageConfigBean.class);
        filter.setMenuId(menuId);
        filter.setRequestUri(uri);
        filter.setActivatedFlag("1");
        CoreAdminPageConfigBean config = this.getDao().selectOneIfPresent(filter, new String[0]);
        if (config == null) {
            return CollectionUtils.emptyList();
        }
        CoreAdminPageConfigLineBean lineFilter = new CoreAdminPageConfigLineBean();
        lineFilter.setConfigId(config.getId());
        return this.configLineService.selectList(lineFilter, new Order[0]);
    }
}
