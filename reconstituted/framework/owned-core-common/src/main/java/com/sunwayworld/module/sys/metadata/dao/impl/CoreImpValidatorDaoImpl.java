/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.metadata.dao.impl;

import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.metadata.bean.CoreImpValidatorBean;
import com.sunwayworld.module.sys.metadata.dao.CoreImpValidatorDao;
import com.sunwayworld.module.sys.metadata.mapper.CoreMetadataMapper;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreImpValidatorDaoImpl
extends MybatisDaoSupport<CoreImpValidatorBean, Long>
implements CoreImpValidatorDao {
    @Autowired
    private CoreMetadataMapper metadataMapper;

    @Override
    public List<Map<String, Object>> selectImpValidatorByTable(String table) {
        return this.metadataMapper.selectImpValidatorByTable(table);
    }
}
