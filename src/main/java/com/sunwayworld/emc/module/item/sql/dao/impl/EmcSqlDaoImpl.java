package com.sunwayworld.emc.module.item.sql.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.item.sql.bean.EmcSqlBean;
import com.sunwayworld.emc.module.item.sql.dao.EmcSqlDao;
import com.sunwayworld.emc.module.item.sql.mapper.EmcSqlMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Repository
@GikamBean
public class EmcSqlDaoImpl extends MybatisDaoSupport<EmcSqlBean, Long> implements EmcSqlDao {

    @Autowired
    private EmcSqlMapper emcSqlMapper;

    @Override
    public EmcSqlMapper getMapper() {
        return emcSqlMapper;
    }

}
