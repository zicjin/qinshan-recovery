/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.workspacepanel.persistent.dao.impl;

import com.sunwayworld.cloud.module.item.workspacepanel.bean.CoreWorkspacePanelBean;
import com.sunwayworld.cloud.module.item.workspacepanel.persistent.dao.CoreWorkspacePanelDao;
import com.sunwayworld.cloud.module.item.workspacepanel.persistent.mapper.CoreWorkspacePanelMapper;
import com.sunwayworld.framework.cache.redis.RedisHelper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreWorkspacePanelDaoImpl
extends MybatisDaoSupport<CoreWorkspacePanelBean, String>
implements CoreWorkspacePanelDao {
    @Autowired
    private CoreWorkspacePanelMapper panelMapper;

    public CoreWorkspacePanelMapper getMapper() {
        return this.panelMapper;
    }

    @Override
    public void cacheEvict(CoreWorkspacePanelBean oldItem, CoreWorkspacePanelBean newItem) {
        RedisHelper.evict("core-user-panel-config-layout");
    }
}
