package com.sunwayworld.emc.module.mdm.bizmdm.test.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcItemBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.dao.EmcItemDao;
import com.sunwayworld.emc.module.mdm.bizmdm.test.mapper.EmcItemMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;


/**
 * @author yuanhh@sunwayworld.com
 * @date 2019-09-19
 */
@Repository
public class EmcItemDaoImpl extends MybatisDaoSupport<EmcItemBean, Long>implements EmcItemDao {
    
    @Autowired
    private EmcItemMapper emcItemMapper;
    
    @Override
    public EmcItemMapper getMapper() {
        return emcItemMapper;
    }

    @Override
    public List<Map<String, Object>> selectTestItem(MapperParameter parameter) {
        return getMapper().selectTestItem(parameter);
    }
}
