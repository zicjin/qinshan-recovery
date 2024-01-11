package com.sunwayworld.emc.module.mdm.basemdm.sealrule.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.basemdm.sealrule.bean.EmcSealRuleDetailsBean;
import com.sunwayworld.emc.module.mdm.basemdm.sealrule.dao.EmcSealRuleDetailsDao;
import com.sunwayworld.emc.module.mdm.basemdm.sealrule.mapper.EmcSealRuleDetailsMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Repository
@GikamBean
public class EmcSealRuleDetailsDaoImpl extends MybatisDaoSupport<EmcSealRuleDetailsBean, Long> implements EmcSealRuleDetailsDao {

    @Autowired
    private EmcSealRuleDetailsMapper emcSealRuleDetailsMapper;

    @Override
    public EmcSealRuleDetailsMapper getMapper() {
        return emcSealRuleDetailsMapper;
    }

}
