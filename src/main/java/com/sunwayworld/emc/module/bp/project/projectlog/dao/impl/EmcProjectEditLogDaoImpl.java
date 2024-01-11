package com.sunwayworld.emc.module.bp.project.projectlog.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.project.projectlog.bean.EmcProjectEditLogBean;
import com.sunwayworld.emc.module.bp.project.projectlog.dao.EmcProjectEditLogDao;
import com.sunwayworld.emc.module.bp.project.projectlog.mapper.EmcProjectEditLogMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Repository
@GikamBean
public class EmcProjectEditLogDaoImpl extends MybatisDaoSupport<EmcProjectEditLogBean, Long> implements EmcProjectEditLogDao {

    @Autowired
    private EmcProjectEditLogMapper emcProjectEditLogMapper;

    @Override
    public EmcProjectEditLogMapper getMapper() {
        return emcProjectEditLogMapper;
    }

}
