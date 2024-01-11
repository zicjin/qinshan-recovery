package com.sunwayworld.emc.module.bp.sol.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.sol.bean.EmcSolLineBean;
import com.sunwayworld.emc.module.bp.sol.dao.EmcSolLineDao;
import com.sunwayworld.emc.module.bp.sol.mapper.EmcSolLineMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;

/**
* @author zhengxy@sunwayworld.com 2020年08月30日
*/
@Repository
public class EmcSolLineDaoImpl extends MybatisDaoSupport<EmcSolLineBean, Long> implements EmcSolLineDao {

    @Autowired
    private EmcSolLineMapper emcSolLineMapper;

    @Override
    public EmcSolLineMapper getMapper() {
        return emcSolLineMapper;
    }
}
