package com.sunwayworld.emc.module.bp.info.gismap.service.impl;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.info.gismap.service.EmcGisMapService;
import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportResultBean;
import com.sunwayworld.emc.module.bp.report.report.service.EmcReportResultService;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

@Repository
public class EmcGisMapServiceImpl implements EmcGisMapService {

    @Autowired
    private EmcReportResultService reportResultService;

    @Override
    public List<EmcReportResultBean> selectMonitorPointCategories(RestJsonWrapperBean wrapper) {
        String beginDate = wrapper.getFilterValue("beginDate");
        String endDate = wrapper.getFilterValue("endDate");
        List<EmcReportResultBean> reportResultList = reportResultService
                .selectListByFilter(SearchFilter.instance().match("REGISTDATE", beginDate).filter(MatchPattern.DGOE).match("REGISTDATE", endDate).filter(MatchPattern.DLOE));
        List<String> mpcList = new ArrayList<>();
        List<EmcReportResultBean> resultList = new LinkedList<>();
        for (EmcReportResultBean e : reportResultList) {
            if (!mpcList.contains(e.getExt$Item("monitorpointcategoryid"))) {
                mpcList.add(e.getExt$Item("monitorpointcategoryid"));
                resultList.add(e);
            }
        }
        return resultList;

    }

    @Override
    public List<EmcReportResultBean> selectTestNames(RestJsonWrapperBean wrapper) {
        String beginDate = wrapper.getFilterValue("beginDate");
        String endDate = wrapper.getFilterValue("endDate");
        String monitorPointCategory = wrapper.getFilterValue("monitorPointCategory");
        List<EmcReportResultBean> reportResultList = reportResultService.selectListByFilter(SearchFilter.instance().match("REGISTDATE", beginDate).filter(MatchPattern.DGOE)
                .match("REGISTDATE", endDate).filter(MatchPattern.DLOE).match("MONITORPOINTCATEGORYID", monitorPointCategory).filter(MatchPattern.EQ));
        List<Long> mpcList = new ArrayList<>();
        List<EmcReportResultBean> resultList = new LinkedList<>();
        for (EmcReportResultBean e : reportResultList) {
            if (!mpcList.contains(e.getTestMethodId())) {
                mpcList.add(e.getTestMethodId());
                resultList.add(e);
            }
        }
        return resultList;

    }

    @Override
    public List<EmcReportResultBean> selectItemAliasNames(RestJsonWrapperBean wrapper) {
        String beginDate = wrapper.getFilterValue("beginDate");
        String endDate = wrapper.getFilterValue("endDate");
        String monitorPointCategory = wrapper.getFilterValue("monitorPointCategory");
        String testName = wrapper.getFilterValue("testName");
        List<EmcReportResultBean> reportResultList = reportResultService
                .selectListByFilter(SearchFilter.instance().match("REGISTDATE", beginDate).filter(MatchPattern.DGOE).match("REGISTDATE", endDate).filter(MatchPattern.DLOE)
                        .match("TESTMETHODID", testName).filter(MatchPattern.EQ).match("MONITORPOINTCATEGORYID", monitorPointCategory).filter(MatchPattern.EQ).match("REPORTDISPLAY", Constant.ACTIVATED_STATUS_YES).filter(MatchPattern.EQ));
        List<Long> mpcList = new ArrayList<>();
        List<EmcReportResultBean> resultList = new LinkedList<>();
        for (EmcReportResultBean e : reportResultList) {
            if (!mpcList.contains(e.getItemId())) {
                mpcList.add(e.getItemId());
                resultList.add(e);
            }
        }
        return resultList;

    }
}
