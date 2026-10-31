package com.sunwayworld.emc.module.mdm.basemdm.revisionrule.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.emc.module.mdm.basemdm.revisionrule.bean.EmcRevisionRuleBean;
import com.sunwayworld.emc.module.mdm.basemdm.revisionrule.service.EmcRevisionRuleService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.dao.GenericDao;
import com.sunwayworld.framework.utils.NumberUtils;

@Repository
public class EmcRevisionRuleServiceImpl implements EmcRevisionRuleService {

    public static final List<EmcRevisionRuleBean> RULELIST = new ArrayList<EmcRevisionRuleBean>();

    static {
        EmcRevisionRuleBean ruleISO = new EmcRevisionRuleBean();
        EmcRevisionRuleBean ruleEPA = new EmcRevisionRuleBean();
        EmcRevisionRuleBean ruleFDA = new EmcRevisionRuleBean();
        EmcRevisionRuleBean ruleEMC = new EmcRevisionRuleBean();
        EmcRevisionRuleBean ruleNONE = new EmcRevisionRuleBean();
        ruleISO.setId(EmcConstant.REVISION_RULE_ISO);
        ruleEPA.setId(EmcConstant.REVISION_RULE_EPA);
        ruleFDA.setId(EmcConstant.REVISION_RULE_FDA);
        ruleEMC.setId(EmcConstant.REVISION_RULE_EMC);
        ruleNONE.setId(EmcConstant.REVISION_RULE_NONE);
        ruleISO.setRuleDesc(EmcConstant.REVISION_RULE_ISO_DESC);
        ruleEPA.setRuleDesc(EmcConstant.REVISION_RULE_EPA_DESC);
        ruleFDA.setRuleDesc(EmcConstant.REVISION_RULE_FDA_DESC);
        ruleEMC.setRuleDesc(EmcConstant.REVISION_RULE_EMC_DESC);
        RULELIST.add(ruleISO);
        RULELIST.add(ruleEPA);
        RULELIST.add(ruleFDA);
        RULELIST.add(ruleEMC);
        RULELIST.add(ruleNONE);
    }

    @Override
    public <D extends GenericDao<EmcRevisionRuleBean, String>> D getDao() {
        return null;
    }

    @Override
    public Page<EmcRevisionRuleBean> getRevisionRuleList() {
        return new Pagination<>(RULELIST);
    }

    @Override
    public String selectRuledValue(String value, RestJsonWrapperBean jsonWrapper) {
        EmcRevisionRuleBean rule = jsonWrapper.parseUnique(EmcRevisionRuleBean.class);
        return NumberUtils.rounding(value, rule.getId() + "," + rule.getEffectiveDigit());
    }

    @Override
    public String getRuledValue(String value, String rule) {
        return NumberUtils.rounding(value, rule);
    }

}
