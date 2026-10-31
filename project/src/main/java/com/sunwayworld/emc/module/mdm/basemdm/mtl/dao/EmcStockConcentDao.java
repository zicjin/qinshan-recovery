package com.sunwayworld.emc.module.mdm.basemdm.mtl.dao;

import java.util.List;
import java.util.Map;

import com.sunwayworld.emc.module.mdm.basemdm.mtl.bean.EmcStockConcentBean;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.support.base.dao.GenericDao;

/**
 * @author baogx@sunwayworld.com
 *
 * @date 2019年9月21日 下午3:36:47
 */
public interface EmcStockConcentDao extends GenericDao<EmcStockConcentBean, Long> {

    List<Map<String, Object>> selectLimitByCondition(MapperParameter parameter);

}
