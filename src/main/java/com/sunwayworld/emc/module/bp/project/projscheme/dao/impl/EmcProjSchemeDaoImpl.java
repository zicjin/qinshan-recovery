package com.sunwayworld.emc.module.bp.project.projscheme.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.project.projscheme.bean.EmcProjSchemeBean;
import com.sunwayworld.emc.module.bp.project.projscheme.dao.EmcProjSchemeDao;
import com.sunwayworld.emc.module.bp.project.projscheme.mapper.EmcProjSchemeMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;

/**
 * @author yuanhh@sunwayworld.com
 * @date 2019-10-08
 */
@Repository
public class EmcProjSchemeDaoImpl extends MybatisDaoSupport<EmcProjSchemeBean, Long> implements EmcProjSchemeDao {

    @Autowired
    private EmcProjSchemeMapper emcProjSchemeMapper;

    @Override
    public EmcProjSchemeMapper getMapper() {
        return emcProjSchemeMapper;
    }

}
