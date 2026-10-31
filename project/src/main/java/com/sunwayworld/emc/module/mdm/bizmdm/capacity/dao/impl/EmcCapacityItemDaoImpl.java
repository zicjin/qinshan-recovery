package com.sunwayworld.emc.module.mdm.bizmdm.capacity.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.bizmdm.capacity.bean.EmcCapacityItemBean;
import com.sunwayworld.emc.module.mdm.bizmdm.capacity.dao.EmcCapacityItemDao;
import com.sunwayworld.emc.module.mdm.bizmdm.capacity.mapper.EmcCapacityItemMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;


/**
 * @author yuanhh@sunwayworld.com
 * @date 2019-09-19
 */
@Repository
public class EmcCapacityItemDaoImpl extends MybatisDaoSupport<EmcCapacityItemBean, Long>implements EmcCapacityItemDao {

    @Autowired
    private EmcCapacityItemMapper capacityItemMapper;

    @Override
    public EmcCapacityItemMapper getMapper() {
        return capacityItemMapper;
    }

    @Override
    public List<Map<String, Object>> selectTestItem(MapperParameter parameter) {
        return getMapper().selectTestItem(parameter);
    }
}
