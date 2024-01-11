package com.sunwayworld.emc.module.item.inconformity.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.item.inconformity.bean.EmcInconformityBean;
import com.sunwayworld.emc.module.item.inconformity.dao.EmcInconformityDao;
import com.sunwayworld.emc.module.item.inconformity.mapper.EmcInconformityMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;


/**
 * @author cuikx@sunwayworld.com
 * @date 2019-12-6
 */
@Repository
@GikamBean
public class EmcInconformityDaoImpl extends MybatisDaoSupport<EmcInconformityBean, Long> implements EmcInconformityDao {
    
    @Autowired
    private EmcInconformityMapper inconformityMapper;
    
    @Override
    public EmcInconformityMapper getMapper() {
        return inconformityMapper;
    }
    
}
