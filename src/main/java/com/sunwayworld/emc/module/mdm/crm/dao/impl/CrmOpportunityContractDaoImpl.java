package com.sunwayworld.emc.module.mdm.crm.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.crm.bean.CrmOpportunityContractBean;
import com.sunwayworld.emc.module.mdm.crm.dao.CrmOpportunityContractDao;
import com.sunwayworld.emc.module.mdm.crm.mapper.CrmOpportunityContractMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;

/**
* @author zhengxy@sunwayworld.com 2021年08月26日
*/
@Repository
public class CrmOpportunityContractDaoImpl extends MybatisDaoSupport<CrmOpportunityContractBean, String> implements CrmOpportunityContractDao {

    @Autowired
    private CrmOpportunityContractMapper crmOpportunityContractMapper;

    @Override
    public CrmOpportunityContractMapper getMapper() {
        return crmOpportunityContractMapper;
    }

    @Override
    public List<Map<String, Object>> selectFileByCondition(MapperParameter parameter) {
        return getMapper().selectFileByCondition(parameter);
    }
}
