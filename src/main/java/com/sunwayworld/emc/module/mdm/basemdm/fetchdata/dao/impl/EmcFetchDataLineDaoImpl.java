package com.sunwayworld.emc.module.mdm.basemdm.fetchdata.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.basemdm.fetchdata.bean.EmcFetchDataLineBean;
import com.sunwayworld.emc.module.mdm.basemdm.fetchdata.dao.EmcFetchDataLineDao;
import com.sunwayworld.emc.module.mdm.basemdm.fetchdata.mapper.EmcFetchDataLineMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;

/**
 * @author zhengxy@sunwayworld.com 2020年03月05日
 */
@Repository
public class EmcFetchDataLineDaoImpl extends MybatisDaoSupport<EmcFetchDataLineBean, Long> implements EmcFetchDataLineDao {

    @Autowired
    private EmcFetchDataLineMapper emcFetchDataLineMapper;

    @Override
    public EmcFetchDataLineMapper getMapper() {
        return emcFetchDataLineMapper;
    }
}
