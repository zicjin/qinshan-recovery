package com.sunwayworld.emc.module.item.mtlapply.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.item.mtlapply.bean.EmcMtlApplyBean;
import com.sunwayworld.emc.module.item.mtlapply.dao.EmcMtlApplyDao;
import com.sunwayworld.emc.module.item.mtlapply.mapper.EmcMtlApplyMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;

/**
 * @author zhengxy@sunwayworld.com 2020年02月24日
 */
@Repository
public class EmcMtlApplyDaoImpl extends MybatisDaoSupport<EmcMtlApplyBean, Long> implements EmcMtlApplyDao {

    @Autowired
    private EmcMtlApplyMapper emcMtlApplyMapper;

    @Override
    public EmcMtlApplyMapper getMapper() {
        return emcMtlApplyMapper;
    }
}
