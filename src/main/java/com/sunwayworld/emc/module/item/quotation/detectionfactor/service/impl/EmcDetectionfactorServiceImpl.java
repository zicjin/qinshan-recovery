package com.sunwayworld.emc.module.item.quotation.detectionfactor.service.impl;

import com.sunwayworld.emc.module.mdm.bizmdm.method.bean.EmcMethodBean;
import com.sunwayworld.emc.module.mdm.bizmdm.method.bean.EmcMethodVersionBean;
import com.sunwayworld.emc.module.mdm.bizmdm.method.service.EmcMethodService;
import com.sunwayworld.emc.module.mdm.bizmdm.method.service.EmcMethodVersionService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcItemGroupBean;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.spring.annotation.GikamBean;

import com.sunwayworld.emc.module.item.quotation.detectionfactor.dao.EmcDetectionfactorDao;
import com.sunwayworld.emc.module.item.quotation.detectionfactor.bean.EmcDetectionfactorBean;
import com.sunwayworld.emc.module.item.quotation.detectionfactor.service.EmcDetectionfactorService;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@GikamBean
public class EmcDetectionfactorServiceImpl implements EmcDetectionfactorService {

    @Autowired
    private EmcDetectionfactorDao emcDetectionfactorDao;

    @Autowired
    private EmcMethodVersionService methodVersionService;

    @Autowired
    private EmcMethodService methodService;

    @Override
    @SuppressWarnings("unchecked")
    public EmcDetectionfactorDao getDao() {
        return emcDetectionfactorDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        EmcDetectionfactorBean insertBean = jsonWrapper.parseUnique(EmcDetectionfactorBean.class);
        insertBean.setId(ApplicationContextHelper.getNextIdentity());
        insertBean.setOrgId(LocalContextHelper.getLoginUser().getExt$Item("parentOrgId"));
        getDao().insert(insertBean);
        return insertBean.getId();
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insertByItemGroup(RestJsonWrapperBean wrapper) {
        List<EmcItemGroupBean> emcItemGroupBeanList = wrapper.parse(EmcItemGroupBean.class);
        if (!CollectionUtils.isEmpty(emcItemGroupBeanList)) {
            List<EmcDetectionfactorBean> insertList = emcItemGroupBeanList.stream().map(e -> {
                EmcDetectionfactorBean insertBean = new EmcDetectionfactorBean();
                insertBean.setId(ApplicationContextHelper.getNextIdentity());
                insertBean.setTestId(e.getTestId());
                if (NumberUtils.isNumber(e.getExt$Item("methodId"))) {
                    insertBean.setMethodId(NumberUtils.parseLong(e.getExt$Item("methodId")));
                }
                insertBean.setItemGroupId(e.getId());
                insertBean.setTestName(e.getExt$Item("testname"));
                insertBean.setMethodName(e.getExt$Item("methodname"));
                insertBean.setItemGroupName(e.getItemGroup());
                insertBean.setOrgId(LocalContextHelper.getLoginUser().getExt$Item("parentOrgId"));
                if (NumberUtils.isNumber(e.getExt$Item("testcategoryid"))) {
                    insertBean.setTestCategoryId(NumberUtils.parseLong(e.getExt$Item("testcategoryid")));
                }
                insertBean.setAnalysisPrice("0");
                insertBean.setSamplePrice("0");
                return insertBean;
            }).collect(Collectors.toList());
            getDao().insert(insertList);
        }
        return 1L;
    }

}
