package com.sunwayworld.emc.module.mdm.equipt.equipt.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptTestItemBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.dao.EmcEquiptTestItemDao;
import com.sunwayworld.emc.module.mdm.equipt.equipt.mapper.EmcEquiptTestItemMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;


/**
 * @author magh@sunwayworld.com
 * @date 2019-11-13
 */
@Repository
public class EmcEquiptTestItemDaoImpl extends MybatisDaoSupport<EmcEquiptTestItemBean, Long>implements EmcEquiptTestItemDao {
    
    @Autowired
    private EmcEquiptTestItemMapper emcEquiptTestItemMapper;
    
    @Override
    public EmcEquiptTestItemMapper getMapper() {
        return emcEquiptTestItemMapper;
    }
    
}
