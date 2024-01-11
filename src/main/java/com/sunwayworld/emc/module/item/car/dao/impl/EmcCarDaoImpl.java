package com.sunwayworld.emc.module.item.car.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.item.car.bean.EmcCarBean;
import com.sunwayworld.emc.module.item.car.dao.EmcCarDao;
import com.sunwayworld.emc.module.item.car.mapper.EmcCarMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;

/**
 * @author cuikx@sunwayworld.com
 * @date 2019-11-12
 */
@Repository
public class EmcCarDaoImpl extends MybatisDaoSupport<EmcCarBean, Long> implements EmcCarDao {

    @Autowired
    private EmcCarMapper emcCarMapper;

    @Override
    public EmcCarMapper getMapper() {
        return emcCarMapper;
    }

}
