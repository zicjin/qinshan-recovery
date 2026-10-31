package com.sunwayworld.emc.module.bp.edit.projectchange.mapper;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.mapper.GenericMapper;

/**
 * @author zhangsy@sunwayworld.com
 * @date 2019-10-12
 */
@Repository
@GikamBean
public interface EmcOrderChangeMapper extends GenericMapper<Long> {

    List<Map<String, Object>> selectOrderList(MapperParameter parameter);
}
