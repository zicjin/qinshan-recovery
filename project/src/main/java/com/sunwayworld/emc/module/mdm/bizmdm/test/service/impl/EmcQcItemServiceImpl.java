package com.sunwayworld.emc.module.mdm.bizmdm.test.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcQcItemBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.dao.EmcQcItemDao;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcQcItemService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

/**
 * 
 * @author changsp@sunwayworld.com
 * @date 2021-07-16
 */

@Repository
public class EmcQcItemServiceImpl implements EmcQcItemService {

    @Autowired
    private EmcQcItemDao emcQcItemDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcQcItemDao getDao() {
        return emcQcItemDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        List<EmcQcItemBean> qcItemList = jsonWrapper.parse(EmcQcItemBean.class);
        qcItemList.forEach(e -> {
            e.setId(ApplicationContextHelper.getNextIdentity());
        });
        getDao().insert(qcItemList);
        return qcItemList.get(0).getId();
    }

}
