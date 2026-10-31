package com.sunwayworld.emc.module.bp.sample.sample.mapper;

import java.util.List;
import java.util.Map;

import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.mapper.GenericMapper;

/**
 * @author zhangsy@sunwayworld.com
 * @date 2019-10-12
 */
@GikamBean
public interface EmcSampleMapper extends GenericMapper<Long> {

    List<Map<String, Object>> selectSamplePagination(MapperParameter parameter);
    
    List<Map<String, Object>> selectMobileScanner(MapperParameter parameter);
    
    List<Map<String, Object>> selectDetailLabel(MapperParameter parameter);
    
    List<Map<String, Object>> selectLabelDetail(MapperParameter parameter);

    List<Map<String, Object>> selectTodoSampleList(MapperParameter parameter);

    List<Map<String, Object>> selectTodoSampleOrderReceiptList(MapperParameter parameter);

    List<Map<String, Object>> selectAssignSampleList(MapperParameter parameter);
}
