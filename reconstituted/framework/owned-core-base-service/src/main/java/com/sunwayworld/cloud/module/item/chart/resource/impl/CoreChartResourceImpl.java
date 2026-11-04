/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.chart.resource.impl;

import com.sunwayworld.cloud.module.item.chart.bean.CoreChartBean;
import com.sunwayworld.cloud.module.item.chart.bean.CoreChartConfigBean;
import com.sunwayworld.cloud.module.item.chart.bean.CoreChartLinesRepresentation;
import com.sunwayworld.cloud.module.item.chart.bean.CoreChartSearchConfigBean;
import com.sunwayworld.cloud.module.item.chart.resource.CoreChartResource;
import com.sunwayworld.cloud.module.item.chart.service.CoreChartConfigService;
import com.sunwayworld.cloud.module.item.chart.service.CoreChartSearchConfigService;
import com.sunwayworld.cloud.module.item.chart.service.CoreChartService;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import com.sunwayworld.framework.support.selectable.resource.AbstractGenericSelectableResource;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u56fe\u8868")
@RestController
@GikamBean
public class CoreChartResourceImpl
implements CoreChartResource,
AbstractGenericResource<CoreChartService, CoreChartBean, String>,
AbstractGenericSelectableResource<CoreChartService, CoreChartBean, String> {
    @Autowired
    private CoreChartService chartService;
    @Autowired
    private CoreChartConfigService configService;
    @Autowired
    private CoreChartSearchConfigService searchConfigService;

    @Override
    public CoreChartService getService() {
        return this.chartService;
    }

    @Override
    @Log(value="\u65b0\u589e", type=LogType.INSERT)
    public String insert(RestJsonWrapperBean jsonWrapper) {
        return this.getService().insert(jsonWrapper);
    }

    @Override
    @Log(value="\u65b0\u589e\u201c\u56fe\u8868\u914d\u7f6e\u8868\u201d", type=LogType.INSERT)
    public Long insertConfig(@PathVariable String id, RestJsonWrapperBean wrapper) {
        return this.getService().insertConfig(id, wrapper);
    }

    @Override
    @Log(value="\u4fdd\u5b58\u914d\u7f6e\u64cd\u4f5c", type=LogType.UPDATE)
    public void saveConfig(@PathVariable String id, RestJsonWrapperBean wrapper) {
        this.getService().saveConfig(id, wrapper);
    }

    @Override
    @Log(value="\u5220\u9664\u201c\u56fe\u8868\u914d\u7f6e\u8868\u201d", type=LogType.DELETE)
    public void deleteConfig(@PathVariable String id, RestJsonWrapperBean wrapper) {
        this.getService().deleteConfig(id, wrapper);
    }

    @Override
    @Log(value="\u65b0\u589e\u201c\u56fe\u8868\u67e5\u8be2\u6761\u4ef6\u201d", type=LogType.INSERT)
    public Long insertSearchConfig(@PathVariable String id, RestJsonWrapperBean wrapper) {
        return this.getService().insertSearchConfig(id, wrapper);
    }

    @Override
    @Log(value="\u5220\u9664\u201c\u56fe\u8868\u67e5\u8be2\u6761\u4ef6\u201d", type=LogType.DELETE)
    public void deleteSearchConfig(@PathVariable String id, RestJsonWrapperBean wrapper) {
        this.getService().deleteSearchConfig(id, wrapper);
    }

    @Override
    @Log(value="\u53d1\u5e03", type=LogType.ACTIVATE)
    @ResponseStatus(value=HttpStatus.OK)
    public void submit(@PathVariable String id) {
        this.getService().submit(id);
    }

    @Override
    @Log(value="\u901a\u8fc7\u56fe\u8868\u7684sql\u67e5\u8be2\u6570\u636e", type=LogType.SELECT)
    public Pagination<Map<String, Object>> selectMapPaginationByChartSql(@PathVariable String id, RestJsonWrapperBean wrapper) {
        return (Pagination)this.getService().selectMapPaginationByChartSql(id, wrapper);
    }

    @Override
    @Log(value="\u901a\u8fc7\u4e3b\u8868\u67e5\u8be2\u201c\u56fe\u8868\u67e5\u8be2\u6761\u4ef6\u201d", type=LogType.SELECT)
    public Pagination<CoreChartSearchConfigBean> selectSearchConfigPaginationByChart(@PathVariable String id, RestJsonWrapperBean wrapper) {
        return (Pagination)this.searchConfigService.selectPaginationByChart(id, wrapper);
    }

    @Override
    @Log(value="\u901a\u8fc7\u4e3b\u8868\u67e5\u8be2\u201c\u56fe\u8868\u67e5\u8be2\u6761\u4ef6\u201d", type=LogType.SELECT)
    public Pagination<CoreChartConfigBean> selectConfigPaginationByChart(@PathVariable String id, RestJsonWrapperBean wrapper) {
        return (Pagination)this.configService.selectPaginationByChart(id, wrapper);
    }

    @Override
    @Log(value="\u901a\u8fc7\u4e3b\u8868\u6253\u5305\u201c\u56fe\u8868\u914d\u5236\u8868\u548c\u67e5\u8be2\u6761\u4ef6\u201d", type=LogType.SELECT)
    public CoreChartLinesRepresentation packageLinesByConfig(@PathVariable String id) {
        return this.getService().packageLinesByConfig(id);
    }
}
