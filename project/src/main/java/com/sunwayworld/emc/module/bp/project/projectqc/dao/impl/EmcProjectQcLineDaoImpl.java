package com.sunwayworld.emc.module.bp.project.projectqc.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.project.projectqc.bean.EmcProjectQcLineBean;
import com.sunwayworld.emc.module.bp.project.projectqc.dao.EmcProjectQcLineDao;
import com.sunwayworld.emc.module.bp.project.projectqc.mapper.EmcProjectQcLineMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;

/**
* @author zhengxy@sunwayworld.com 2020年09月21日
*/
@Repository
public class EmcProjectQcLineDaoImpl extends MybatisDaoSupport<EmcProjectQcLineBean, Long> implements EmcProjectQcLineDao {

    @Autowired
    private EmcProjectQcLineMapper emcProjectQcLineMapper;

    @Override
    public EmcProjectQcLineMapper getMapper() {
        return emcProjectQcLineMapper;
    }
}
