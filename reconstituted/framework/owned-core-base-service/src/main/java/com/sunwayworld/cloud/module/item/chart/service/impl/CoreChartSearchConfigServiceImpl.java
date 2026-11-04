/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.chart.service.impl;

import com.sunwayworld.cloud.module.item.chart.bean.CoreChartSearchConfigBean;
import com.sunwayworld.cloud.module.item.chart.persistent.dao.CoreChartSearchConfigDao;
import com.sunwayworld.cloud.module.item.chart.service.CoreChartSearchConfigService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.mybatis.page.PageRowBounds;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreChartSearchConfigServiceImpl
implements CoreChartSearchConfigService {
    @Autowired
    private CoreChartSearchConfigDao searchConfigDao;

    @Override
    public CoreChartSearchConfigDao getDao() {
        return this.searchConfigDao;
    }

    @Override
    public Page<CoreChartSearchConfigBean> selectPaginationByChart(String chartId, RestJsonWrapperBean wrapper) {
        MapperParameter parameter = wrapper == null ? new MapperParameter() : wrapper.extractMapFilter();
        parameter.setFilter(SearchFilter.instance().match("MASTERID", chartId).filter(MatchPattern.EQ));
        PageRowBounds rowBounds = wrapper == null ? null : wrapper.extractPageRowBounds();
        return this.selectPagination(parameter, rowBounds);
    }
}
