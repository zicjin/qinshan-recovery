package com.sunwayworld.emc.module.mdm.equipt.equiptcheck.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.equipt.equiptcheck.bean.EmcEquipDataBean;
import com.sunwayworld.emc.module.mdm.equipt.equiptcheck.dao.EmcEquipDataDao;
import com.sunwayworld.emc.module.mdm.equipt.equiptcheck.service.EmcEquipDataService;
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
public class EmcEquipDataServiceImpl implements EmcEquipDataService {

    @Autowired
    private EmcEquipDataDao emcEquipDataDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcEquipDataDao getDao() {
        return emcEquipDataDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        EmcEquipDataBean emcEquipData = jsonWrapper.parseUnique(EmcEquipDataBean.class);
        emcEquipData.setId(ApplicationContextHelper.getNextIdentity());
        getDao().insert(emcEquipData);
        return emcEquipData.getId();
    }

    @Override
    public Page<EmcEquipDataBean> selectEquiptDataPaginationById(Long id, RestJsonWrapperBean wrapper) {
        return selectPaginationByFilter(SearchFilter.instance().match("equiptId", id).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insertEquiptData(Long id, RestJsonWrapperBean wrapper) {
        EmcEquipDataBean emcEquipDataBean = wrapper.parseUnique(EmcEquipDataBean.class);
        emcEquipDataBean.setId(ApplicationContextHelper.getNextIdentity());
        emcEquipDataBean.setEquiptId(id);
        getDao().insert(emcEquipDataBean);
        return emcEquipDataBean.getId();
    }
}
