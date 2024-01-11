package com.sunwayworld.emc.module.bp.project.project.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.project.project.bean.EmcProjectDirectorBean;
import com.sunwayworld.emc.module.bp.project.project.dao.EmcProjectDirectorDao;
import com.sunwayworld.emc.module.bp.project.project.mapper.EmcProjectDirectorMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Repository
@GikamBean
public class EmcProjectDirectorDaoImpl extends MybatisDaoSupport<EmcProjectDirectorBean, Long> implements EmcProjectDirectorDao {

    @Autowired
    private EmcProjectDirectorMapper emcProjectDirectorMapper;

    @Override
    public EmcProjectDirectorMapper getMapper() {
        return emcProjectDirectorMapper;
    }

}
