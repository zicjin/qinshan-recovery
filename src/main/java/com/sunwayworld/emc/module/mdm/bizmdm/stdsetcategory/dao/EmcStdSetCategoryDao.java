package com.sunwayworld.emc.module.mdm.bizmdm.stdsetcategory.dao;

import java.util.List;
import java.util.Map;

import com.sunwayworld.emc.module.mdm.bizmdm.stdsetcategory.bean.EmcStdSetCategoryBean;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.support.base.dao.GenericDao;

public interface EmcStdSetCategoryDao extends GenericDao<EmcStdSetCategoryBean, Long> {

    List<Map<String, Object>> selectFieldInfoPagination(MapperParameter mapperParameter);
}
