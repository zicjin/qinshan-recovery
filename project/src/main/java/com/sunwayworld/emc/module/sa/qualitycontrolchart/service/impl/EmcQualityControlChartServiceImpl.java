package com.sunwayworld.emc.module.sa.qualitycontrolchart.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskResultBean;
import com.sunwayworld.emc.module.sa.qualitycontrolchart.dao.EmcQualityControlChartDao;
import com.sunwayworld.emc.module.sa.qualitycontrolchart.service.EmcQualityControlChartService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.mybatis.page.PageRowBounds;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.StringUtils;

@Repository
@GikamBean
public class EmcQualityControlChartServiceImpl implements EmcQualityControlChartService {
    @Autowired
    private EmcQualityControlChartDao emcQualityControlChartDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcQualityControlChartDao getDao() {
        return emcQualityControlChartDao;
    }

    @Override
    public Page<EmcOrderTaskResultBean> selectOrderTaskResultPagination(RestJsonWrapperBean wrapper) {
        MapperParameter parameter = wrapper.extractMapFilter();
        String pageSize = parameter.get("pageSize").toString();
        if (!StringUtils.isEmpty(pageSize) && NumberUtils.parseInt(pageSize) > -1) {
            wrapper.setParamValue("s", pageSize);
        }
        PageRowBounds rowBounds = wrapper.extractPageRowBounds();
        return selectPagination(() -> getDao().selectOrderTaskResultPagination(parameter), rowBounds);
    }

    @Override
    public List<EmcOrderTaskBean> selectTestSelectableList(RestJsonWrapperBean wrapper) {
        MapperParameter parameter = wrapper.extractMapFilter();
        return getDao().selectTestSelectableList(parameter);
    }

    @Override
    public List<EmcOrderTaskResultBean> selectItemSelectableList(RestJsonWrapperBean wrapper) {
        MapperParameter parameter = wrapper.extractMapFilter();
        return getDao().selectItemSelectableList(parameter);
    }

    @Override
    public List<EmcOrderTaskBean> selectQcCategorySelectableList(RestJsonWrapperBean wrapper) {
        MapperParameter parameter = wrapper.extractMapFilter();
        return getDao().selectQcCategorySelectableList(parameter);
    }

    @Override
    public List<EmcOrderTaskBean> selectUserSelectableList(RestJsonWrapperBean wrapper) {
        MapperParameter parameter = wrapper.extractMapFilter();
        return getDao().selectUserSelectableList(parameter);
    }

}
