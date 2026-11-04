/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.table.persistent.dao.impl;

import com.sunwayworld.cloud.module.lcdp.table.persistent.dao.LcdpDatabaseDao;
import com.sunwayworld.cloud.module.lcdp.table.persistent.mapper.LcdpDatabaseMapper;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class LcdpDatabaseDaoImpl
implements LcdpDatabaseDao {
    @Autowired
    private LcdpDatabaseMapper lcdpDatabaseMapper;

    @Override
    public List<Map<String, Object>> selectFieldListByCondition(MapperParameter mapperParameter) {
        return this.lcdpDatabaseMapper.selectFieldListByCondition(mapperParameter);
    }

    @Override
    public List<Map<String, Object>> selectIndexListByCondition(MapperParameter mapperParameter) {
        return this.lcdpDatabaseMapper.selectIndexListByCondition(mapperParameter);
    }

    @Override
    public List<Map<String, Object>> selectTableNameListByCondition(MapperParameter mapperParameter) {
        return this.lcdpDatabaseMapper.selectTableNameListByCondition(mapperParameter);
    }

    @Override
    public List<Map<String, Object>> selectTableInfoByCondition(MapperParameter mapperParameter) {
        return this.lcdpDatabaseMapper.selectTableInfoByCondition(mapperParameter);
    }

    @Override
    public List<Map<String, Object>> selectViewInfoListByCondition(MapperParameter mapperParameter) {
        return this.lcdpDatabaseMapper.selectViewInfoListByCondition(mapperParameter);
    }

    @Override
    public int countByTableName(String name) {
        return this.lcdpDatabaseMapper.countByTableName(name);
    }

    @Override
    public int countByViewName(String name) {
        return this.lcdpDatabaseMapper.countByViewName(name);
    }

    @Override
    public int countByIndexName(String name) {
        return this.lcdpDatabaseMapper.countByIndexName(name);
    }

    @Override
    public void lockTableByTableName(String name) {
        this.lcdpDatabaseMapper.lockTableByTableName(name);
    }

    @Override
    public List<Map<String, Object>> selectTableOrViewNameListByCondition(MapperParameter mapperParameter) {
        return this.lcdpDatabaseMapper.selectTableOrViewNameListByCondition(mapperParameter);
    }
}
