package com.sunwayworld.emc.module.bp.info.reporttmpl.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.bp.info.reporttmpl.bean.EmcReportTmplBean;
import com.sunwayworld.emc.module.bp.info.reporttmpl.bean.EmcReportTmplLineBean;
import com.sunwayworld.emc.module.bp.info.reporttmpl.dao.EmcReportTmplDao;
import com.sunwayworld.emc.module.bp.info.reporttmpl.service.EmcReportTmplLineService;
import com.sunwayworld.emc.module.bp.info.reporttmpl.service.EmcReportTmplService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.utils.BeanUtils;

@Repository
public class EmcReportTmplServiceImpl implements EmcReportTmplService {

    @Autowired
    private EmcReportTmplDao reportTmplDao;

    @Autowired
    private EmcReportTmplLineService reportTmplLineService;

    @Override
    @SuppressWarnings("unchecked")
    public EmcReportTmplDao getDao() {
        return reportTmplDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        EmcReportTmplBean emcReportTmpl = jsonWrapper.parseUnique(EmcReportTmplBean.class);
        emcReportTmpl.setId(ApplicationContextHelper.getNextIdentity());
        Long sorter = selectMaxSorter() + 5L;
        emcReportTmpl.setOrderNo(sorter);
        getDao().insert(emcReportTmpl);
        return emcReportTmpl.getId();
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void copyTo(String id, RestJsonWrapperBean wrapper) {
        List<EmcReportTmplBean> reportTmplList = wrapper.parse(EmcReportTmplBean.class);
        
        List<EmcReportTmplLineBean> reportTmplLineList = reportTmplLineService.getDao().selectListByOneColumnValue(Long.valueOf(id), "REPORTTMPLID");
        List<EmcReportTmplLineBean> insertList = new ArrayList<>();
        
        for (EmcReportTmplBean reportTmpl : reportTmplList) {
            for (EmcReportTmplLineBean reportTmplLine : reportTmplLineList) {
                EmcReportTmplLineBean bean = new EmcReportTmplLineBean();
                BeanUtils.copyProperties(reportTmplLine, bean, new String[] { "ID", "REPORTTMPLID", "CREATEDBYID", "CREATEDBYNAME", "CREATEDTIME", "CREATEDBYORGID", "CREATEDBYORGNAME"});
                bean.setId(ApplicationContextHelper.getNextIdentity());
                bean.setReportTmplId(reportTmpl.getId());
                insertList.add(bean);
            }
        }
        reportTmplLineService.getDao().insert(insertList);
    }

    // 获取最大的序号
    private Long selectMaxSorter() {
        EmcReportTmplBean temp = getDao().selectFirstIfPresent(new EmcReportTmplBean(), Arrays.asList("ORDERNO"), Order.desc("ORDERNO"));
        return temp == null ? 0L : temp.getOrderNo();
    }

    @Override
    public void copyLine(RestJsonWrapperBean wrapper) {
        reportTmplLineService.copy(wrapper);
    }

    @Override
    public Long insertLine(Long reportTmplId, RestJsonWrapperBean wrapper) {
        return reportTmplLineService.insert(reportTmplId, wrapper);
    }

    @Override
    public Page<EmcReportTmplLineBean> selectLinePagation(Long reportTmplId, RestJsonWrapperBean wrapper) {
        return reportTmplLineService.selectPaginationByFilter(SearchFilter.instance().match("REPORTTMPLID", reportTmplId).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    @AuditTrailEntry(AuditTrailType.DELETE)
    public void deleteLine(RestJsonWrapperBean wrapper) {
        reportTmplLineService.delete(wrapper);
    }

}
