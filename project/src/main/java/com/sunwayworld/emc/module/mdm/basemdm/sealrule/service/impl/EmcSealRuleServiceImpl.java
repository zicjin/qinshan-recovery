package com.sunwayworld.emc.module.mdm.basemdm.sealrule.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.spring.annotation.GikamBean;

import com.sunwayworld.emc.module.mdm.basemdm.sealrule.dao.EmcSealRuleDao;
import com.sunwayworld.emc.module.mdm.basemdm.sealrule.bean.EmcSealRuleBean;
import com.sunwayworld.emc.module.mdm.basemdm.sealrule.service.EmcSealRuleService;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.exception.checked.CheckedException;
import java.util.List;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.emc.module.mdm.basemdm.sealrule.bean.EmcSealRuleDetailsBean;
import com.sunwayworld.emc.module.mdm.basemdm.sealrule.service.EmcSealRuleDetailsService;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

@Repository
@GikamBean
public class EmcSealRuleServiceImpl implements EmcSealRuleService {

    @Autowired
    private EmcSealRuleDao emcSealRuleDao;

    @Autowired
    private EmcSealRuleDetailsService emcSealRuleDetailsService;

    @Override
    @SuppressWarnings("unchecked")
    public EmcSealRuleDao getDao() {
        return emcSealRuleDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        EmcSealRuleBean emcSealRule = jsonWrapper.parseUnique(EmcSealRuleBean.class);
        emcSealRule.setId(ApplicationContextHelper.getNextIdentity());
        getDao().insert(emcSealRule);
        return emcSealRule.getId();
    }

    @Override
    public Page<EmcSealRuleDetailsBean> selectRuleDetailsPaginationBySealRuleId(Long id, RestJsonWrapperBean wrapper) {
        return emcSealRuleDetailsService.selectPaginationByFilter(SearchFilter.instance().match("SEALRULEID", id).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insertRuleDetails(Long id, RestJsonWrapperBean wrapper) {

        List<EmcSealRuleDetailsBean> emcSealRuleDetailsList = wrapper.parse(EmcSealRuleDetailsBean.class);

        emcSealRuleDetailsList.forEach(e -> {
            e.setId(ApplicationContextHelper.getNextIdentity());
            e.setSealRuleId(id);
        });

        emcSealRuleDetailsService.getDao().insert(emcSealRuleDetailsList);

        return emcSealRuleDetailsList.get(0).getId();
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.DELETE)
    public void deleteRuleDetails(Long id, RestJsonWrapperBean wrapper) {
        if (id == null) {
            throw new CheckedException("GIKAM.EXCEPTION.PARAM_ERROR");
        }
        for (EmcSealRuleDetailsBean selected : wrapper.parse(EmcSealRuleDetailsBean.class)) {
            if (selected.getId() == null) {
                throw new CheckedException("GIKAM.EXCEPTION.PARAM_ERROR");
            }
            EmcSealRuleDetailsBean stored = emcSealRuleDetailsService.selectByIdIfPresent(selected.getId());
            if (stored != null && !id.equals(stored.getSealRuleId())) {
                throw new CheckedException("GIKAM.EXCEPTION.PARAM_ERROR");
            }
        }
        emcSealRuleDetailsService.delete(wrapper);
    }

}
