package com.sunwayworld.emc.module.bp.sol.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.sol.bean.EmcSolRecordBean;
import com.sunwayworld.emc.module.bp.sol.dao.EmcSolRecordDao;
import com.sunwayworld.emc.module.bp.sol.mapper.EmcSolRecordMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;

@Repository
public class EmcSolRecordDaoImpl extends MybatisDaoSupport<EmcSolRecordBean, Long>implements EmcSolRecordDao {

    @Autowired
    private EmcSolRecordMapper solRecordMapper;

    @Override
    public EmcSolRecordMapper getMapper() {
        return solRecordMapper;
    }

}
