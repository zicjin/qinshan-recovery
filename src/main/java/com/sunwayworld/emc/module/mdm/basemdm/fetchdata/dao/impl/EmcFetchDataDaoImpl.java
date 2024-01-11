package com.sunwayworld.emc.module.mdm.basemdm.fetchdata.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.basemdm.fetchdata.bean.EmcFetchDataBean;
import com.sunwayworld.emc.module.mdm.basemdm.fetchdata.dao.EmcFetchDataDao;
import com.sunwayworld.emc.module.mdm.basemdm.fetchdata.mapper.EmcFetchDataMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;

/**
 * @author zhengxy@sunwayworld.com 2020年03月05日
 */
@Repository
public class EmcFetchDataDaoImpl extends MybatisDaoSupport<EmcFetchDataBean, Long> implements EmcFetchDataDao {

    @Autowired
    private EmcFetchDataMapper emcFetchDataMapper;

    @Override
    public EmcFetchDataMapper getMapper() {
        return emcFetchDataMapper;
    }
}
