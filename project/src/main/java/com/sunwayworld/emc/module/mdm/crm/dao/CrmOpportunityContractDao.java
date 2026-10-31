package com.sunwayworld.emc.module.mdm.crm.dao;

import java.util.List;
import java.util.Map;

import com.sunwayworld.emc.module.mdm.crm.bean.CrmOpportunityContractBean;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.support.base.dao.GenericDao;

/**
* @author zhengxy@sunwayworld.com 2021年08月26日
*/
public interface CrmOpportunityContractDao extends GenericDao<CrmOpportunityContractBean, String> {

    List<Map<String, Object>> selectFileByCondition(MapperParameter parameter);
}
