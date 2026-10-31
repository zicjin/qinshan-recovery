package com.sunwayworld.emc.module.mdm.basemdm.mtl.mapper;

import java.util.List;
import java.util.Map;

import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.mapper.GenericMapper;
/**
 * @author baogx@sunwayworld.com
 *O
 * @date 2019年9月20日 下午3:17:26
 */
@GikamBean
public interface EmcStockMapper extends GenericMapper<Long> {

    List<Map<String, Object>> selectStockLowLine(MapperParameter parameter);
    
    List<Map<String, Object>> selectStockOverdue(MapperParameter parameter);

    List<Map<String, Object>> selectQcStdStockPagination(MapperParameter parameter);
}
