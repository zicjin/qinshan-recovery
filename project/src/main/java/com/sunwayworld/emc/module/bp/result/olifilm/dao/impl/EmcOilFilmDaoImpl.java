package com.sunwayworld.emc.module.bp.result.olifilm.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.result.olifilm.bean.EmcOilFilmBean;
import com.sunwayworld.emc.module.bp.result.olifilm.dao.EmcOilFilmDao;
import com.sunwayworld.emc.module.bp.result.olifilm.mapper.EmcOilFilmMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Repository
@GikamBean
public class EmcOilFilmDaoImpl extends MybatisDaoSupport<EmcOilFilmBean, Long> implements EmcOilFilmDao {

    @Autowired
    private EmcOilFilmMapper emcOilFilmMapper;

    @Override
    public EmcOilFilmMapper getMapper() {
        return emcOilFilmMapper;
    }

}
