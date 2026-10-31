package com.sunwayworld.emc.module.mdm.basemdm.mtl.mapper;

import java.util.List;
import java.util.Map;

import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.mapper.GenericMapper;

/**
 * @author baogx@sunwayworld.com
 *
 * @date 2019年9月21日 下午3:26:45
 */
@GikamBean
public interface EmcStockConcentMapper extends GenericMapper<Long> {

    List<Map<String, Object>> selectLimitByCondition(MapperParameter parameter);

}
