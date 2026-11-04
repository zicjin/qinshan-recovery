/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.metadata.dao.impl;

import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.metadata.bean.CoreValidatorBean;
import com.sunwayworld.module.sys.metadata.dao.CoreValidatorDao;
import com.sunwayworld.module.sys.metadata.mapper.CoreMetadataMapper;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreValidatorDaoImpl
extends MybatisDaoSupport<CoreValidatorBean, Long>
implements CoreValidatorDao {
    @Autowired
    private CoreMetadataMapper metadataMapper;

    @Override
    @Cacheable(value={"T_CORE_METADATA_VALIDATOR.BY_TABLE"}, key="#table", unless="#result == null")
    public List<CoreValidatorBean> selectEnabledValidatorByTable(String table) {
        return this.metadataMapper.selectEnabledValidatorByTable(table);
    }
}
