package com.sunwayworld.emc.module.bp.subpackage.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.subpackage.bean.EmcSubpackageTaskBean;
import com.sunwayworld.emc.module.bp.subpackage.dao.EmcSubpackageTaskDao;
import com.sunwayworld.emc.module.bp.subpackage.mapper.EmcSubpackageTaskMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;

/**
* @author zhengxy@sunwayworld.com 2022年09月05日
*/
@Repository
public class EmcSubpackageTaskDaoImpl extends MybatisDaoSupport<EmcSubpackageTaskBean, Long> implements EmcSubpackageTaskDao {

    @Autowired
    private EmcSubpackageTaskMapper emcSubpackageTaskMapper;

    @Override
    public EmcSubpackageTaskMapper getMapper() {
        return emcSubpackageTaskMapper;
    }
}
