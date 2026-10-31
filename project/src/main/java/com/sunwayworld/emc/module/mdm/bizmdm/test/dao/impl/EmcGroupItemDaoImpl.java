package com.sunwayworld.emc.module.mdm.bizmdm.test.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcGroupItemBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.dao.EmcGroupItemDao;
import com.sunwayworld.emc.module.mdm.bizmdm.test.mapper.EmcGroupItemMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;


/**
 * @author yuanhh@sunwayworld.com
 * @date 2019-09-19
 */
@Repository
public class EmcGroupItemDaoImpl extends MybatisDaoSupport<EmcGroupItemBean, Long>implements EmcGroupItemDao {
    
    @Autowired
    private EmcGroupItemMapper emcGroupItemMapper;
    
    @Override
    public EmcGroupItemMapper getMapper() {
        return emcGroupItemMapper;
    }
    
}
