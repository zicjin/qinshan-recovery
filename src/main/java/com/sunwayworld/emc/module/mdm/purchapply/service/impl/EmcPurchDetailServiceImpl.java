package com.sunwayworld.emc.module.mdm.purchapply.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.purchapply.bean.EmcPurchDetailBean;
import com.sunwayworld.emc.module.mdm.purchapply.dao.EmcPurchDetailDao;
import com.sunwayworld.emc.module.mdm.purchapply.service.EmcPurchDetailService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author zhanghy@sunwayworld.com 2020-09-03
 */
@GikamBean
@Repository
public class EmcPurchDetailServiceImpl implements EmcPurchDetailService {

	@Autowired
	private EmcPurchDetailDao emcPurchDetailDao;
	
    @Override
    @SuppressWarnings("unchecked")
    public EmcPurchDetailDao getDao() {
    	return emcPurchDetailDao;
    }

	@Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
	public Long insertPurchDetail(Long id, RestJsonWrapperBean wrapper) {
		EmcPurchDetailBean dinganPurchDetail = wrapper.parseUnique(EmcPurchDetailBean.class);
		dinganPurchDetail.setId(ApplicationContextHelper.getNextIdentity());
		dinganPurchDetail.setPurchId(id);
		getDao().insert(dinganPurchDetail);
		
		return dinganPurchDetail.getId();
	}

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.DELETE)
	public void deletePurchDetail(RestJsonWrapperBean wrapper) {
		this.delete(wrapper);
	}
}
