package com.sunwayworld.emc.module.mdm.equipt.equipt.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptRecordBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.dao.EmcEquiptRecordDao;
import com.sunwayworld.emc.module.mdm.equipt.equipt.mapper.EmcEquiptRecordMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;


/**
 * @author cuikx@sunwayworld.com
 * @date 2019-11-21
 */
@Repository
public class EmcEquiptRecordDaoImpl extends MybatisDaoSupport<EmcEquiptRecordBean, Long>implements EmcEquiptRecordDao {
    
    @Autowired
    private EmcEquiptRecordMapper emcEquiptRecordMapper;
    
    @Override
    public EmcEquiptRecordMapper getMapper() {
        return emcEquiptRecordMapper;
    }
    
}
