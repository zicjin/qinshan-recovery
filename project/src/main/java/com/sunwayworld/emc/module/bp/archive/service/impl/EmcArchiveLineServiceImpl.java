package com.sunwayworld.emc.module.bp.archive.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.bp.archive.bean.EmcArchiveLineBean;
import com.sunwayworld.emc.module.bp.archive.dao.EmcArchiveLineDao;
import com.sunwayworld.emc.module.bp.archive.service.EmcArchiveLineService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

/**
* @author zhengxy@sunwayworld.com 2020年10月31日
*/
@Repository
public class EmcArchiveLineServiceImpl implements EmcArchiveLineService {

    @Autowired
    private EmcArchiveLineDao emcArchiveLineDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcArchiveLineDao getDao() {
        return emcArchiveLineDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean wrapperBean) {
        EmcArchiveLineBean emcArchiveLineBean = wrapperBean.parseUnique(EmcArchiveLineBean.class);
        Long id = ApplicationContextHelper.getNextIdentity();
        emcArchiveLineBean.setId(id);
        getDao().insert(emcArchiveLineBean);
        return id;
    }

}
