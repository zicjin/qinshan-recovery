package com.sunwayworld.emc.module.mdm.contract.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.contract.bean.EmcReviewBasicInfoBean;
import com.sunwayworld.emc.module.mdm.contract.dao.EmcReviewBasicInfoDao;
import com.sunwayworld.emc.module.mdm.contract.service.EmcReviewBasicInfoService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

/**
* @author zhengxy@sunwayworld.com 2020年11月01日
*/
@Repository
public class EmcReviewBasicInfoServiceImpl implements EmcReviewBasicInfoService {

    @Autowired
    private EmcReviewBasicInfoDao emcReviewBasicInfoDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcReviewBasicInfoDao getDao() {
        return emcReviewBasicInfoDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean wrapperBean) {
        EmcReviewBasicInfoBean emcReviewBasicInfoBean = wrapperBean.parseUnique(EmcReviewBasicInfoBean.class);
        Long id = ApplicationContextHelper.getNextIdentity();
        emcReviewBasicInfoBean.setId(id);
        getDao().insert(emcReviewBasicInfoBean);
        return id;
    }

}
