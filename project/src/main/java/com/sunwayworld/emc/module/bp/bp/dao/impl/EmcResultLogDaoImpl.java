package com.sunwayworld.emc.module.bp.bp.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.bp.bean.EmcResultLogBean;
import com.sunwayworld.emc.module.bp.bp.dao.EmcResultLogDao;
import com.sunwayworld.emc.module.bp.bp.mapper.EmcResultLogMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;

/**
 * @author magh@sunwayworld.com
 * @date 2019-11-21
 */
@Repository
public class EmcResultLogDaoImpl extends MybatisDaoSupport<EmcResultLogBean, Long> implements EmcResultLogDao {

    @Autowired
    private EmcResultLogMapper emcResultLogMapper;

    @Override
    public EmcResultLogMapper getMapper() {
        return emcResultLogMapper;
    }

}
