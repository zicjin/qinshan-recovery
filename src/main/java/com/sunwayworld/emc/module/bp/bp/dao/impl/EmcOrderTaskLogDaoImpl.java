package com.sunwayworld.emc.module.bp.bp.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskLogBean;
import com.sunwayworld.emc.module.bp.bp.dao.EmcOrderTaskLogDao;
import com.sunwayworld.emc.module.bp.bp.mapper.EmcOrderTaskLogMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;

/**
 * @author magh@sunwayworld.com
 * @date 2019-11-22
 */
@Repository
public class EmcOrderTaskLogDaoImpl extends MybatisDaoSupport<EmcOrderTaskLogBean, Long> implements EmcOrderTaskLogDao {

    @Autowired
    private EmcOrderTaskLogMapper emcOrderTaskLogMapper;

    @Override
    public EmcOrderTaskLogMapper getMapper() {
        return emcOrderTaskLogMapper;
    }

}
