package com.sunwayworld.emc.module.mdm.basemdm.subcontractor.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.basemdm.subcontractor.bean.EmcSubcontractRecordBean;
import com.sunwayworld.emc.module.mdm.basemdm.subcontractor.bean.EmcSubcontractorBean;
import com.sunwayworld.emc.module.mdm.basemdm.subcontractor.dao.EmcSubcontractorDao;
import com.sunwayworld.emc.module.mdm.basemdm.subcontractor.service.EmcSubcontractRecordService;
import com.sunwayworld.emc.module.mdm.basemdm.subcontractor.service.EmcSubcontractorService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author magh@sunwayworld.com
 * @date 2019-11-05
 */
@GikamBean
@Repository
public class EmcSubcontractorServiceImpl implements EmcSubcontractorService {
    @Autowired
    private EmcSubcontractorDao emcSubcontractorDao;

    @Autowired
    private EmcSubcontractRecordService subcontractRecordService;

    @Override
    @SuppressWarnings("unchecked")
    public EmcSubcontractorDao getDao() {
        return emcSubcontractorDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        EmcSubcontractorBean emcSubcontractor = jsonWrapper.parseUnique(EmcSubcontractorBean.class);
        emcSubcontractor.setId(ApplicationContextHelper.getNextIdentity());
        getDao().insert(emcSubcontractor);
        return emcSubcontractor.getId();
    }

    @Override
    public Page<EmcSubcontractRecordBean> selectRecordPaginationBySubcontractorId(Long id, RestJsonWrapperBean wrapper) {
        return subcontractRecordService.selectPaginationByFilter(SearchFilter.instance().match("SUBCONTRACTORID", id).filter(MatchPattern.EQ), wrapper);
    }

}
