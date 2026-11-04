/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.workspacescheme.persistent.dao.impl;

import com.sunwayworld.cloud.module.item.workspacescheme.bean.CoreWorkspaceSchemePanelConfigBean;
import com.sunwayworld.cloud.module.item.workspacescheme.persistent.dao.CoreWorkspaceSchemePanelConfigDao;
import com.sunwayworld.cloud.module.item.workspacescheme.persistent.mapper.CoreWorkspaceSchemePanelConfigMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreWorkspaceSchemePanelConfigDaoImpl
extends MybatisDaoSupport<CoreWorkspaceSchemePanelConfigBean, Long>
implements CoreWorkspaceSchemePanelConfigDao {
    @Autowired
    private CoreWorkspaceSchemePanelConfigMapper schemePanelConfigMapper;

    public CoreWorkspaceSchemePanelConfigMapper getMapper() {
        return this.schemePanelConfigMapper;
    }
}
