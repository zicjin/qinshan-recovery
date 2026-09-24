package com.sunwayworld.emc.module.bp.sample.sample.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.sample.sample.bean.EmcSampleBean;
import com.sunwayworld.emc.module.bp.sample.sample.dao.EmcSampleDao;
import com.sunwayworld.emc.module.bp.sample.sample.mapper.EmcSampleMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;


/**
 * @author zhangsy@sunwayworld.com
 * @date 2019-10-12
 */
@Repository
public class EmcSampleDaoImpl extends MybatisDaoSupport<EmcSampleBean, Long>implements EmcSampleDao {
    
    @Autowired
    private EmcSampleMapper emcSampleMapper;
    
    @Override
    public EmcSampleMapper getMapper() {
        return emcSampleMapper;
    }

    @Override
    public List<Map<String, Object>> selectSamplePagination(MapperParameter parameter) {
        return getMapper().selectSamplePagination(parameter);
    }
    
    @Override
    public List<Map<String, Object>> selectMobileScanner(MapperParameter parameter) {
        return getMapper().selectMobileScanner(parameter);
    }
    
    @Override
    public List<Map<String, Object>> selectDetailLabel(MapperParameter parameter) {
        return getMapper().selectDetailLabel(parameter);
    }
    
    @Override
    public List<Map<String, Object>> selectLabelDetail(MapperParameter parameter) {
        return getMapper().selectLabelDetail(parameter);
    }

    @Override
    public List<Map<String, Object>> selectTodoSampleList(MapperParameter parameter) {
        return getMapper().selectTodoSampleList(parameter);
    }

    @Override
    public List<Map<String, Object>> selectTodoSampleOrderReceiptList(MapperParameter parameter) {
        return getMapper().selectTodoSampleOrderReceiptList(parameter);
    }

    @Override
    public List<Map<String, Object>> selectAssignSampleList(MapperParameter parameter) { return getMapper().selectAssignSampleList(parameter); }
    
}
