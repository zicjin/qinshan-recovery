package com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.mapper;

import com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.bean.EmcIndexSetBean;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.mapper.GenericMapper;

import java.util.List;

@GikamBean
public interface EmcIndexSetMapper extends GenericMapper<Long> {
    List<EmcIndexSetBean> selectListByIndexIdAndItemId(MapperParameter parameter);
}
