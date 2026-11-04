/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.workspacescheme.persistent.dao.impl;

import com.sunwayworld.cloud.module.item.workspacescheme.bean.CoreWorkspaceUserSchemePanelConfigBean;
import com.sunwayworld.cloud.module.item.workspacescheme.persistent.dao.CoreWorkspaceUserSchemePanelConfigDao;
import com.sunwayworld.cloud.module.item.workspacescheme.persistent.mapper.CoreWorkspaceUserSchemePanelConfigMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreWorkspaceUserSchemePanelConfigDaoImpl
extends MybatisDaoSupport<CoreWorkspaceUserSchemePanelConfigBean, Long>
implements CoreWorkspaceUserSchemePanelConfigDao {
    @Autowired
    private CoreWorkspaceUserSchemePanelConfigMapper userSchemePanelConfigMapper;

    public CoreWorkspaceUserSchemePanelConfigMapper getMapper() {
        return this.userSchemePanelConfigMapper;
    }
}
