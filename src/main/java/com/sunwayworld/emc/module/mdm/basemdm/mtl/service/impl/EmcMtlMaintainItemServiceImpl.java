package com.sunwayworld.emc.module.mdm.basemdm.mtl.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.basemdm.mtl.bean.EmcMtlMaintainItemBean;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.dao.EmcMtlMaintainItemDao;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.service.EmcMtlMaintainItemService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

/**
 * @author baogx@sunwayworld.com
 *
 * @date 2019年9月21日 下午3:44:17
 */
@Repository
public class EmcMtlMaintainItemServiceImpl implements EmcMtlMaintainItemService {

    @Autowired
    private EmcMtlMaintainItemDao emcMtlMaintainItemDao;
    
    @SuppressWarnings("unchecked")
    @Override
    public EmcMtlMaintainItemDao getDao() {
        return emcMtlMaintainItemDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT) 
    public Long insert(Long maintainId, RestJsonWrapperBean wrapper) {
        List<EmcMtlMaintainItemBean> insertMaintainItemList = wrapper.parse(EmcMtlMaintainItemBean.class);
        insertMaintainItemList.forEach(item -> item.setId(ApplicationContextHelper.getNextIdentity()));
        getDao().insert(insertMaintainItemList);
        return maintainId;
    }
}
