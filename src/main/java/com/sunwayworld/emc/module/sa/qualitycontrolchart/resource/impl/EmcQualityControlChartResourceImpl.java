package com.sunwayworld.emc.module.sa.qualitycontrolchart.resource.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskResultBean;
import com.sunwayworld.emc.module.sa.qualitycontrolchart.resource.EmcQualityControlChartResource;
import com.sunwayworld.emc.module.sa.qualitycontrolchart.service.EmcQualityControlChartService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@LogModule("质控图")
@RestController
@GikamBean
public class EmcQualityControlChartResourceImpl implements EmcQualityControlChartResource {
    @Autowired
    private EmcQualityControlChartService emcQualityControlChartService;

    @Override
    public EmcQualityControlChartService getService() {
        return emcQualityControlChartService;
    }

    @Log(value = "查询结果列表", type = LogType.SELECT)
    @Override
    @RequestMapping(value = "/order-task-results/queries", method = RequestMethod.POST)
    public Page<EmcOrderTaskResultBean> selectOrderTaskResultPagination(RestJsonWrapperBean wrapper) {
        return getService().selectOrderTaskResultPagination(wrapper);
    }

    @Log(value = "查询测试下拉框", type = LogType.SELECT)
    @Override
    @RequestMapping(value = "/tests/queries/selectable", method = RequestMethod.POST)
    public List<EmcOrderTaskBean> selectTestSelectableList(RestJsonWrapperBean wrapper) {
        return getService().selectTestSelectableList(wrapper);
    }

    @Log(value = "查询分析项下拉框", type = LogType.SELECT)
    @Override
    @RequestMapping(value = "/items/queries/selectable", method = RequestMethod.POST)
    public List<EmcOrderTaskResultBean> selectItemSelectableList(RestJsonWrapperBean wrapper) {
        return getService().selectItemSelectableList(wrapper);
    }

    @Log(value = "查询QC类型下拉框", type = LogType.SELECT)
    @Override
    @RequestMapping(value = "/qc-categories/queries/selectable", method = RequestMethod.POST)
    public List<EmcOrderTaskBean> selectQcCategorySelectableList(RestJsonWrapperBean wrapper) {
        return getService().selectQcCategorySelectableList(wrapper);
    }

    @Log(value = "查询分析人员下拉框", type = LogType.SELECT)
    @Override
    @RequestMapping(value = "/users/queries/selectable", method = RequestMethod.POST)
    public List<EmcOrderTaskBean> selectUserSelectableList(RestJsonWrapperBean wrapper) {
        return getService().selectUserSelectableList(wrapper);
    }

}
