package com.sunwayworld.emc.module.bp.project.projectqc.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderBean;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderService;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcOrderItemGroupBean;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcProjectBean;
import com.sunwayworld.emc.module.bp.project.project.service.EmcOrderItemGroupService;
import com.sunwayworld.emc.module.bp.project.projectqc.bean.EmcProjectQcBean;
import com.sunwayworld.emc.module.bp.project.projectqc.bean.EmcProjectQcLineBean;
import com.sunwayworld.emc.module.bp.project.projectqc.bean.QcLineResultDTO;
import com.sunwayworld.emc.module.bp.project.projectqc.dao.EmcProjectQcDao;
import com.sunwayworld.emc.module.bp.project.projectqc.service.EmcProjectQcLineService;
import com.sunwayworld.emc.module.bp.project.projectqc.service.EmcProjectQcService;
import com.sunwayworld.emc.module.bp.project.projscheme.bean.EmcProjOrderBean;
import com.sunwayworld.emc.module.bp.project.projscheme.bean.EmcProjSchemeBean;
import com.sunwayworld.emc.module.bp.project.projscheme.service.EmcProjOrderService;
import com.sunwayworld.emc.module.bp.project.projscheme.service.EmcProjSchemeService;
import com.sunwayworld.emc.module.mdm.basemdm.qccategory.bean.EmcQcCategoryBean;
import com.sunwayworld.emc.module.mdm.basemdm.qccategory.service.EmcQcCategoryService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.table.TableCellDescriptor;
import com.sunwayworld.framework.support.table.TableHelper;
import com.sunwayworld.framework.support.table.bean.TableHeadBean;
import com.sunwayworld.framework.support.table.bean.TableRowBean;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;

/**
* @author zhengxy@sunwayworld.com 2020年09月21日
*/
@Repository
public class EmcProjectQcServiceImpl implements EmcProjectQcService {

    @Autowired
    private EmcProjectQcLineService projectQcLineService;

    @Autowired
    private EmcQcCategoryService qcCategoryService;

    @Autowired
    private EmcOrderService orderService;

    @Autowired
    private EmcOrderItemGroupService orderItemGroupService;

    @Autowired
    private EmcProjSchemeService projSchemeService;

    @Autowired
    private EmcProjOrderService projOrderService;

    @Autowired
    private EmcProjectQcDao emcProjectQcDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcProjectQcDao getDao() {
        return emcProjectQcDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean wrapperBean) {
        EmcProjectQcBean emcProjectQcBean = wrapperBean.parseUnique(EmcProjectQcBean.class);
        Long id = ApplicationContextHelper.getNextIdentity();
        emcProjectQcBean.setId(id);
        getDao().insert(emcProjectQcBean);
        return id;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void insert(List<EmcProjectBean> projectList) {
        List<EmcQcCategoryBean> qcCategoryList = qcCategoryService.getDao().selectListByOneColumnValues(Arrays.asList(EmcConstant.QCCATEGORY_USECATEGORY_LAB, EmcConstant.QCCATEGORY_USECATEGORY_SCENE), "USECATEGORY");
        qcCategoryList.sort(Comparator.comparing(EmcQcCategoryBean::getUseCategory).reversed());

        List<EmcProjectQcBean> projectQcList = new ArrayList<>();
        List<EmcProjectQcLineBean> projectQcLineList = new ArrayList<>();

        // 送样任务
        List<EmcProjectBean> noSampleProjectList = projectList.stream().filter(e -> !Constant.YES.equals(e.getSample())).collect(Collectors.toList());
        if (!noSampleProjectList.isEmpty()) {
            List<Long> noSampleProjectIdList = noSampleProjectList.stream().map(EmcProjectBean::getId).collect(Collectors.toList());

            List<EmcOrderBean> orderList = orderService.getDao().selectListByOneColumnValues(noSampleProjectIdList, "PROJID");
            List<Long> orderIdList = orderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList());
            List<EmcOrderItemGroupBean> orderItemGroupList = orderItemGroupService.getDao().selectListByOneColumnValues(orderIdList, "ORDERID");

            createNoSamplingProjectQcLine(orderList, orderItemGroupList, qcCategoryList, projectQcList, projectQcLineList);
        }

        // 采样任务
        List<EmcProjectBean> sampleProjectList = projectList.stream().filter(e -> Constant.YES.equals(e.getSample())).collect(Collectors.toList());
        if (!sampleProjectList.isEmpty()) {
            List<Long> sampleProjectIdList = sampleProjectList.stream().map(EmcProjectBean::getId).collect(Collectors.toList());

            List<EmcProjSchemeBean> projectSchemeList = projSchemeService.selectListByFilter(SearchFilter.instance().match("PROJID", sampleProjectIdList).filter(MatchPattern.OR));
            List<Long> projectSchemeIdList = projectSchemeList.stream().map(EmcProjSchemeBean::getId).collect(Collectors.toList());
            List<EmcProjOrderBean> schemeOrderList = projOrderService.selectListByFilter(SearchFilter.instance().match("PROJSCHEMEID", projectSchemeIdList).filter(MatchPattern.OR));

            createSamplingProjectQcLine(projectSchemeList, schemeOrderList, qcCategoryList, projectQcList, projectQcLineList);
        }

        if (!projectQcList.isEmpty()) {
            getDao().insert(projectQcList);
        }

        if (!projectQcLineList.isEmpty()) {
            projectQcLineService.getDao().insert(projectQcLineList);
        }

    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.DELETE)
    public void delete(List<Long> projectList) {
        List<EmcProjectQcBean> projectQcList = getDao().selectListByOneColumnValues(projectList, "PROJECTID", Collections.singletonList("ID"));
        List<Long> qcIdList = projectQcList.stream().map(EmcProjectQcBean::getId).collect(Collectors.toList());
        List<EmcProjectQcLineBean> projectQcLineList = projectQcLineService.getDao().selectListByOneColumnValues(qcIdList, "QCID", Collections.singletonList("ID"));

        getDao().deleteByIdList(qcIdList);
        projectQcLineService.getDao().deleteBy(projectQcLineList);
    }

    @Override
    public Page<EmcProjectQcBean> selectOrderCategoryPagination(Long id, RestJsonWrapperBean wrapper) {
        List<EmcProjectQcBean> projectQcList = getDao().selectListByOneColumnValue(id, "PROJECTID", Arrays.asList("ID", "PROJECTID", "MONITORTMPLID", "ORDERCATEGORY"));
        List<EmcProjectQcBean> uniqueOrderCategoryList = projectQcList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(EmcProjectQcBean::getMonitorTmplId))), ArrayList::new));

        return new Pagination<>(uniqueOrderCategoryList);
    }

    @Override
    public List<TableHeadBean> selectQcLineTitleList(RestJsonWrapperBean wrapper) {
        EmcProjectQcBean projectQc = wrapper.parseUnique(EmcProjectQcBean.class);
        List<EmcProjectQcBean> projectQcList = selectListByFilter(SearchFilter.instance().match("PROJECTID", projectQc.getProjectId()).filter(MatchPattern.EQ).match("MONITORTMPLID", projectQc.getMonitorTmplId()).filter(MatchPattern.EQ));
        List<Long> projectQcIdList = projectQcList.stream().map(EmcProjectQcBean::getId).collect(Collectors.toList());
        List<EmcProjectQcLineBean> qcLineList = projectQcLineService.getDao().selectListByOneColumnValues(projectQcIdList, "QCID", Arrays.asList("ID", "QCCATEGORYID", "QCNAME"));
        Map<Long, String> qcCategoryMap = qcLineList.stream().collect(Collectors.toMap(EmcProjectQcLineBean::getQcCategoryId, EmcProjectQcLineBean::getQcName, (key1, key2) -> key2, LinkedHashMap::new));

        List<TableHeadBean> tableHeadList = new ArrayList<>();
        qcCategoryMap.forEach((qcCategoryId, qcName) -> {
            TableHeadBean tableHead = new TableHeadBean();
            tableHead.setField(qcCategoryId + "");
            tableHead.setTitle(qcName);
            tableHeadList.add(tableHead);
        });

        return tableHeadList;
    }

    @Override
    public List<TableRowBean> selectQcLineRowList(RestJsonWrapperBean wrapper) {
        EmcProjectQcBean projectQc = wrapper.parseUnique(EmcProjectQcBean.class);
        List<EmcProjectQcBean> projectQcList = selectListByFilter(SearchFilter.instance().match("PROJECTID", projectQc.getProjectId()).filter(MatchPattern.EQ).match("MONITORTMPLID", projectQc.getMonitorTmplId()).filter(MatchPattern.EQ));
        Map<Long, EmcProjectQcBean> projectQcMap = projectQcList.stream().collect(Collectors.toMap(EmcProjectQcBean::getId, Function.identity()));

        List<Long> projectQcIdList = projectQcList.stream().map(EmcProjectQcBean::getId).collect(Collectors.toList());
        List<EmcProjectQcLineBean> qcLineList = projectQcLineService.getDao().selectListByOneColumnValues(projectQcIdList, "QCID");

        List<QcLineResultDTO> dataList = new ArrayList<>();
        qcLineList.forEach(line -> {
            QcLineResultDTO resultDTO = new QcLineResultDTO();
            resultDTO.setId(line.getId());
            resultDTO.setColId(line.getQcCategoryId() + "");
            resultDTO.setColVal(line.getQcQty() == null ? "" : line.getQcQty() + "");
            Optional.ofNullable(projectQcMap.get(line.getQcId())).ifPresent(qc -> resultDTO.setRowNo(qc.getTestId() + ""));
            dataList.add(resultDTO);
        });

        TableCellDescriptor<QcLineResultDTO> cellDescriptor = new TableCellDescriptor<>("colId", "colVal");
        cellDescriptor.addPivot("rowNo");

        List<TableRowBean> tableRowList = TableHelper.parseTableRowList(dataList, cellDescriptor);
        tableRowList.forEach(row -> {
            String testId = row.getPivotValue("rowNo");
            EmcProjectQcBean currentProjectQc = projectQcList.stream().filter(qc -> StringUtils.equals(testId, qc.getTestId() + "")).findFirst().orElse(null);
            if (currentProjectQc == null) {
                return;
            }

            row.put("testName", currentProjectQc.getTestName());
            row.put("totalQty", currentProjectQc.getTotalQty());
            row.put("testId", currentProjectQc.getTestId());
        });

        tableRowList.sort(Comparator.comparing(r -> NumberUtils.parseInt(Optional.ofNullable(r.get("testId")).orElse("1"))));

        return tableRowList;
    }

    private void createSamplingProjectQcLine(List<EmcProjSchemeBean> projectSchemeList, List<EmcProjOrderBean> schemeOrderList, List<EmcQcCategoryBean> qcCategoryList, List<EmcProjectQcBean> projectQcList, List<EmcProjectQcLineBean> projectQcLineList) {
        // 按样品类型对监测方案分组
        Map<String, List<EmcProjSchemeBean>> orderCategoryMap = projectSchemeList.stream().collect(Collectors.groupingBy(EmcProjSchemeBean::getMonitorTmplId));

        orderCategoryMap.forEach((monitorTmplId, schemeList) -> {
            List<Long> schemeIdList = schemeList.stream().map(EmcProjSchemeBean::getId).collect(Collectors.toList());
            // 按测试进行分组
            Map<Long, List<EmcProjOrderBean>> testSchemeOrderMap = schemeOrderList.stream().filter(order -> schemeIdList.contains(order.getProjSchemeId())).collect(Collectors.groupingBy(EmcProjOrderBean::getTestId));
            testSchemeOrderMap.forEach((testId, testSchemeOrderList) -> {
                EmcProjectQcBean projectQc = new EmcProjectQcBean();
                projectQc.setId(ApplicationContextHelper.getNextIdentity());
                projectQc.setProjectId(schemeList.get(0).getProjId());
                projectQc.setMonitorTmplId(schemeList.get(0).getMonitorTmplId());
                projectQc.setOrderCategory(schemeList.get(0).getExt$Item("monitortmplname"));
                projectQc.setTestId(testId);
                projectQc.setTestName(testSchemeOrderList.get(0).getExt$Item("testname"));

                LongStream.Builder totalQtyBuilder = LongStream.builder();
                testSchemeOrderList.forEach(testSchemeOrder -> schemeList.stream().filter(scheme -> ObjectUtils.equals(testSchemeOrder.getProjSchemeId(), scheme.getId())).findFirst().ifPresent(scheme -> {
                    long totalQty = scheme.getCycle() * scheme.getFrequency() * testSchemeOrder.getSampleNum();
                    totalQtyBuilder.add(totalQty);
                }));
                long totalQty = totalQtyBuilder.build().sum();
                projectQc.setTotalQty(totalQty);
                projectQcList.add(projectQc);

                qcCategoryList.forEach(qcCategory -> {
                    EmcProjectQcLineBean qcLine = new EmcProjectQcLineBean();
                    qcLine.setId(ApplicationContextHelper.getNextIdentity());
                    qcLine.setQcCategoryId(qcCategory.getId());
                    qcLine.setQcCategoryCode(qcCategory.getQcCategoryId());
                    qcLine.setQcName(qcCategory.getQcName());
                    qcLine.setQcId(projectQc.getId());
                    qcLine.setQcQty(0);
                    projectQcLineList.add(qcLine);
                });
            });
        });
    }

    private void createNoSamplingProjectQcLine(List<EmcOrderBean> orderList, List<EmcOrderItemGroupBean> orderItemGroupList, List<EmcQcCategoryBean> qcCategoryList, List<EmcProjectQcBean> projectQcList, List<EmcProjectQcLineBean> projectQcLineList) {
        Map<String, List<EmcOrderBean>> orderCategoryMap = orderList.stream().collect(Collectors.groupingBy(EmcOrderBean::getMonitorTmplId));
        orderCategoryMap.forEach((monitorTmplId, tmplOrderList) -> {
            List<Long> tmplOrderIdList = tmplOrderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList());
            Map<Long, List<EmcOrderItemGroupBean>> testItemGroupMap = orderItemGroupList.stream().filter(group -> tmplOrderIdList.contains(group.getOrderId())).collect(Collectors.groupingBy(EmcOrderItemGroupBean::getTestId));
            testItemGroupMap.forEach((testId, testItemGroupList) -> {
                EmcProjectQcBean projectQc = new EmcProjectQcBean();
                projectQc.setId(ApplicationContextHelper.getNextIdentity());
                projectQc.setProjectId(tmplOrderList.get(0).getProjId());
                projectQc.setMonitorTmplId(monitorTmplId);
                projectQc.setOrderCategory(tmplOrderList.get(0).getOrderCategory());
                projectQc.setTestId(testId);
                projectQc.setTestName(testItemGroupList.get(0).getTestName());
                projectQc.setTotalQty(Integer.valueOf(testItemGroupList.size()).longValue());
                projectQcList.add(projectQc);

                qcCategoryList.forEach(qcCategory -> {
                    EmcProjectQcLineBean qcLine = new EmcProjectQcLineBean();
                    qcLine.setId(ApplicationContextHelper.getNextIdentity());
                    qcLine.setQcCategoryId(qcCategory.getId());
                    qcLine.setQcCategoryCode(qcCategory.getQcCategoryId());
                    qcLine.setQcName(qcCategory.getQcName());
                    qcLine.setQcId(projectQc.getId());
                    qcLine.setQcQty(0);
                    projectQcLineList.add(qcLine);
                });
            });
        });
    }

}
