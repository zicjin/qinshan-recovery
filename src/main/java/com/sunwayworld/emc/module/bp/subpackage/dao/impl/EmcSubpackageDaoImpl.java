package com.sunwayworld.emc.module.bp.subpackage.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.subpackage.bean.EmcSubpackageBean;
import com.sunwayworld.emc.module.bp.subpackage.dao.EmcSubpackageDao;
import com.sunwayworld.emc.module.bp.subpackage.mapper.EmcSubpackageMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;

/**
* @author zhengxy@sunwayworld.com 2022年09月05日
*/
@Repository
public class EmcSubpackageDaoImpl extends MybatisDaoSupport<EmcSubpackageBean, Long> implements EmcSubpackageDao {

    @Autowired
    private EmcSubpackageMapper emcSubpackageMapper;

    @Override
    public EmcSubpackageMapper getMapper() {
        return emcSubpackageMapper;
    }
}
