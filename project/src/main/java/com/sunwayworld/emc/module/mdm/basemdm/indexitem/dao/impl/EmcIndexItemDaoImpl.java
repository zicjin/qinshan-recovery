package com.sunwayworld.emc.module.mdm.basemdm.indexitem.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.basemdm.indexitem.bean.EmcIndexItemBean;
import com.sunwayworld.emc.module.mdm.basemdm.indexitem.dao.EmcIndexItemDao;
import com.sunwayworld.emc.module.mdm.basemdm.indexitem.mapper.EmcIndexItemMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;


/**
 * @author magh@sunwayworld.com
 * @date 2019-09-17
 */
@Repository
public class EmcIndexItemDaoImpl extends MybatisDaoSupport<EmcIndexItemBean, Long>implements EmcIndexItemDao {
    
    @Autowired
    private EmcIndexItemMapper indexItemMapper;
    
    @Override
    public EmcIndexItemMapper getMapper() {
        return indexItemMapper;
    }

}
