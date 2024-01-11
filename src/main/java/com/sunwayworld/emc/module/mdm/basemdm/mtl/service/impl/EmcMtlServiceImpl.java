package com.sunwayworld.emc.module.mdm.basemdm.mtl.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.basemdm.mtl.bean.EmcMtlBean;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.bean.EmcMtlMaintainBean;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.dao.EmcMtlDao;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.service.EmcMtlMaintainService;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.service.EmcMtlService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;

/**
 * @author baogx@sunwayworld.com
 *
 * @date 2019年9月19日 下午6:20:19
 */
@Repository
@GikamBean
public class EmcMtlServiceImpl implements EmcMtlService {

    @Autowired
    private EmcMtlDao mtlDao;
    
    @Autowired
    @Lazy
    private EmcMtlMaintainService mtlMaintainService;
    
    @SuppressWarnings("unchecked")
    @Override
    public EmcMtlDao getDao() {
        return mtlDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT) 
    public String insert(RestJsonWrapperBean wrapper) {
        
        EmcMtlBean emcMtl = wrapper.parseUnique(EmcMtlBean.class);
        emcMtl.setId(ApplicationContextHelper.getNextSequence(getDao().getTable()));
        //赋值所属单位信息
        CoreUserBean user = new CoreUserBean();
        user = LocalContextHelper.getLoginUser();
        String parentOrgId = user.getExt$Item("parentorgid");
        emcMtl.setOrgId(parentOrgId);
        
        getDao().insert(emcMtl);
        
        //添加物质维护
        EmcMtlMaintainBean emcMtlMaintain = new EmcMtlMaintainBean();
        emcMtlMaintain.setCategory(emcMtl.getMtlName());
        emcMtlMaintain.setMtlName(emcMtl.getMtlName());
        emcMtlMaintain.setMtlId(emcMtl.getId());
        emcMtlMaintain.setId(ApplicationContextHelper.getNextIdentity());
        mtlMaintainService.getDao().insert(emcMtlMaintain);
        
        return emcMtl.getId();
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE) 
    public void updateStockQty(String id) {
        getDao().updateStockQty(id);
    }
}
