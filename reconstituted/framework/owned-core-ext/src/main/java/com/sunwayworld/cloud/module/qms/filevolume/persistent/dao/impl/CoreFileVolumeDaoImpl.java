/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.filevolume.persistent.dao.impl;

import com.sunwayworld.cloud.module.qms.filevolume.bean.CoreFileVolumeBean;
import com.sunwayworld.cloud.module.qms.filevolume.persistent.dao.CoreFileVolumeDao;
import com.sunwayworld.cloud.module.qms.filevolume.persistent.mapper.CoreFileVolumeMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreFileVolumeDaoImpl
extends MybatisDaoSupport<CoreFileVolumeBean, Long>
implements CoreFileVolumeDao {
    @Autowired
    private CoreFileVolumeMapper coreFileVolumeMapper;

    public CoreFileVolumeMapper getMapper() {
        return this.coreFileVolumeMapper;
    }
}
