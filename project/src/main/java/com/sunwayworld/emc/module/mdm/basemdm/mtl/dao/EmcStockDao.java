package com.sunwayworld.emc.module.mdm.basemdm.mtl.dao;

import java.util.List;
import java.util.Map;

import com.sunwayworld.emc.module.mdm.basemdm.mtl.bean.EmcStockBean;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.support.base.dao.GenericDao;
/**
 * @author baogx@sunwayworld.com
 *
 * @date 2019年9月20日 下午3:24:51
 */
public interface EmcStockDao extends GenericDao<EmcStockBean, Long> {
    

    List<Map<String, Object>> selectStockLowLine(MapperParameter parameter);

    List<Map<String, Object>> selectStockOverdue(MapperParameter parameter);


    List<Map<String, Object>> selectQcStdStockPagination(MapperParameter parameter);
}
