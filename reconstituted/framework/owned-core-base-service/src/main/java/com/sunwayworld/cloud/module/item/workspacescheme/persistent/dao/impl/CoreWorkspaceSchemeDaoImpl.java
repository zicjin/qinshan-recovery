/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.workspacescheme.persistent.dao.impl;

import com.sunwayworld.cloud.module.item.workspacescheme.bean.CoreWorkspaceSchemeBean;
import com.sunwayworld.cloud.module.item.workspacescheme.persistent.dao.CoreWorkspaceSchemeDao;
import com.sunwayworld.cloud.module.item.workspacescheme.persistent.mapper.CoreWorkspaceSchemeMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreWorkspaceSchemeDaoImpl
extends MybatisDaoSupport<CoreWorkspaceSchemeBean, Long>
implements CoreWorkspaceSchemeDao {
    @Autowired
    private CoreWorkspaceSchemeMapper schemeMapper;

    public CoreWorkspaceSchemeMapper getMapper() {
        return this.schemeMapper;
    }
}
