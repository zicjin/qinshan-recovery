/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.office.persistent.dao.impl;

import com.sunwayworld.cloud.module.office.bean.CoreOfficeCustomizationBean;
import com.sunwayworld.cloud.module.office.persistent.dao.CoreOfficeCustomizationDao;
import com.sunwayworld.cloud.module.office.persistent.mapper.CoreOfficeCustomizationMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreOfficeCustomizationDaoImpl
extends MybatisDaoSupport<CoreOfficeCustomizationBean, String>
implements CoreOfficeCustomizationDao {
    @Autowired
    private CoreOfficeCustomizationMapper coreOfficeCustomizationMapper;

    public CoreOfficeCustomizationMapper getMapper() {
        return this.coreOfficeCustomizationMapper;
    }
}
