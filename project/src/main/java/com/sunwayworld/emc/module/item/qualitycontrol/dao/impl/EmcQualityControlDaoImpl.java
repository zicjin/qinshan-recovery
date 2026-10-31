package com.sunwayworld.emc.module.item.qualitycontrol.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.item.qualitycontrol.bean.EmcQualityControlBean;
import com.sunwayworld.emc.module.item.qualitycontrol.dao.EmcQualityControlDao;
import com.sunwayworld.emc.module.item.qualitycontrol.mapper.EmcQualityControlMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;

/**
 * @author magt@sunwayworld.com
 * @date 2019-12-04
 */
@Repository
public class EmcQualityControlDaoImpl extends MybatisDaoSupport<EmcQualityControlBean, Long> implements EmcQualityControlDao {
    @Autowired
    private EmcQualityControlMapper emcQualityControlMapper;

    @Override
    public EmcQualityControlMapper getMapper() {
        return emcQualityControlMapper;
    }
}
