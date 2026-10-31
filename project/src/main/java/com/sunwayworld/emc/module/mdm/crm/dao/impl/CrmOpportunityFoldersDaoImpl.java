package com.sunwayworld.emc.module.mdm.crm.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.crm.bean.CrmOpportunityFoldersBean;
import com.sunwayworld.emc.module.mdm.crm.dao.CrmOpportunityFoldersDao;
import com.sunwayworld.emc.module.mdm.crm.mapper.CrmOpportunityFoldersMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;

/**
* @author zhengxy@sunwayworld.com 2021年08月26日
*/
@Repository
public class CrmOpportunityFoldersDaoImpl extends MybatisDaoSupport<CrmOpportunityFoldersBean, String> implements CrmOpportunityFoldersDao {

    @Autowired
    private CrmOpportunityFoldersMapper crmOpportunityFoldersMapper;

    @Override
    public CrmOpportunityFoldersMapper getMapper() {
        return crmOpportunityFoldersMapper;
    }
}
