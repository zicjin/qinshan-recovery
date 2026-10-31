package com.sunwayworld.emc.module.mdm.crm.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.crm.bean.CrmFolderBean;
import com.sunwayworld.emc.module.mdm.crm.dao.CrmFolderDao;
import com.sunwayworld.emc.module.mdm.crm.mapper.CrmFolderMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Repository
@GikamBean
public class CrmFolderDaoImpl extends MybatisDaoSupport<CrmFolderBean, String> implements CrmFolderDao {

    @Autowired
    private CrmFolderMapper crmFolderMapper;

    @Override
    public CrmFolderMapper getMapper() {
        return crmFolderMapper;
    }

}
