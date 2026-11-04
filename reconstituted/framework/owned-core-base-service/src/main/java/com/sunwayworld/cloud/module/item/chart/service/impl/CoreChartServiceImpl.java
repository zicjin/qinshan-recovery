/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.chart.service.impl;

import com.sunwayworld.cloud.module.item.chart.bean.CoreChartBean;
import com.sunwayworld.cloud.module.item.chart.bean.CoreChartConfigBean;
import com.sunwayworld.cloud.module.item.chart.bean.CoreChartLinesRepresentation;
import com.sunwayworld.cloud.module.item.chart.bean.CoreChartSearchConfigBean;
import com.sunwayworld.cloud.module.item.chart.persistent.dao.CoreChartDao;
import com.sunwayworld.cloud.module.item.chart.service.CoreChartConfigService;
import com.sunwayworld.cloud.module.item.chart.service.CoreChartSearchConfigService;
import com.sunwayworld.cloud.module.item.chart.service.CoreChartService;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.mybatis.page.MybatisPageHelper;
import com.sunwayworld.framework.mybatis.page.PageRowBounds;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.sys.bpmn.diagram.ProcessStatus;
import com.sunwayworld.module.sys.menu.bean.CoreMenuBean;
import com.sunwayworld.module.sys.menu.service.CoreMenuService;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@GikamBean
@Repository
public class CoreChartServiceImpl
implements CoreChartService {
    @Autowired
    private CoreChartDao chartDao;
    @Autowired
    @Lazy
    private CoreChartConfigService configService;
    @Autowired
    @Lazy
    private CoreChartSearchConfigService searchConfigService;
    @Autowired
    private CoreMenuService menuService;

    @Override
    public CoreChartDao getDao() {
        return this.chartDao;
    }

    @Override
    @Transactional
    public String insert(RestJsonWrapperBean jsonWrapper) {
        CoreChartBean chart = jsonWrapper.parseUnique(CoreChartBean.class);
        String id = ApplicationContextHelper.getNextSequence(this.getDao().getTable());
        chart.setId(id);
        this.getDao().insert(chart);
        return id;
    }

    @Override
    @Transactional
    public Long insertConfig(String id, RestJsonWrapperBean wrapper) {
        CoreChartConfigBean chartConfig = wrapper.parseUnique(CoreChartConfigBean.class);
        chartConfig.setId(ApplicationContextHelper.getNextIdentity());
        chartConfig.setMasterId(id);
        chartConfig.setChart("0");
        this.configService.getDao().insert((CoreChartConfigBean)chartConfig);
        return chartConfig.getId();
    }

    @Override
    @Transactional
    public void saveConfig(String id, RestJsonWrapperBean wrapper) {
        List<CoreChartSearchConfigBean> chartSearchConfigList;
        List<CoreChartConfigBean> chartConfigList = wrapper.parse(CoreChartConfigBean.class);
        if (chartConfigList.size() > 0) {
            this.configService.getDao().updateIfChanged(chartConfigList);
        }
        if ((chartSearchConfigList = wrapper.parse(CoreChartSearchConfigBean.class)).size() > 0) {
            this.searchConfigService.getDao().updateIfChanged(chartSearchConfigList);
        }
    }

    @Override
    @Transactional
    public void deleteConfig(String id, RestJsonWrapperBean wrapper) {
        this.configService.delete(wrapper);
    }

    @Override
    @Transactional
    public Long insertSearchConfig(String id, RestJsonWrapperBean wrapper) {
        CoreChartSearchConfigBean chartConfig = wrapper.parseUnique(CoreChartSearchConfigBean.class);
        chartConfig.setId(ApplicationContextHelper.getNextIdentity());
        chartConfig.setMasterId(id);
        this.searchConfigService.getDao().insert((CoreChartSearchConfigBean)chartConfig);
        return chartConfig.getId();
    }

    @Override
    @Transactional
    public void deleteSearchConfig(String id, RestJsonWrapperBean wrapper) {
        this.searchConfigService.delete(wrapper);
    }

    @Override
    @Transactional
    public void submit(String id) {
        CoreChartBean chart = (CoreChartBean)this.getDao().selectById(id);
        CoreChartBean proxy = BeanUtils.getPropertyListenerProxy(chart);
        proxy.setProcessStatus(ProcessStatus.DONE.name().toLowerCase());
        this.getDao().update(proxy, new String[0]);
        this.insertMenuByChartId(id);
    }

    @Override
    public Page<Map<String, Object>> selectMapPaginationByChartSql(String id, RestJsonWrapperBean wrapper) {
        MapperParameter parameter = wrapper.extractMapFilter(MapperParameter.class);
        PageRowBounds rowBounds = wrapper.extractPageRowBounds();
        CoreChartBean chart = (CoreChartBean)this.selectDetail(id);
        String sql = chart.getSearchSql();
        if (!StringUtils.isEmpty(chart.getSearchRule())) {
            String[] searchRule;
            for (String authority : searchRule = StringUtils.split(chart.getSearchRule(), ",")) {
                if (StringUtils.isEmpty(authority)) continue;
                parameter.setAuthorityParameter(authority);
            }
        }
        parameter.put("sql", sql);
        Page<Map<String, Object>> mapPage = MybatisPageHelper.get(rowBounds, () -> this.getDao().selectSqlData(parameter));
        CoreChartConfigBean search = new CoreChartConfigBean();
        search.setMasterId(id);
        search.setColumnType("date");
        List<CoreChartConfigBean> chartConfigList = this.configService.selectList(search, new Order[0]);
        for (CoreChartConfigBean chartConfig : chartConfigList) {
            String field = chartConfig.getColumnField();
            mapPage.getRows().forEach(e -> {
                if (!ObjectUtils.isEmpty(e.get(field))) {
                    e.put(field, new SimpleDateFormat(StringUtils.isEmpty(chartConfig.getColumnCategory()) ? "yyyy-MM-dd" : chartConfig.getColumnCategory()).format((Timestamp)e.get(field)));
                }
            });
        }
        return mapPage;
    }

    @Override
    public CoreChartLinesRepresentation packageLinesByConfig(String id) {
        CoreChartLinesRepresentation pack = new CoreChartLinesRepresentation();
        pack.setConfigList(this.configService.selectPaginationByChart(id).getRows());
        pack.setSearchConfigList(this.searchConfigService.selectPaginationByChart(id).getRows());
        return pack;
    }

    private void insertMenuByChartId(String id) {
        CoreChartBean chart = (CoreChartBean)this.getDao().selectById(id);
        ArrayList<CoreMenuBean> addMenuList = new ArrayList<CoreMenuBean>();
        CoreMenuBean thirdMenu = new CoreMenuBean();
        thirdMenu.setId(id);
        thirdMenu.setMenuName(chart.getChartName());
        thirdMenu.setMenuInitName(chart.getChartName());
        thirdMenu.setOrderNo(NumberUtils.parseLong(chart.getId()));
        thirdMenu.setParentId("1299");
        addMenuList.add(thirdMenu);
        CoreMenuBean fourthMenu = new CoreMenuBean();
        fourthMenu.setId(id + "02");
        fourthMenu.setMenuName("\u67e5\u8be2");
        fourthMenu.setMenuInitName("\u67e5\u8be2");
        fourthMenu.setOrderNo(NumberUtils.parseLong(id + "02"));
        fourthMenu.setParentId(id);
        fourthMenu.setHref("/secure/core/module/sys/charts/page/display?id=" + id);
        addMenuList.add(fourthMenu);
        this.menuService.getDao().insert(addMenuList);
    }
}
