package com.sunwayworld.emc.module.mdm.basemdm.sealrule.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.spring.annotation.GikamBean;

import com.sunwayworld.emc.module.mdm.basemdm.sealrule.dao.EmcSealRuleDetailsDao;
import com.sunwayworld.emc.module.mdm.basemdm.sealrule.bean.EmcSealRuleDetailsBean;
import com.sunwayworld.emc.module.mdm.basemdm.sealrule.service.EmcSealRuleDetailsService;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.beans.BeanPropertyListener;
import com.sunwayworld.framework.exception.checked.CheckedException;
import java.util.Collections;
import java.util.List;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

@Repository
@GikamBean
public class EmcSealRuleDetailsServiceImpl implements EmcSealRuleDetailsService {

    @Autowired
    private EmcSealRuleDetailsDao emcSealRuleDetailsDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcSealRuleDetailsDao getDao() {
        return emcSealRuleDetailsDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        EmcSealRuleDetailsBean emcSealRuleDetails = jsonWrapper.parseUnique(EmcSealRuleDetailsBean.class);
        emcSealRuleDetails.setId(ApplicationContextHelper.getNextIdentity());
        getDao().insert(emcSealRuleDetails);
        return emcSealRuleDetails.getId();
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    @Audit("AUDIT.SAVE")
    public void updateIfChanged(EmcSealRuleDetailsBean item) {
        updateIfChanged(Collections.singletonList(item));
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    @Audit("AUDIT.SAVE")
    public void updateIfChanged(List<EmcSealRuleDetailsBean> items) {
        if (items == null || items.isEmpty()) {
            return;
        }
        for (EmcSealRuleDetailsBean item : items) {
            if (item.getId() == null) {
                throw new CheckedException("GIKAM.EXCEPTION.PARAM_ERROR");
            }
            EmcSealRuleDetailsBean stored = getDao().selectByIdIfPresent(item.getId());
            if (stored == null) {
                throw new CheckedException("GIKAM.EXCEPTION.PARAM_ERROR");
            }
            boolean parentProvided = item instanceof BeanPropertyListener
                    ? ((BeanPropertyListener) item).getChangedPropertyEventList().stream()
                        .anyMatch(event -> "sealRuleId".equalsIgnoreCase(event.getPropertyDescriptor().getName()))
                    : item.getSealRuleId() != null;
            if (parentProvided && (stored.getSealRuleId() == null || !stored.getSealRuleId().equals(item.getSealRuleId()))) {
                throw new CheckedException("GIKAM.EXCEPTION.PARAM_ERROR");
            }
        }
        EmcSealRuleDetailsService.super.updateIfChanged(items);
    }

}
