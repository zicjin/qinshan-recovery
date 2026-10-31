package com.sunwayworld.emc.module.mdm.equipt.equiptacqresult.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.sunwayworld.framework.support.base.mapper.GenericMapper;

public interface EmcEquiptAcqResultMapper extends GenericMapper<Long> {

    List<Map<String, Object>> selectByConditionByBizNoList(@Param("bizNoList") List<String> bizNoList, @Param("testId") String testId);

    List<Map<String, Object>> selectByConditionForAcqAutoUpdate(@Param("idList") List<Long> idList);

}
