package com.sunwayworld.emc.module.mdm.filemodification.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.filemodification.bean.EmcFileModificationBean;
import com.sunwayworld.emc.module.mdm.filemodification.dao.EmcFileModificationDao;
import com.sunwayworld.emc.module.mdm.filemodification.mapper.EmcFileModificationMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;

/**
 * @author zhanghy@sunwayworld.com 2020-09-01
 */
@Repository
public class EmcFileModificationDaoImpl extends MybatisDaoSupport<EmcFileModificationBean, Long> implements EmcFileModificationDao{

	@Autowired
	private EmcFileModificationMapper emcFileModificationMapper;
	
	@Override
	public EmcFileModificationMapper getMapper() {
		return emcFileModificationMapper;
	}
}
