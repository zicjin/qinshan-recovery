/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.rounding.service.impl;

import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.module.sys.rounding.bean.CoreRoundingRuleBean;
import com.sunwayworld.module.sys.rounding.bean.CoreRoundingRuleDTO;
import com.sunwayworld.module.sys.rounding.dao.CoreRoundingRuleDao;
import com.sunwayworld.module.sys.rounding.service.CoreRoundingRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class CoreRoundingRuleServiceImpl
implements CoreRoundingRuleService {
    @Autowired
    private CoreRoundingRuleDao roundingRuleDao;

    @Override
    public CoreRoundingRuleDao getDao() {
        return this.roundingRuleDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public String insert(RestJsonWrapperBean wrapper) {
        CoreRoundingRuleBean roundingRule = wrapper.parseUnique(CoreRoundingRuleBean.class);
        this.getDao().insert(roundingRule);
        return roundingRule.getId();
    }

    @Override
    public CoreRoundingRuleDTO calculate(RestJsonWrapperBean wrapper) {
        CoreRoundingRuleDTO coreRoundingRuleDTO = wrapper.parseUnique(CoreRoundingRuleDTO.class);
        String result = NumberUtils.rounding(coreRoundingRuleDTO.getOriginalResult(), coreRoundingRuleDTO.getParam());
        coreRoundingRuleDTO.setRevisionResult(result);
        return coreRoundingRuleDTO;
    }
}
