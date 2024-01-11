package com.sunwayworld.emc.module.mdm.basemdm.fixedasset.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.basemdm.fixedasset.bean.EmcFixedassetBean;
import com.sunwayworld.emc.module.mdm.basemdm.fixedasset.dao.EmcFixedassetDao;
import com.sunwayworld.emc.module.mdm.basemdm.fixedasset.mapper.EmcFixedassetMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;


/**
 * @author cuikx@sunwayworld.com
 * @date 2019-11-25
 */
@Repository
public class EmcFixedassetDaoImpl extends MybatisDaoSupport<EmcFixedassetBean, Long>implements EmcFixedassetDao {
    
    @Autowired
    private EmcFixedassetMapper emcFixedassetMapper;
    
    @Override
    public EmcFixedassetMapper getMapper() {
        return emcFixedassetMapper;
    }
    
}
