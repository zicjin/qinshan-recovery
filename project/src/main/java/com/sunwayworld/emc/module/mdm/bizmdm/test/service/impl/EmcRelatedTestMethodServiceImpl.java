package com.sunwayworld.emc.module.mdm.bizmdm.test.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcRelatedTestMethodBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.dao.EmcRelatedTestMethodDao;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcRelatedTestMethodService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

/**
* @author zhengxy@sunwayworld.com 2021年08月26日
*/
@Repository
public class EmcRelatedTestMethodServiceImpl implements EmcRelatedTestMethodService {

    @Autowired
    private EmcRelatedTestMethodDao emcRelatedTestMethodDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcRelatedTestMethodDao getDao() {
        return emcRelatedTestMethodDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean wrapperBean) {
        EmcRelatedTestMethodBean emcRelatedTestMethodBean = wrapperBean.parseUnique(EmcRelatedTestMethodBean.class);
        Long id = ApplicationContextHelper.getNextIdentity();
        emcRelatedTestMethodBean.setId(id);
        getDao().insert(emcRelatedTestMethodBean);
        return id;
    }

    @Override
    public void insert(Long testMethodId, RestJsonWrapperBean wrapper) {
        List<EmcRelatedTestMethodBean> relatedTestMethodList = wrapper.parse(EmcRelatedTestMethodBean.class);
        relatedTestMethodList.forEach(method -> method.setId(ApplicationContextHelper.getNextIdentity()));
        getDao().insert(relatedTestMethodList);
    }
}
