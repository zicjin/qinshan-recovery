package com.sunwayworld.emc.module.item.sql.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.item.sql.bean.EmcSqlBean;
import com.sunwayworld.emc.module.item.sql.dao.EmcSqlDao;
import com.sunwayworld.emc.module.item.sql.service.EmcSqlService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Repository
@GikamBean
public class EmcSqlServiceImpl implements EmcSqlService {

    @Autowired
    private EmcSqlDao emcSqlDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcSqlDao getDao() {
        return emcSqlDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        EmcSqlBean emcSql = jsonWrapper.parseUnique(EmcSqlBean.class);
        emcSql.setId(ApplicationContextHelper.getNextIdentity());
        getDao().insert(emcSql);
        return emcSql.getId();
    }

}
