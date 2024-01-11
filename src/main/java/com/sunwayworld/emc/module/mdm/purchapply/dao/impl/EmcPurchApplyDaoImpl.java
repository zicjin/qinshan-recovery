package com.sunwayworld.emc.module.mdm.purchapply.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.purchapply.bean.EmcPurchApplyBean;
import com.sunwayworld.emc.module.mdm.purchapply.dao.EmcPurchApplyDao;
import com.sunwayworld.emc.module.mdm.purchapply.mapper.EmcPurchApplyMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;

/**
 * @author zhanghy@sunwayworld.com 2020-09-03
 */
@Repository
public class EmcPurchApplyDaoImpl extends MybatisDaoSupport<EmcPurchApplyBean, Long> implements EmcPurchApplyDao {

	@Autowired
	private EmcPurchApplyMapper emcPurchApplyMapper;
	
	@Override
	public EmcPurchApplyMapper getMapper() {
		return emcPurchApplyMapper;
	}
}
