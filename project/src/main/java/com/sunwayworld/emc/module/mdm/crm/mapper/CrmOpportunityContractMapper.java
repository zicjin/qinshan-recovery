package com.sunwayworld.emc.module.mdm.crm.mapper;

import java.util.List;
import java.util.Map;

import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.support.base.mapper.GenericMapper;

/**
* @author zhengxy@sunwayworld.com 2021年08月26日
*/
public interface CrmOpportunityContractMapper extends GenericMapper<String> {

    List<Map<String, Object>> selectFileByCondition(MapperParameter parameter);
}
