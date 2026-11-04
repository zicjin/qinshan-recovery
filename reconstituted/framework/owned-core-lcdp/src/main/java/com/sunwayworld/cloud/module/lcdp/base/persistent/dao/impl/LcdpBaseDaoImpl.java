/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.base.persistent.dao.impl;

import com.sunwayworld.cloud.module.lcdp.base.persistent.dao.LcdpBaseDao;
import com.sunwayworld.cloud.module.lcdp.base.persistent.mapper.LcdpBaseMapper;
import com.sunwayworld.framework.database.context.TableContext;
import com.sunwayworld.framework.mybatis.MybatisHelper;
import com.sunwayworld.framework.mybatis.interceptor.MybatisColumnTypeHolder;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.dao.MapDao;
import com.sunwayworld.framework.utils.CollectionUtils;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class LcdpBaseDaoImpl
implements LcdpBaseDao {
    @Autowired
    private LcdpBaseMapper baseMapper;
    @Autowired
    @Lazy
    private MapDao mapDao;

    @Override
    public List<Map<String, Object>> selectByCondition(MapperParameter parameter) {
        List<Map<String, Object>> mapList = this.baseMapper.selectByCondition(parameter);
        if (!CollectionUtils.isEmpty(mapList)) {
            Map<String, Class<?>> columnTypeMap = MybatisColumnTypeHolder.popColumnTypeMap();
            if (!CollectionUtils.isEmpty(columnTypeMap)) {
                mapList.forEach(m -> MybatisHelper.correctValue(columnTypeMap, (Map<String, Object>)m));
            }
            MybatisColumnTypeHolder.setColumnTypeMap(columnTypeMap);
        }
        return mapList;
    }

    @Override
    public void insert(String tableName, Map<String, Object> map) {
        this.mapDao.insert(TableContext.of(tableName), map);
    }

    @Override
    public void insert(String tableName, List<Map<String, Object>> list) {
        this.mapDao.insert(TableContext.of(tableName), list);
    }

    @Override
    public void delete(String tableName, List<String> idList) {
        this.mapDao.deleteByIdList(TableContext.of(tableName), idList);
    }

    @Override
    public void update(String tableName, Map<String, Object> map) {
        this.mapDao.update(TableContext.of(tableName), map, new String[0]);
    }

    @Override
    public void update(String tableName, List<Map<String, Object>> list, String ... updateColumns) {
        this.mapDao.update(TableContext.of(tableName), list, updateColumns);
    }

    @Override
    public void updateIfChanged(String tableName, List<Map<String, Object>> list) {
        this.mapDao.updateIfChanged(TableContext.of(tableName), list);
    }
}
