package com.sunwayworld.emc.module.bp.project.projectqc.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.bp.project.projectqc.bean.EmcProjectQcLineBean;
import com.sunwayworld.emc.module.bp.project.projectqc.dao.EmcProjectQcLineDao;
import com.sunwayworld.emc.module.bp.project.projectqc.service.EmcProjectQcLineService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

/**
* @author zhengxy@sunwayworld.com 2020年09月21日
*/
@Repository
public class EmcProjectQcLineServiceImpl implements EmcProjectQcLineService {

    @Autowired
    private EmcProjectQcLineDao emcProjectQcLineDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcProjectQcLineDao getDao() {
        return emcProjectQcLineDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean wrapperBean) {
        EmcProjectQcLineBean emcProjectQcLineBean = wrapperBean.parseUnique(EmcProjectQcLineBean.class);
        Long id = ApplicationContextHelper.getNextIdentity();
        emcProjectQcLineBean.setId(id);
        getDao().insert(emcProjectQcLineBean);
        return id;
    }

}
