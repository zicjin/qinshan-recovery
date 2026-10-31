package com.sunwayworld.emc.module.mdm.basemdm.sealrule.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.basemdm.sealrule.bean.EmcSealRuleBean;
import com.sunwayworld.emc.module.mdm.basemdm.sealrule.dao.EmcSealRuleDao;
import com.sunwayworld.emc.module.mdm.basemdm.sealrule.mapper.EmcSealRuleMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Repository
@GikamBean
public class EmcSealRuleDaoImpl extends MybatisDaoSupport<EmcSealRuleBean, Long> implements EmcSealRuleDao {

    @Autowired
    private EmcSealRuleMapper emcSealRuleMapper;

    @Override
    public EmcSealRuleMapper getMapper() {
        return emcSealRuleMapper;
    }

}
