package com.sunwayworld.emc.module.mdm.basemdm.cert.service.impl;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.basemdm.cert.bean.EmcCertBean;
import com.sunwayworld.emc.module.mdm.basemdm.cert.bean.EmcCertTestMethodBean;
import com.sunwayworld.emc.module.mdm.basemdm.cert.dao.EmcCertTestMethodDao;
import com.sunwayworld.emc.module.mdm.basemdm.cert.service.EmcCertTestMethodService;
import com.sunwayworld.emc.module.mdm.bizmdm.capacity.bean.EmcTestCapacityBean;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.utils.NumberUtils;

/**
 * @author zhangsy@sunwayworld.com
 * @date 2019-10-08
 */

@Repository
@GikamBean
public class EmcCertTestMethodServiceImpl implements EmcCertTestMethodService {
    @Autowired
    private EmcCertTestMethodDao emcCertTestMethodDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcCertTestMethodDao getDao() {
        return emcCertTestMethodDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void insert(Long id, RestJsonWrapperBean wrapper) {
        List<EmcTestCapacityBean> testMethodList = wrapper.parse(EmcTestCapacityBean.class);

        List<EmcCertTestMethodBean> certTestMethodList = new ArrayList<EmcCertTestMethodBean>();
        testMethodList.stream().forEach(e -> {
            EmcCertTestMethodBean emcCertTestMethodBean = new EmcCertTestMethodBean();
            emcCertTestMethodBean.setId(ApplicationContextHelper.getNextIdentity());
            emcCertTestMethodBean.setCertId(id);
            emcCertTestMethodBean.setTestMethodId(e.getTestMethodId());
            emcCertTestMethodBean.setMethodId(NumberUtils.parseLong(e.getExt$Item("methodid")));
            emcCertTestMethodBean.setTestId(NumberUtils.parseLong(e.getExt$Item("testid")));
            certTestMethodList.add(emcCertTestMethodBean);
        });

        getDao().insert(certTestMethodList);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void copyCertTestMethod(RestJsonWrapperBean wrapper) {
        List<EmcCertBean> certList = wrapper.parse(EmcCertBean.class);
        List<EmcCertTestMethodBean> certTestMethodList = wrapper.parse(EmcCertTestMethodBean.class);

        List<EmcCertTestMethodBean> certTestMethodInsertList = new ArrayList<EmcCertTestMethodBean>();
        certList.stream().forEach(cert -> {
            certTestMethodList.stream().forEach(e -> {
                EmcCertTestMethodBean certTestMethod = new EmcCertTestMethodBean();
                certTestMethod.setCertId(cert.getId());
                certTestMethod.setTestId(e.getTestId());
                certTestMethod.setMethodId(e.getMethodId());
                certTestMethodInsertList.add(certTestMethod);
            });
        });
        // 去除已存在的方法，避免证书下方法重复添加
        List<EmcCertTestMethodBean> certTestMethodFilterList = getDao().selectList(certTestMethodInsertList, Arrays.asList("CERTID", "TESTID", "METHODID"), Collections.emptyList());
        certTestMethodFilterList.forEach(filter -> {
            certTestMethodInsertList.removeIf(e -> {
                return filter.getCertId().equals(e.getCertId()) && filter.getTestId().equals(e.getTestId()) && filter.getMethodId().equals(e.getMethodId());
            });
        });

        if (!certTestMethodInsertList.isEmpty()) {
            // 方法添加id
            certTestMethodInsertList.forEach(e -> e.setId(ApplicationContextHelper.getNextIdentity()));
            getDao().insert(certTestMethodInsertList);
        }
    }
}
