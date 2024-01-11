package com.sunwayworld.emc.module.bp.sol.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.sol.bean.EmcSolBean;
import com.sunwayworld.emc.module.bp.sol.dao.EmcSolDao;
import com.sunwayworld.emc.module.bp.sol.mapper.EmcSolMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@GikamBean
@Repository
public class EmcSolDaoImpl extends MybatisDaoSupport<EmcSolBean, Long>implements EmcSolDao {

    @Autowired
    private EmcSolMapper solMapper;

    @Override
    public EmcSolMapper getMapper() {
        return solMapper;
    }
    
}
