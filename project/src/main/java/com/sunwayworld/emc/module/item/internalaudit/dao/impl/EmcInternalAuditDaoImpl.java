package com.sunwayworld.emc.module.item.internalaudit.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.item.internalaudit.bean.EmcInternalAuditBean;
import com.sunwayworld.emc.module.item.internalaudit.dao.EmcInternalAuditDao;
import com.sunwayworld.emc.module.item.internalaudit.mapper.EmcInternalAuditMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;


/**
 * @author cuikx@sunwayworld.com
 * @date 2019-11-12
 */
@Repository
@GikamBean
public class EmcInternalAuditDaoImpl extends MybatisDaoSupport<EmcInternalAuditBean, Long> implements EmcInternalAuditDao {
    
    @Autowired
    private EmcInternalAuditMapper internalAuditMapper;
    
    @Override
    public EmcInternalAuditMapper getMapper() {
        return internalAuditMapper;
    }
    
}
