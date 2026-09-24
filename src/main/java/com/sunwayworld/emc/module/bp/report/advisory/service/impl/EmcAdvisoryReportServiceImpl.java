package com.sunwayworld.emc.module.bp.report.advisory.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.framework.constant.PfBizConstant;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcProjectBean;
import com.sunwayworld.emc.module.bp.report.advisory.bean.EmcAdvisoryReportBean;
import com.sunwayworld.emc.module.bp.report.advisory.dao.EmcAdvisoryReportDao;
import com.sunwayworld.emc.module.bp.report.advisory.service.EmcAdvisoryReportService;
import com.sunwayworld.emc.module.mdm.basemdm.bizcategory.bean.EmcBizCategoryBean;
import com.sunwayworld.emc.module.mdm.basemdm.bizcategory.service.EmcBizCategoryService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.utils.DateTimeUtils;
import com.sunwayworld.module.mdm.org.bean.CoreOrgBean;
import com.sunwayworld.module.mdm.org.service.CoreOrgService;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceStatusDTO;

/**
* @author zhengxy@sunwayworld.com 2022年08月29日
*/
@Repository
public class EmcAdvisoryReportServiceImpl implements EmcAdvisoryReportService {

    @Autowired
    private EmcAdvisoryReportDao emcAdvisoryReportDao;

    @Autowired
    private CoreOrgService coreOrgService;

    @Autowired
    private EmcBizCategoryService bizCategoryService;

    @Override
    @SuppressWarnings("unchecked")
    public EmcAdvisoryReportDao getDao() {
        return emcAdvisoryReportDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean wrapperBean) {
        EmcAdvisoryReportBean emcAdvisoryReportBean = wrapperBean.parseUnique(EmcAdvisoryReportBean.class);
        Long id = ApplicationContextHelper.getNextIdentity();
        emcAdvisoryReportBean.setId(id);
        CoreUserBean user = LocalContextHelper.getLoginUser();
        String parentOrgId = user.getExt$Item("parentorgid");
        CoreOrgBean parentOrg = (CoreOrgBean) coreOrgService.selectById(parentOrgId);
        EmcBizCategoryBean bizCategory = (EmcBizCategoryBean) bizCategoryService.selectById(emcAdvisoryReportBean.getBizCategoryId());
        String orgCode = Optional.ofNullable(parentOrg.getExt$Item("orgshortname")).orElse("XX");
        String year = DateTimeUtils.formatLocalDateTime(LocalDateTime.now(), "yyyy");
        String projNoPrefix = orgCode + bizCategory.getBizCategoryId() + year;
        String reportNo = "";
        String key = "";
        if ("SFJD".equals(emcAdvisoryReportBean.getBizClassId())) {
            key = emcAdvisoryReportBean.getCreatedByOrgName() + "检验检测有限公司司法鉴定所[" + year + "]鉴检字第";
            reportNo = ApplicationContextHelper.getNextSequence("T_EMC_ADVISORY_REPORT_SF", key) + "号";
        } else {
            reportNo = ApplicationContextHelper.getNextSequence("T_EMC_ADVISORY_REPORT_SF", projNoPrefix);
        }
        emcAdvisoryReportBean.setReportNo(reportNo);
        getDao().insert(emcAdvisoryReportBean);
        return id;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void createAdvisoryReport(List<EmcProjectBean> projectList) {
        List<EmcAdvisoryReportBean> reportList = new ArrayList<>(projectList.size());
        projectList.forEach(project -> {
            EmcAdvisoryReportBean newReport = new EmcAdvisoryReportBean();
            newReport.setId(ApplicationContextHelper.getNextIdentity());
            newReport.setProjId(project.getId());
            newReport.setStatus(PfBizConstant.ADVISORY_REPORT_STATUS_REPORTEDIT);
            reportList.add(newReport);
        });

        getDao().insert(reportList);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.PROCESS_START)
    public List<CoreBpmnInstanceStatusDTO<Long>> startProcess(RestJsonWrapperBean wrapper) {
        List<CoreBpmnInstanceStatusDTO<Long>> coreBpmnInstanceStatusDTOList = EmcAdvisoryReportService.super.startProcess(wrapper);
        List<EmcAdvisoryReportBean> reportList = wrapper.parse(EmcAdvisoryReportBean.class);

        // 更新报告状态
        reportList.forEach(report -> report.setStatus(PfBizConstant.ADVISORY_REPORT_STATUS_REPORTCHECK));

        getDao().update(reportList, "STATUS");

        return coreBpmnInstanceStatusDTOList;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.PROCESS_COMPLETE)
    public List<CoreBpmnInstanceStatusDTO<Long>> completeTask(RestJsonWrapperBean wrapper) {

        List<CoreBpmnInstanceStatusDTO<Long>> coreBpmnInstanceStatusDTOList = EmcAdvisoryReportService.super.completeTask(wrapper);
        List<Long> reportIdList = coreBpmnInstanceStatusDTOList.stream().map(CoreBpmnInstanceStatusDTO::getId).collect(Collectors.toList());
        List<EmcAdvisoryReportBean> reportList = selectListByIds(reportIdList);

        // 更新报告状态
        String statusCode = coreBpmnInstanceStatusDTOList.get(0).getStatusCode();
        List<EmcAdvisoryReportBean> updateReportList = new ArrayList<>();
        reportList.forEach(report -> {
            EmcAdvisoryReportBean updateReport = new EmcAdvisoryReportBean();
            updateReport.setId(report.getId());
            if (PfBizConstant.ADVISORY_REPORT_STATUS_REPORTCHECK.equals(statusCode)) {
                updateReport.setStatus(PfBizConstant.ADVISORY_REPORT_STATUS_REPORTAUDIT);
            } else if (PfBizConstant.ADVISORY_REPORT_STATUS_REPORTAUDIT.equals(statusCode)) {
                updateReport.setStatus(PfBizConstant.ADVISORY_REPORT_STATUS_REPORTISSUE);
            } else if (PfBizConstant.ADVISORY_REPORT_STATUS_REPORTISSUE.equals(statusCode)) {
                if (Constant.YES.equals(report.getDiscuss())) {
                    updateReport.setStatus(PfBizConstant.ADVISORY_REPORT_STATUS_REPORTDISCUSS);
                } else if (Constant.YES.equals(report.getRevise())) {
                    updateReport.setStatus(PfBizConstant.ADVISORY_REPORT_STATUS_REPORTREVISE);
                } else {
                    updateReport.setStatus(PfBizConstant.ADVISORY_REPORT_STATUS_REPORTARCHIVING);
                }
            } else if (PfBizConstant.ADVISORY_REPORT_STATUS_REPORTDISCUSS.equals(statusCode)) {
                if (Constant.YES.equals(report.getRevise())) {
                    updateReport.setStatus(PfBizConstant.ADVISORY_REPORT_STATUS_REPORTREVISE);
                } else {
                    updateReport.setStatus(PfBizConstant.ADVISORY_REPORT_STATUS_REPORTARCHIVING);
                }
            } else if (PfBizConstant.ADVISORY_REPORT_STATUS_REPORTARCHIVING.equals(statusCode)) {
                updateReport.setStatus(PfBizConstant.ADVISORY_REPORT_STATUS_DONE);
            }
            updateReportList.add(updateReport);
        });
        getDao().update(updateReportList, "STATUS");

        return coreBpmnInstanceStatusDTOList;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.PROCESS_REJECT)
    public List<CoreBpmnInstanceStatusDTO<Long>> rejectTask(RestJsonWrapperBean wrapper) {
        List<CoreBpmnInstanceStatusDTO<Long>> coreBpmnInstanceStatusDTOList = EmcAdvisoryReportService.super.rejectTask(wrapper);
        List<Long> reportIdList = coreBpmnInstanceStatusDTOList.stream().map(CoreBpmnInstanceStatusDTO::getId).collect(Collectors.toList());
        List<EmcAdvisoryReportBean> reportList = selectListByFilter(SearchFilter.instance().match("ID", reportIdList).filter(MatchPattern.OR));

        // 更新报告状态
        String bpmn_targetStatusCode = wrapper.getParamMap().get("bpmn_targetStatusCode");
        List<EmcAdvisoryReportBean> updateReportList = new ArrayList<>();
        reportList.forEach(e -> {
            EmcAdvisoryReportBean updateReport = new EmcAdvisoryReportBean();
            updateReport.setId(e.getId());
            if (PfBizConstant.ADVISORY_REPORT_STATUS_DRAFT.equalsIgnoreCase(bpmn_targetStatusCode)) {
                updateReport.setStatus(PfBizConstant.ADVISORY_REPORT_STATUS_REPORTEDIT);
            } else {
                updateReport.setStatus(bpmn_targetStatusCode);
            }
            updateReportList.add(updateReport);
        });
        getDao().update(updateReportList, "STATUS");

        return coreBpmnInstanceStatusDTOList;
    }
}
