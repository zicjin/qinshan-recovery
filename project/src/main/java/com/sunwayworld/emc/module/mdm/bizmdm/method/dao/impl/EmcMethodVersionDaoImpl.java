package com.sunwayworld.emc.module.mdm.bizmdm.method.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.bizmdm.method.bean.EmcMethodVersionBean;
import com.sunwayworld.emc.module.mdm.bizmdm.method.dao.EmcMethodVersionDao;
import com.sunwayworld.emc.module.mdm.bizmdm.method.mapper.EmcMethodVersionMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;

/**
 * @author yuanhh@sunwayworld.com 2019年9月10日
 */

@Repository
public class EmcMethodVersionDaoImpl extends MybatisDaoSupport<EmcMethodVersionBean, Long>implements EmcMethodVersionDao {

    @Autowired
    private EmcMethodVersionMapper emcMethodVersionMapper;

    @Override
    public EmcMethodVersionMapper getMapper() {
        return emcMethodVersionMapper;
    }

}
