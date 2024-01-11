package com.sunwayworld.emc.module.mdm.equipt.equipt.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptUseRecordBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.dao.EmcEquiptUseRecordDao;
import com.sunwayworld.emc.module.mdm.equipt.equipt.mapper.EmcEquiptUseRecordMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;

@Repository
public class EmcEquiptUseRecordDaoImpl extends MybatisDaoSupport<EmcEquiptUseRecordBean, Long>implements EmcEquiptUseRecordDao {

    @Autowired
    private EmcEquiptUseRecordMapper equiptUseRecordMapper;

    @Override
    public EmcEquiptUseRecordMapper getMapper() {
        return equiptUseRecordMapper;
    }

}
