package com.sunwayworld.emc.module.mdm.basemdm.indexitem.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.basemdm.indexitem.bean.EmcIndexTestItemBean;
import com.sunwayworld.emc.module.mdm.basemdm.indexitem.dao.EmcIndexTestItemDao;
import com.sunwayworld.emc.module.mdm.basemdm.indexitem.mapper.EmcIndexTestItemMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.support.PersistableMetadataHelper;


/**
 * @author magh@sunwayworld.com
 * @date 2019-09-17
 */
@Repository
public class EmcIndexTestItemDaoImpl extends MybatisDaoSupport<EmcIndexTestItemBean, Long>implements EmcIndexTestItemDao {
    
    @Autowired
    private EmcIndexTestItemMapper indexTestItemMapper;
    
    @Override
    public EmcIndexTestItemMapper getMapper() {
        return indexTestItemMapper;
    }
    
    
    public List<Map<String, Object>> selectIndexTestItemLimit(MapperParameter parameter) {
        parameter.setTableName(PersistableMetadataHelper.getTableName(this.getType()));
        return getMapper().selectIndexTestItemLimit(parameter);
    }
}
