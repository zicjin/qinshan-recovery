package com.sunwayworld.emc.module.mdm.purchapply.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.purchapply.bean.EmcPurchApplyBean;
import com.sunwayworld.emc.module.mdm.purchapply.dao.EmcPurchApplyDao;
import com.sunwayworld.emc.module.mdm.purchapply.service.EmcPurchApplyService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author zhanghy@sunwayworld.com 2020-09-03
 */
@Repository
@GikamBean
public class EmcPurchApplyServiceImpl implements EmcPurchApplyService {

	@Autowired
	private EmcPurchApplyDao emcPurchApplyDao;
	
	@SuppressWarnings("unchecked")
	@Override
	public EmcPurchApplyDao getDao() {
		return emcPurchApplyDao;
	}
	
    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean wrapper) {
    	EmcPurchApplyBean emcPurchApply = wrapper.parseUnique(EmcPurchApplyBean.class);
    	emcPurchApply.setId(ApplicationContextHelper.getNextIdentity());
        getDao().insert(emcPurchApply);

        return emcPurchApply.getId();
    }
}
