package com.sunwayworld.emc.module.bp.archive.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.bp.archive.bean.EmcArchiveCategoryBean;
import com.sunwayworld.emc.module.bp.archive.dao.EmcArchiveCategoryDao;
import com.sunwayworld.emc.module.bp.archive.service.EmcArchiveCategoryService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

/**
* @author zhengxy@sunwayworld.com 2020年10月31日
*/
@Repository
public class EmcArchiveCategoryServiceImpl implements EmcArchiveCategoryService {

    @Autowired
    private EmcArchiveCategoryDao emcArchiveCategoryDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcArchiveCategoryDao getDao() {
        return emcArchiveCategoryDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean wrapperBean) {
        EmcArchiveCategoryBean emcArchiveCategoryBean = wrapperBean.parseUnique(EmcArchiveCategoryBean.class);
        Long id = ApplicationContextHelper.getNextIdentity();
        emcArchiveCategoryBean.setId(id);
        getDao().insert(emcArchiveCategoryBean);
        return id;
    }

}
