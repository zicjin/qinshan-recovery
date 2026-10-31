package com.sunwayworld.emc.module.mdm.basemdm.vendor.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.basemdm.vendor.bean.EmcVendorBean;
import com.sunwayworld.emc.module.mdm.basemdm.vendor.bean.EmcVendorMtlBean;
import com.sunwayworld.emc.module.mdm.basemdm.vendor.dao.EmcVendorDao;
import com.sunwayworld.emc.module.mdm.basemdm.vendor.service.EmcVendorMtlService;
import com.sunwayworld.emc.module.mdm.basemdm.vendor.service.EmcVendorService;
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
public class EmcVendorServiceImpl implements EmcVendorService {
    @Autowired
    private EmcVendorDao emcVendorDao;

    @Autowired
    private EmcVendorMtlService emcVendorMtlService;

    @Override
    @SuppressWarnings("unchecked")
    public EmcVendorDao getDao() {
        return emcVendorDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        EmcVendorBean emcVendor = jsonWrapper.parseUnique(EmcVendorBean.class);
        emcVendor.setId(ApplicationContextHelper.getNextIdentity());
        getDao().insert(emcVendor);
        return emcVendor.getId();
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insertMtl(Long id, RestJsonWrapperBean wrapper) {
        EmcVendorMtlBean emcVendorMtl = wrapper.parseUnique(EmcVendorMtlBean.class);
        emcVendorMtl.setId(ApplicationContextHelper.getNextIdentity());
        emcVendorMtl.setVendorId(id);
        emcVendorMtlService.getDao().insert(emcVendorMtl);
        return emcVendorMtl.getId();
    }

    @Override
    public Page<EmcVendorMtlBean> selectMtlPaginationById(Long id, RestJsonWrapperBean wrapper) {
        return emcVendorMtlService.selectPaginationByFilter(SearchFilter.instance().match("VENDORID", id).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.DELETE)
    public void deleteMtl(RestJsonWrapperBean wrapper) {
        emcVendorMtlService.delete(wrapper);

    }

}
