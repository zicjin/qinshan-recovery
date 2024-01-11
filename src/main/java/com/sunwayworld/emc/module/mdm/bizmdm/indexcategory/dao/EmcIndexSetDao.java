package com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.dao;

import com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.bean.EmcIndexSetBean;
import com.sunwayworld.framework.support.base.dao.GenericDao;

import java.util.List;

public interface EmcIndexSetDao extends GenericDao<EmcIndexSetBean, Long> {
    List<EmcIndexSetBean> selectListByIndexIdAndItemId(Long indexId, Long itemId);
}
