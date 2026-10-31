package com.sunwayworld.emc.module.mdm.bizmdm.capacity.dao.impl;

import com.sunwayworld.emc.module.mdm.bizmdm.capacity.bean.EmcCapacityItemGroupBean;
import com.sunwayworld.emc.module.mdm.bizmdm.capacity.dao.EmcCapacityItemGroupDao;
import com.sunwayworld.emc.module.mdm.bizmdm.capacity.mapper.EmcCapacityItemGroupMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


/**
 * @author luoc@sunwayworld.com
 * @date 2021-04-15
 */
@Repository
public class EmcCapacityItemGroupDaoImpl extends MybatisDaoSupport<EmcCapacityItemGroupBean, Long>implements EmcCapacityItemGroupDao {
    
    @Autowired
    private EmcCapacityItemGroupMapper capacityItemGroupMapper;
    
    @Override
    public EmcCapacityItemGroupMapper getMapper() {
        return capacityItemGroupMapper;
    }
    
}
