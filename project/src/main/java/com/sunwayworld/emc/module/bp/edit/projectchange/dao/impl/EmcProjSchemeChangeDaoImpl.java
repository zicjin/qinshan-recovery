package com.sunwayworld.emc.module.bp.edit.projectchange.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.edit.projectchange.bean.EmcProjSchemeChangeBean;
import com.sunwayworld.emc.module.bp.edit.projectchange.dao.EmcProjSchemeChangeDao;
import com.sunwayworld.emc.module.bp.edit.projectchange.mapper.EmcProjSchemeChangeMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;

/**
 * @author yuanhh@sunwayworld.com
 * @date 2019-10-08
 */
@Repository
public class EmcProjSchemeChangeDaoImpl extends MybatisDaoSupport<EmcProjSchemeChangeBean, Long> implements EmcProjSchemeChangeDao {

    @Autowired
    private EmcProjSchemeChangeMapper emcProjSchemeMapper;

    @Override
    public EmcProjSchemeChangeMapper getMapper() {
        return emcProjSchemeMapper;
    }

}
