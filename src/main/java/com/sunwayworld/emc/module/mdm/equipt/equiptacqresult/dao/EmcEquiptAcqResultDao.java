package com.sunwayworld.emc.module.mdm.equipt.equiptacqresult.dao;

import java.util.List;
import java.util.Map;

import com.sunwayworld.emc.module.mdm.equipt.equiptacqresult.bean.EmcEquiptAcqResultBean;
import com.sunwayworld.framework.support.base.dao.GenericDao;

public interface EmcEquiptAcqResultDao extends GenericDao<EmcEquiptAcqResultBean, Long> {

    List<Map<String, Object>> selectByConditionByBizNoList(List<String> bizNoList, String testId);

    List<Map<String, Object>> selectByConditionForAcqAutoUpdate(List<Long> idList);

}
