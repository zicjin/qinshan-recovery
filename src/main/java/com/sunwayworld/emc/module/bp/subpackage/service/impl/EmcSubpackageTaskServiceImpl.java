package com.sunwayworld.emc.module.bp.subpackage.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.bp.subpackage.bean.EmcSubpackageTaskBean;
import com.sunwayworld.emc.module.bp.subpackage.dao.EmcSubpackageTaskDao;
import com.sunwayworld.emc.module.bp.subpackage.service.EmcSubpackageTaskService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

/**
* @author zhengxy@sunwayworld.com 2022年09月05日
*/
@Repository
public class EmcSubpackageTaskServiceImpl implements EmcSubpackageTaskService {

    @Autowired
    private EmcSubpackageTaskDao emcSubpackageTaskDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcSubpackageTaskDao getDao() {
        return emcSubpackageTaskDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean wrapperBean) {
        EmcSubpackageTaskBean emcSubpackageTaskBean = wrapperBean.parseUnique(EmcSubpackageTaskBean.class);
        Long id = ApplicationContextHelper.getNextIdentity();
        emcSubpackageTaskBean.setId(id);
        getDao().insert(emcSubpackageTaskBean);
        return id;
    }

}
