package com.sunwayworld.emc.module.mdm.basemdm.subcontractor.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.basemdm.subcontractor.bean.EmcSubcontractRecordBean;
import com.sunwayworld.emc.module.mdm.basemdm.subcontractor.dao.EmcSubcontractRecordDao;
import com.sunwayworld.emc.module.mdm.basemdm.subcontractor.mapper.EmcSubcontractRecordMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;


/**
 * @author magh@sunwayworld.com
 * @date 2019-11-05
 */
@Repository
public class EmcSubcontractRecordDaoImpl extends MybatisDaoSupport<EmcSubcontractRecordBean, Long>implements EmcSubcontractRecordDao {
    
    @Autowired
    private EmcSubcontractRecordMapper emcScItemMapper;
    
    @Override
    public EmcSubcontractRecordMapper getMapper() {
        return emcScItemMapper;
    }
    
}
