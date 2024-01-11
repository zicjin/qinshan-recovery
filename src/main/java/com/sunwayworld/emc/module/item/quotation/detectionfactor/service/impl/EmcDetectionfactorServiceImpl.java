package com.sunwayworld.emc.module.item.quotation.detectionfactor.service.impl;

import com.sunwayworld.emc.module.mdm.bizmdm.method.bean.EmcMethodBean;
import com.sunwayworld.emc.module.mdm.bizmdm.method.bean.EmcMethodVersionBean;
import com.sunwayworld.emc.module.mdm.bizmdm.method.service.EmcMethodService;
import com.sunwayworld.emc.module.mdm.bizmdm.method.service.EmcMethodVersionService;
import com.sunwayworld.framework.utils.CollectionUtils;
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
        List<EmcDetectionfactorBean> emcDetectionfactors = jsonWrapper.parse(EmcDetectionfactorBean.class);
        List<EmcDetectionfactorBean> allDetectionList = getDao().selectAll();
        List<EmcDetectionfactorBean> insertDetectionList = new ArrayList<>();
        List<EmcMethodVersionBean> allMethodVersionList = methodVersionService.selectAll();
        List<EmcMethodBean> allMethodList = methodService.selectAll();
        emcDetectionfactors.forEach(e -> {
            if(allDetectionList.stream().noneMatch(m -> ObjectUtils.equals(m.getMonitorTmplGroupId(),e.getMonitorTmplGroupId())
                    && ObjectUtils.equals(m.getTestId(),e.getTestId()) && ObjectUtils.equals(m.getMethodId(),e.getMethodId())
                    && ObjectUtils.equals(m.getItemGroupName(),e.getItemGroupName()))){
                List<EmcMethodVersionBean> methodVersion = allMethodVersionList.stream().filter(m -> ObjectUtils.equals(m.getMethodId(),e.getMethodId())).collect(Collectors.toList());
                if(!ObjectUtils.isEmpty(e.getMethodName())){
                    EmcMethodBean method = allMethodList.stream().filter(m -> ObjectUtils.equals(m.getMethodName(),e.getMethodName())).findFirst().orElse(null);
                    if(!ObjectUtils.isEmpty(method)){
                        e.setMethodId(method.getId());
                    }
                }
                if(!CollectionUtils.isEmpty(methodVersion)){
                    e.setTrialScope(methodVersion.get(0).getTrialScope());
                }
                e.setId(ApplicationContextHelper.getNextIdentity());
                if(ObjectUtils.isEmpty(e.getAnalysisPrice())){
                    e.setAnalysisPrice("0");
                }
                if(ObjectUtils.isEmpty(e.getSamplePrice())){
                    e.setSamplePrice("0");
                }
                insertDetectionList.add(e);
            }

        });
        getDao().insert(insertDetectionList);
        return emcDetectionfactors.get(0).getId();
    }

}
