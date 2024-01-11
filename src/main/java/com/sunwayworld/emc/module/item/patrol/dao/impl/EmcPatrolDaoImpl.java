package com.sunwayworld.emc.module.item.patrol.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.item.patrol.bean.EmcPatrolBean;
import com.sunwayworld.emc.module.item.patrol.dao.EmcPatrolDao;
import com.sunwayworld.emc.module.item.patrol.mapper.EmcPatrolMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;

/**
 * @author cuikx@sunwayworld.com
 * @date 2019-11-09
 */
@Repository
public class EmcPatrolDaoImpl extends MybatisDaoSupport<EmcPatrolBean, Long> implements EmcPatrolDao {

    @Autowired
    private EmcPatrolMapper emcPatrolMapper;

    @Override
    public EmcPatrolMapper getMapper() {
        return emcPatrolMapper;
    }

}
