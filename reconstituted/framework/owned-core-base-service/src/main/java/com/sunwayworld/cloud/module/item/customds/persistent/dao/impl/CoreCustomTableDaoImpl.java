/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.customds.persistent.dao.impl;

import com.sunwayworld.cloud.module.item.customds.persistent.dao.CoreCustomTableDao;
import com.sunwayworld.cloud.module.item.customds.persistent.mapper.CoreCustomTableMapper;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreCustomTableDaoImpl
implements CoreCustomTableDao {
    @Autowired
    private CoreCustomTableMapper coreCustomTableMapper;

    @Override
    @Cacheable(value={"DATABASE_TABLE"}, key="'ALL'")
    public List<Map<String, Object>> selectAllTable(MapperParameter parameter) {
        return this.coreCustomTableMapper.selectAllTable(parameter);
    }

    @Override
    @Cacheable(value={"DATABASE_TABLE_COLUMN"}, key="#tableName")
    public List<Map<String, Object>> selectColumnByTableName(String tableName) {
        return this.coreCustomTableMapper.selectColumnByTableName(tableName);
    }
}
