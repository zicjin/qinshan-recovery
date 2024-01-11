package com.sunwayworld.emc.module.mdm.emp.emp.service.impl;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.framework.constant.EmcBizConstant;
import com.sunwayworld.emc.module.bp.bp.bean.EmcFolderBean;
import com.sunwayworld.emc.module.bp.bp.service.EmcFolderService;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcProjectBean;
import com.sunwayworld.emc.module.bp.project.project.service.EmcProjectService;
import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportBean;
import com.sunwayworld.emc.module.bp.report.report.service.EmcReportService;
import com.sunwayworld.emc.module.mdm.basemdm.cert.bean.EmcCertBean;
import com.sunwayworld.emc.module.mdm.basemdm.cert.bean.EmcCertTestMethodBean;
import com.sunwayworld.emc.module.mdm.basemdm.cert.service.EmcCertService;
import com.sunwayworld.emc.module.mdm.basemdm.cert.service.EmcCertTestMethodService;
import com.sunwayworld.emc.module.mdm.emp.emp.bean.EmcEmpBean;
import com.sunwayworld.emc.module.mdm.emp.emp.bean.EmcEmpQualBean;
import com.sunwayworld.emc.module.mdm.emp.emp.bean.EmcEmpTestBean;
import com.sunwayworld.emc.module.mdm.emp.emp.dao.EmcEmpDao;
import com.sunwayworld.emc.module.mdm.emp.emp.service.EmcEmpQualService;
import com.sunwayworld.emc.module.mdm.emp.emp.service.EmcEmpService;
import com.sunwayworld.emc.module.mdm.emp.emp.service.EmcEmpTestService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.StringUtils;

/**
 * @author yuanhh@sunwayworld.com 2019骞�9鏈�4鏃�
 */
@GikamBean
@Repository
public class EmcEmpServiceImpl implements EmcEmpService {
    @Autowired
    private EmcEmpDao empDao;
    @Autowired
    private EmcEmpQualService empQualService;
    @Autowired
    private EmcEmpTestService empTestService;
    @Autowired
    private EmcCertService certService;
    @Autowired
    private EmcCertTestMethodService certTestMethodService;

    @Autowired
    @Lazy
    private EmcProjectService projectService;

    @Autowired
    @Lazy
    private EmcReportService reportService;
    
    @Autowired
    @Lazy
    private EmcFolderService folderService;

    @Override
    @SuppressWarnings("unchecked")
    public EmcEmpDao getDao() {
        return empDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public String insert(RestJsonWrapperBean wrapper) {
        List<EmcEmpBean> empList = wrapper.parse(EmcEmpBean.class);
        getDao().insert(empList);

        return empList.isEmpty() ? null : empList.get(0).getId();
    }

    @Override
    @Transactional
    public Long insertQual(String id, RestJsonWrapperBean wrapper) {
        return empQualService.insert(id, wrapper);
    }

    @Override
    @Transactional
    public void insertTest(String id, RestJsonWrapperBean wrapper) {
        empTestService.insert(id, wrapper);
    }

    @Override
    public Page<EmcEmpQualBean> selectQualPagationByEmp(String id, RestJsonWrapperBean wrapper) {
        return empQualService.selectPaginationByFilter(SearchFilter.instance().match("EMPID", id).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    public Page<EmcEmpTestBean> selectTestPagationByEmp(String id, RestJsonWrapperBean wrapper) {
        return empTestService.selectPaginationByFilter(SearchFilter.instance().match("EMPID", id).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    @Transactional
    public void deleteQual(String id, RestJsonWrapperBean wrapper) {
        empQualService.delete(wrapper);
    }

    @Override
    @Transactional
    public void deleteTest(String id, RestJsonWrapperBean wrapper) {
        empTestService.delete(wrapper);
    }

    @Override
    public Page<EmcCertBean> selectCretPagationByEmp(String id, RestJsonWrapperBean wrapper) {
        return certService.selectPaginationByFilter(SearchFilter.instance().match("EMPID", id).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    public Page<EmcCertTestMethodBean> selectCertTestMethodPagationByEmp(Long id, RestJsonWrapperBean wrapper) {
        return certTestMethodService.selectPaginationByFilter(SearchFilter.instance().match("CERTID", id).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    @Transactional
    public void insertCertTestMethod(Long id, RestJsonWrapperBean wrapper) {
        certTestMethodService.insert(id, wrapper);
    }

    @Override
    @Transactional
    public void deleteEmpCertTestMethod(RestJsonWrapperBean wrapper) {
        certTestMethodService.delete(wrapper);
    }

    @Override
    public Page<EmcEmpBean> selectEditReportEmpPagination(RestJsonWrapperBean wrapper) {
        Page<EmcEmpBean> empPage = selectChoosablePagination(wrapper);
        List<EmcEmpBean> empList = empPage.getRows();

        List<String> empIdList = empList.stream().map(EmcEmpBean::getId).distinct().collect(Collectors.toList());
        List<EmcProjectBean> projectList = projectService.getDao().selectListByOneColumnValues(empIdList, "REPORTEDITUSERID", Arrays.asList("ID", "STATUS", "REPORTEDITUSERID"));
        List<EmcProjectBean> normalProjectList = projectList.stream().filter(project -> !StringUtils.equals(project.getStatus(), EmcBizConstant.PROJECT_STATUS_CANCEL)).collect(Collectors.toList());

        // 杩囨护鍑哄浜庢姤鍛婇樁娈电殑椤圭洰锛屽苟涓旀壘鍑哄凡缁忓畬鎴愮殑浠诲姟锛堝浜庢姤鍛婄紪鍒朵箣鍚庣殑浠诲姟閮界畻宸插畬鎴愶級
        List<Long> reportEditProjectIdList = normalProjectList.stream().filter(project -> StringUtils.equals(project.getStatus(), "reportEdit")).map(EmcProjectBean::getId).collect(Collectors.toList());
        List<EmcReportBean> reportList = reportService.getDao().selectListByOneColumnValues(reportEditProjectIdList, "PROJID", Arrays.asList("ID", "PROJID", "STATUS"));
        List<Long> finishedProjectIdList = reportList.stream().filter(report -> !StringUtils.equals(report.getStatus(), "draft") && !StringUtils.equals(report.getStatus(), "reportEdit")).map(EmcReportBean::getProjId).distinct().collect(Collectors.toList());

        List<EmcProjectBean> unfinishedProjectList = normalProjectList.stream().filter(project -> !finishedProjectIdList.contains(project.getId())).collect(Collectors.toList());
        Map<String, Long> empUnfinishedProjectMap = unfinishedProjectList.stream().collect(Collectors.groupingBy(EmcProjectBean::getReportEditUserId, Collectors.counting()));

        empList.forEach(emp -> emp.setExt$Item("unfinishedProjectNum", Optional.ofNullable(empUnfinishedProjectMap.get(emp.getId())).orElse(0L) + ""));

        return new Pagination<>(empList);
    }

    @Override
    public Page<EmcEmpBean> selectSampleEmpPagination(RestJsonWrapperBean wrapper) {
        Page<EmcEmpBean> empPage = selectChoosablePagination(wrapper);
        String planSampleDate = wrapper.getFilterValue("planSampleDate");
        List<EmcEmpBean> empList = empPage.getRows();
        List<EmcFolderBean> folderList = folderService.selectListByFilter(SearchFilter.instance().match("PLANSAMPLEDATE", planSampleDate).filter(MatchPattern.DEQ).match("PLANSAMPLEUSERID", null).filter(MatchPattern.DIFFER));
        for(EmcEmpBean e : empList) {
            List<EmcFolderBean> filterList = folderList.stream().filter(b -> ("," + b.getPlanSampleUserId() + ",").contains("," + e.getId() + ",")).collect(Collectors.toList());
            String projNo = filterList.stream().filter(v -> !StringUtils.isEmpty(v.getExt$Item("projno"))).map(a -> a.getExt$Item("projno")).distinct().collect(Collectors.joining(","));
            if(!StringUtils.isEmpty(projNo)) {
                String projName = filterList.stream().filter(v -> !StringUtils.isEmpty(v.getExt$Item("projname"))).map(a -> a.getExt$Item("projname")).distinct().collect(Collectors.joining(","));
                e.setExt$Item("projno", projNo);
                e.setExt$Item("projname", projName);
                e.setExt$Item("plansampledate", LocalDate.now().toString());
            }
        }
        return new Pagination<>(empList);
    }

}
