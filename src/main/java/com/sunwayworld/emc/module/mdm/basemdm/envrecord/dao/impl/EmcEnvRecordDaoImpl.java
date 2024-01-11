package com.sunwayworld.emc.module.mdm.basemdm.envrecord.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.basemdm.envrecord.bean.EmcEnvRecordBean;
import com.sunwayworld.emc.module.mdm.basemdm.envrecord.dao.EmcEnvRecordDao;
import com.sunwayworld.emc.module.mdm.basemdm.envrecord.mapper.EmcEnvRecordMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;

@Repository
public class EmcEnvRecordDaoImpl extends MybatisDaoSupport<EmcEnvRecordBean, String> implements EmcEnvRecordDao {

    @Autowired
    private EmcEnvRecordMapper envRecordMapper;

    @Override
    public EmcEnvRecordMapper getMapper() {
        return envRecordMapper;
    }

}
