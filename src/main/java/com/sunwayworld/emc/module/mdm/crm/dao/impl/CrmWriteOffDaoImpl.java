package com.sunwayworld.emc.module.mdm.crm.dao.impl;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.sunwayworld.emc.module.mdm.crm.bean.CrmWriteOffBean;
import com.sunwayworld.emc.module.mdm.crm.dao.CrmWriteOffDao;
import com.sunwayworld.emc.module.mdm.crm.mapper.CrmWriteOffMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;

/**
* @author licm@sunwayworld.com 2022年02月18日
*/
@Repository
public class CrmWriteOffDaoImpl extends MybatisDaoSupport<CrmWriteOffBean, String> implements CrmWriteOffDao {

    @Autowired
    private CrmWriteOffMapper crmWriteOffMapper;

    @Override
    public CrmWriteOffMapper getMapper() {
        return crmWriteOffMapper;
    }


}
