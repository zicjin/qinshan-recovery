/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.menu.persistent.dao.impl;

import com.sunwayworld.cloud.module.mobile.menu.bean.MobilePageBean;
import com.sunwayworld.cloud.module.mobile.menu.persistent.dao.MobilePageDao;
import com.sunwayworld.cloud.module.mobile.menu.persistent.mapper.MobilePageMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class MobilePageDaoImpl
extends MybatisDaoSupport<MobilePageBean, Long>
implements MobilePageDao {
    @Autowired
    public MobilePageMapper mobilePageMapper;

    public MobilePageMapper getMapper() {
        return this.mobilePageMapper;
    }

    @Override
    public List<Map<String, Object>> selectMobilePageTreeList(MapperParameter parameter) {
        return this.getMapper().selectMobilePageTreeList(parameter);
    }
}
