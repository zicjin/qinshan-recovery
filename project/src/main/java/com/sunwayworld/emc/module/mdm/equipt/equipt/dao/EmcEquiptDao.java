package com.sunwayworld.emc.module.mdm.equipt.equipt.dao;

import java.util.List;
import java.util.Map;

import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptBean;
import com.sunwayworld.framework.support.base.dao.GenericDao;

public interface EmcEquiptDao extends GenericDao<EmcEquiptBean, Long>{

    List<EmcEquiptBean> selectByConditionByTestMethodIdList(List<String> testMethodIds);

    List<Map<String, Object>> selectCaliChoose();

    List<Map<String, Object>> selectVerifyChoose();

    List<Map<String, Object>> selectPerCheckChoose();

    List<EmcEquiptBean> selectMaxEquiptIdByEquiptCategoryId(long equiptCategoryId);
}
