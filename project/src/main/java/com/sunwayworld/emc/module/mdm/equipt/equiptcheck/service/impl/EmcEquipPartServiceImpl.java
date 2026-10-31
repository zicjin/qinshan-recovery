package com.sunwayworld.emc.module.mdm.equipt.equiptcheck.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.equipt.equiptcheck.bean.EmcEquipPartBean;
import com.sunwayworld.emc.module.mdm.equipt.equiptcheck.dao.EmcEquipPartDao;
import com.sunwayworld.emc.module.mdm.equipt.equiptcheck.service.EmcEquipPartService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Repository
@GikamBean
public class EmcEquipPartServiceImpl implements EmcEquipPartService {

    @Autowired
    private EmcEquipPartDao emcEquipPartDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcEquipPartDao getDao() {
        return emcEquipPartDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        EmcEquipPartBean emcEquipPart = jsonWrapper.parseUnique(EmcEquipPartBean.class);
        emcEquipPart.setId(ApplicationContextHelper.getNextIdentity());
        getDao().insert(emcEquipPart);
        return emcEquipPart.getId();
    }

    @Override
    public Page<EmcEquipPartBean> selectEquiptPartPaginationById(Long id, RestJsonWrapperBean wrapper) {
        return selectPaginationByFilter(SearchFilter.instance().match("equiptId", id).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insertEquiptPart(Long id, RestJsonWrapperBean wrapper) {
        EmcEquipPartBean emcEquipPartBean = wrapper.parseUnique(EmcEquipPartBean.class);
        emcEquipPartBean.setId(ApplicationContextHelper.getNextIdentity());
        emcEquipPartBean.setEquiptId(id);
        getDao().insert(emcEquipPartBean);
        return emcEquipPartBean.getId();
    }
}
