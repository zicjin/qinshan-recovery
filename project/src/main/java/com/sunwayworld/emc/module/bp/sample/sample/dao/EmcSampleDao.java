package com.sunwayworld.emc.module.bp.sample.sample.dao;

import java.util.List;
import java.util.Map;

import com.sunwayworld.emc.module.bp.sample.sample.bean.EmcSampleBean;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.support.base.dao.GenericDao;

/**
 * @author zhangsy@sunwayworld.com
 * @date 2019-10-12
 */

public interface EmcSampleDao extends GenericDao<EmcSampleBean, Long> {
    
    List<Map<String, Object>> selectSamplePagination(MapperParameter parameter);
    
    List<Map<String, Object>> selectMobileScanner(MapperParameter parameter);
    
    List<Map<String, Object>> selectDetailLabel(MapperParameter parameter);
    
    List<Map<String, Object>> selectLabelDetail(MapperParameter parameter);

    List<Map<String, Object>> selectTodoSampleList(MapperParameter parameter);

    List<Map<String, Object>> selectTodoSampleOrderReceiptList(MapperParameter parameter);

    List<Map<String, Object>> selectAssignSampleList(MapperParameter parameter);
}
