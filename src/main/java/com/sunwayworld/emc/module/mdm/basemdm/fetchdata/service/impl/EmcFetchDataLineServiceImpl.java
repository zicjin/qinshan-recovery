package com.sunwayworld.emc.module.mdm.basemdm.fetchdata.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.basemdm.fetchdata.bean.EmcFetchDataLineBean;
import com.sunwayworld.emc.module.mdm.basemdm.fetchdata.dao.EmcFetchDataLineDao;
import com.sunwayworld.emc.module.mdm.basemdm.fetchdata.service.EmcFetchDataLineService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

/**
 * @author zhengxy@sunwayworld.com 2020年03月05日
 */
@Repository
public class EmcFetchDataLineServiceImpl implements EmcFetchDataLineService {

    @Autowired
    private EmcFetchDataLineDao emcFetchDataLineDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcFetchDataLineDao getDao() {
        return emcFetchDataLineDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean wrapperBean) {
        EmcFetchDataLineBean emcFetchDataLineBean = wrapperBean.parseUnique(EmcFetchDataLineBean.class);
        Long id = ApplicationContextHelper.getNextIdentity();
        emcFetchDataLineBean.setId(id);
        getDao().insert(emcFetchDataLineBean);
        return id;
    }

}
