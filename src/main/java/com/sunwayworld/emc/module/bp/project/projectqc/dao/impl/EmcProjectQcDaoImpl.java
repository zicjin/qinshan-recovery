package com.sunwayworld.emc.module.bp.project.projectqc.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.project.projectqc.bean.EmcProjectQcBean;
import com.sunwayworld.emc.module.bp.project.projectqc.dao.EmcProjectQcDao;
import com.sunwayworld.emc.module.bp.project.projectqc.mapper.EmcProjectQcMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;

/**
* @author zhengxy@sunwayworld.com 2020年09月21日
*/
@Repository
public class EmcProjectQcDaoImpl extends MybatisDaoSupport<EmcProjectQcBean, Long> implements EmcProjectQcDao {

    @Autowired
    private EmcProjectQcMapper emcProjectQcMapper;

    @Override
    public EmcProjectQcMapper getMapper() {
        return emcProjectQcMapper;
    }
}
