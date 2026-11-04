/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.design.studio.service.impl;

import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.design.studio.bean.CoreStudioAdvSearchConfigBean;
import com.sunwayworld.module.design.studio.dao.CoreStudioAdvSearchConfigDao;
import com.sunwayworld.module.design.studio.service.CoreStudioAdvSearchConfigService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@GikamBean
@Repository
public class CoreStudioAdvSearchConfigServiceImpl
implements CoreStudioAdvSearchConfigService {
    @Autowired
    private CoreStudioAdvSearchConfigDao configDao;

    @Override
    public CoreStudioAdvSearchConfigDao getDao() {
        return this.configDao;
    }

    @Override
    @Transactional
    public Long save(RestJsonWrapperBean wrapper) {
        CoreStudioAdvSearchConfigBean config = wrapper.parseUnique(CoreStudioAdvSearchConfigBean.class);
        if (config.getId() != null) {
            this.getDao().update(config, new String[]{"JSON"});
        } else {
            CoreStudioAdvSearchConfigBean filter = new CoreStudioAdvSearchConfigBean();
            filter.setUrl(config.getUrl());
            filter.setBlockId(config.getBlockId());
            filter.setCreatedById(LocalContextHelper.getLoginUserId());
            CoreStudioAdvSearchConfigBean matchConfig = this.getDao().selectFirstIfPresent(filter, new Order[0]);
            if (matchConfig != null) {
                config.setId(matchConfig.getId());
                this.getDao().update(config, new String[]{"JSON"});
            } else {
                config.setId(ApplicationContextHelper.getNextIdentity());
                this.getDao().insert(config);
            }
        }
        return config.getId();
    }

    @Override
    @Cacheable(value={"T_CORE_STUDIO_ADVSEARCH_CONFIG"}, keyGenerator="studioAdvancedConfigKeyGenerator")
    public List<CoreStudioAdvSearchConfigBean> getCurrentUserConfigListByUrl(String url) {
        CoreStudioAdvSearchConfigBean filter = new CoreStudioAdvSearchConfigBean();
        filter.setUrl(url);
        filter.setCreatedById(LocalContextHelper.getLoginUserId());
        return this.getDao().selectList(filter, new Order[0]);
    }
}
