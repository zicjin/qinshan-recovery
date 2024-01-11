package com.sunwayworld.emc.module.bp.sol.service.impl;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.bp.sol.bean.EmcSolLineBean;
import com.sunwayworld.emc.module.bp.sol.dao.EmcSolLineDao;
import com.sunwayworld.emc.module.bp.sol.service.EmcSolLineService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

/**
* @author zhengxy@sunwayworld.com 2020年08月30日
*/
@Repository
@GikamBean
public class EmcSolLineServiceImpl implements EmcSolLineService {

    @Autowired
    private EmcSolLineDao emcSolLineDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcSolLineDao getDao() {
        return emcSolLineDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean wrapperBean) {
        EmcSolLineBean emcSolLineBean = wrapperBean.parseUnique(EmcSolLineBean.class);
        Long id = ApplicationContextHelper.getNextIdentity();
        emcSolLineBean.setId(id);
        getDao().insert(emcSolLineBean);
        return id;
    }

}
