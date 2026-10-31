package com.sunwayworld.emc.module.bp.info.reporttmpl.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.bp.info.reporttmpl.bean.EmcReportTmplLineBean;
import com.sunwayworld.emc.module.bp.info.reporttmpl.dao.EmcReportTmplLineDao;
import com.sunwayworld.emc.module.bp.info.reporttmpl.service.EmcReportTmplLineService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.NumberUtils;

@Repository
public class EmcReportTmplLineServiceImpl implements EmcReportTmplLineService {
    static String ORDERNO = "orderno";// 排序号
    @Autowired
    private EmcReportTmplLineDao reportTmplLineDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcReportTmplLineDao getDao() {
        return reportTmplLineDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(Long reportTmplId, RestJsonWrapperBean wrapper) {
        Long sorter = selectMaxSorterByCond(SearchFilter.instance().match("REPORTTMPLID", reportTmplId).filter(MatchPattern.EQ)) + 5;
        EmcReportTmplLineBean emcReportTmplLine = wrapper.getBodyList().size() > 0 ? wrapper.parseUnique(EmcReportTmplLineBean.class) : new EmcReportTmplLineBean();
        emcReportTmplLine.setId(ApplicationContextHelper.getNextIdentity());
        emcReportTmplLine.setReportTmplId(reportTmplId);
        emcReportTmplLine.setOrderNo(sorter);
        getDao().insert(emcReportTmplLine);
        return emcReportTmplLine.getId();
    }

    // 获取最大的序号
    @Override
    public Long selectMaxSorterByCond(SearchFilter cond) {
        return (long) selectListByFilter(cond).stream().mapToInt(r -> NumberUtils.parseInt(BeanUtils.getPropertyValue(r, ORDERNO))).max().orElse(0);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long copy(RestJsonWrapperBean wrapper) {
        EmcReportTmplLineBean emcReportTmplLine = wrapper.getBodyList().size() > 0 ? wrapper.parseUnique(EmcReportTmplLineBean.class) : new EmcReportTmplLineBean();
        Long sorter = selectMaxSorterByCond(SearchFilter.instance().match("REPORTTMPLID", emcReportTmplLine.getReportTmplId()).filter(MatchPattern.EQ)) + 5;

        emcReportTmplLine.setId(ApplicationContextHelper.getNextIdentity());
        emcReportTmplLine.setReportTmplId(emcReportTmplLine.getReportTmplId());
        emcReportTmplLine.setOrderNo(sorter);
        getDao().insert(emcReportTmplLine);
        return emcReportTmplLine.getId();
    }

}
