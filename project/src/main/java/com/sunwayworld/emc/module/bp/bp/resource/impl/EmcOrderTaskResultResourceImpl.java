package com.sunwayworld.emc.module.bp.bp.resource.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.bp.bp.resource.EmcOrderTaskResultResource;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskResultService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.table.bean.TableHeadBean;
import com.sunwayworld.framework.support.table.bean.TableRowBean;

@LogModule("样品结果")
@RestController
@GikamBean
public class EmcOrderTaskResultResourceImpl implements EmcOrderTaskResultResource {

    @Autowired
    private EmcOrderTaskResultService orderTaskResultService;

    @Override
    public EmcOrderTaskResultService getService() {
        return orderTaskResultService;
    }

    @Log(value = "原始结果修约", type = LogType.UPDATE)
    @Override
    @RequestMapping(value = "/action/change-result", method = RequestMethod.PUT)
    public void changeResult(RestJsonWrapperBean wrapper) {
        getService().changeResult(wrapper);
    }

    @Log(value = "查询数据汇总的表头列表", type = LogType.SELECT)
    @Override
    @RequestMapping(value = "/data-titles/queries", method = RequestMethod.POST)
    public List<TableHeadBean> selectResultDataSummaryTitleList(RestJsonWrapperBean wrapper) {
        return orderTaskResultService.selectResultDataSummaryTitleList(wrapper);
    }

    @Log(value = "查询数据汇总的值列表", type = LogType.SELECT)
    @Override
    @RequestMapping(value = "/data-rows/queries", method = RequestMethod.POST)
    public List<TableRowBean> selectResultDataSummaryRowList(RestJsonWrapperBean wrapper) {
        return orderTaskResultService.selectResultDataSummaryRowList(wrapper);
    }

    @Override
    @Log(value = "数据查询页面查询数据", type = LogType.SELECT)
    @RequestMapping(value = "/data-query-rows/queries", method = RequestMethod.POST)
    public Page<TableRowBean> selectDataQueryRowList(RestJsonWrapperBean wrapper) {
        return getService().selectDataQueryRowList(wrapper);
    }

    @Override
    @Log(value = "数据查询页面查询表头", type = LogType.SELECT)
    @RequestMapping(value = "/data-query-titles/queries", method = RequestMethod.POST)
    public List<TableHeadBean> selectDataQueryTitleList(RestJsonWrapperBean wrapper) {
        return getService().selectDataQueryTitleList(wrapper);
    }

}
