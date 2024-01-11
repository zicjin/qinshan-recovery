package com.sunwayworld.emc.module.mdm.equipt.equipt.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptPerCheckRecordBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.dao.EmcEquiptPerCheckRecordDao;
import com.sunwayworld.emc.module.mdm.equipt.equipt.mapper.EmcEquiptPerCheckRecordMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;

/**
 * @author liuyz@sunwayworld.com 2020年09月25日
 */
@Repository
public class EmcEquiptPerCheckRecordDaoImpl extends MybatisDaoSupport<EmcEquiptPerCheckRecordBean,Long> implements EmcEquiptPerCheckRecordDao {
    @Autowired
    private EmcEquiptPerCheckRecordMapper emcEquiptPerCheckRecordMapper;

    @Override
    public EmcEquiptPerCheckRecordMapper getMapper() {
        return emcEquiptPerCheckRecordMapper;
    }
}
