package com.sunwayworld.emc.module.mdm.purchapply.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.purchapply.bean.EmcPurchDetailBean;
import com.sunwayworld.emc.module.mdm.purchapply.dao.EmcPurchDetailDao;
import com.sunwayworld.emc.module.mdm.purchapply.mapper.EmcPurchDetailMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author zhanghy@sunwayworld.com 2020-09-03
 */
@Repository
@GikamBean
public class EmcPurchDetailDaoImpl extends MybatisDaoSupport<EmcPurchDetailBean, Long> implements EmcPurchDetailDao {

	@Autowired
	private EmcPurchDetailMapper emcPurchDetailMapper;
	
	@Override
	public EmcPurchDetailMapper getMapper() {
		return emcPurchDetailMapper;
	}
}
