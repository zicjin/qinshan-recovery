package com.sunwayworld.emc.module.bp.project.projscheme.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.framework.constant.EmcBizConstant;
import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.emc.framework.constant.PfBizConstant;
import com.sunwayworld.emc.framework.constant.PfConstant;
import com.sunwayworld.emc.framework.utils.EmcNumberUtils;
import com.sunwayworld.emc.module.bp.bp.bean.EmcFolderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskResultBean;
import com.sunwayworld.emc.module.bp.bp.service.EmcFolderService;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderContainerService;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderService;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskResultService;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskService;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcOrderItemBean;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcOrderItemGroupBean;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcProjectBean;
import com.sunwayworld.emc.module.bp.project.project.service.EmcOrderItemGroupService;
import com.sunwayworld.emc.module.bp.project.project.service.EmcOrderItemService;
import com.sunwayworld.emc.module.bp.project.project.service.EmcProjectService;
import com.sunwayworld.emc.module.bp.project.projectlog.bean.EmcProjectEditLogBean;
import com.sunwayworld.emc.module.bp.project.projectlog.service.EmcProjectEditLogService;
import com.sunwayworld.emc.module.bp.project.projscheme.bean.EmcProjOrderBean;
import com.sunwayworld.emc.module.bp.project.projscheme.bean.EmcProjSchemeBean;
import com.sunwayworld.emc.module.bp.project.projscheme.service.EmcProjOrderService;
import com.sunwayworld.emc.module.bp.project.projscheme.service.PfProjSchemeService;
import com.sunwayworld.emc.module.bp.project.projscheme.validator.PfDeleteFolderValidator;
import com.sunwayworld.emc.module.bp.project.projscheme.validator.PfDeleteOrderValidator;
import com.sunwayworld.emc.module.bp.project.projscheme.validator.PfDeleteTestValidator;
import com.sunwayworld.emc.module.bp.project.projscheme.validator.PfSyncFolderFromSchemeValidator;
import com.sunwayworld.emc.module.bp.project.projscheme.validator.PfSyncNewFolderValidator;
import com.sunwayworld.emc.module.bp.project.projscheme.validator.PfSyncNewTestValidator;
import com.sunwayworld.emc.module.bp.project.projscheme.validator.PfSyncTestFromSchemeValidator;
import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportBean;
import com.sunwayworld.emc.module.bp.report.report.service.EmcReportService;
import com.sunwayworld.emc.module.mdm.basemdm.indexitem.bean.EmcIndexTestItemBean;
import com.sunwayworld.emc.module.mdm.basemdm.indexitem.service.EmcIndexTestItemService;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.bean.EmcPropertyDataBean;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.service.EmcPropertyDataService;
import com.sunwayworld.emc.module.mdm.bizmdm.capacity.bean.EmcCapacityItemBean;
import com.sunwayworld.emc.module.mdm.bizmdm.capacity.bean.EmcCapacityItemGroupBean;
import com.sunwayworld.emc.module.mdm.bizmdm.capacity.bean.EmcTestCapacityBean;
import com.sunwayworld.emc.module.mdm.bizmdm.capacity.service.EmcCapacityItemGroupService;
import com.sunwayworld.emc.module.mdm.bizmdm.capacity.service.EmcCapacityItemService;
import com.sunwayworld.emc.module.mdm.bizmdm.capacity.service.EmcTestCapacityService;
import com.sunwayworld.emc.module.mdm.bizmdm.client.bean.EmcMonitorPointBean;
import com.sunwayworld.emc.module.mdm.bizmdm.client.service.EmcMonitorPointService;
import com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.bean.EmcIndexSetBean;
import com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.service.EmcIndexSetService;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.bean.EmcMonitorTmplBean;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.service.EmcMonitorTmplService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcGroupItemBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcItemGroupBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcRelatedTestMethodBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcTestBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcTestMethodBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcTestMethodOrderCategoryBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcGroupItemService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcItemGroupService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcRelatedTestMethodService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcTestMethodOrderCategoryService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcTestMethodService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcTestService;
import com.sunwayworld.emc.module.mdm.crm.service.CrmOpportunityContractService;
import com.sunwayworld.emc.module.mdm.crm.service.CrmOpportunityMainService;
import com.sunwayworld.emc.module.mdm.crm.service.CrmOpportunityOrdersService;
import com.sunwayworld.emc.module.mdm.crm.service.CrmOpportunityOrdtaskService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.auditable.bean.CoreBpmnParameterDTO;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.DateTimeUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.framework.validator.data.annotation.ValidateDataWith;
import com.sunwayworld.module.mdm.org.bean.CoreOrgBean;
import com.sunwayworld.module.mdm.org.service.CoreOrgService;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;

/**
 * @author zhengxy@sunwayworld.com 2021年06月01日
 */
@Repository
@GikamBean
public class PfProjSchemeServiceImpl extends EmcProjSchemeServiceImpl implements PfProjSchemeService {

    @Autowired
    @Lazy
    private EmcProjectService projectService;

    @Autowired
    private EmcGroupItemService groupItemService;

    @Autowired
    private EmcProjOrderService projOrderService;

    @Autowired
    private EmcMonitorPointService monitorPointService;

    @Autowired
    private EmcTestMethodService testMethodService;

    @Autowired
    private EmcTestMethodOrderCategoryService testMethodOrderCategoryService;

    @Autowired
    private EmcOrderItemGroupService orderItemGroupService;

    @Autowired
    private EmcIndexSetService indexSetService;

    @Autowired
    private EmcIndexTestItemService indexTestItemService;

    @Autowired
    @Lazy
    private EmcFolderService folderService;

    @Autowired
    private EmcOrderService orderService;

    @Autowired
    private EmcOrderTaskService orderTaskService;

    @Autowired
    private EmcOrderTaskResultService orderTaskResultService;

    @Autowired
    private EmcCapacityItemGroupService capacityItemGroupService;

    @Autowired
    private EmcItemGroupService itemGroupService;

    @Autowired
    private EmcTestCapacityService testCapacityService;

    @Autowired
    private EmcMonitorTmplService monitorTmplService;

    @Autowired
    private EmcPropertyDataService propertyDataService;

    @Autowired
    private CoreOrgService coreOrgService;

    @Autowired
    private EmcRelatedTestMethodService relatedTestMethodService;

    @Autowired
    @Lazy
    private EmcReportService reportService;

    @Autowired
    @Lazy
    private EmcOrderItemService orderItemService;

    @Autowired
    @Lazy
    private EmcTestService testService;

    @Autowired
    @Lazy
    private EmcProjectEditLogService projectEditLogService;

    @Autowired
    private CrmOpportunityMainService opportunityMainService;

    @Autowired
    private CrmOpportunityContractService opportunityContractService;

    @Autowired
    private CrmOpportunityOrdersService opportunityOrdersService;

    @Autowired
    private CrmOpportunityOrdtaskService opportunityOrdtaskService;

    @Autowired
    private EmcCapacityItemService capacityItemService;

    @Autowired
    private EmcOrderContainerService orderContainerService;

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        int i;
        List<EmcProjSchemeBean> schemeList = jsonWrapper.parse(EmcProjSchemeBean.class);
        List<EmcProjOrderBean> orderList = jsonWrapper.parse(EmcProjOrderBean.class);
        int qty = schemeList.size();
        Long projId = ((EmcProjSchemeBean)schemeList.get(0)).getProjId();
        EmcProjectBean emcProject = (EmcProjectBean)this.projectService.selectById(projId);
        List<Long> monitorPointIdList = schemeList.stream().filter(e -> !ObjectUtils.isEmpty(e.getMonitorPointId())).map(EmcProjSchemeBean::getMonitorPointId).distinct().collect(Collectors.toList());
        List<EmcMonitorPointBean> monitorPointList = Collections.emptyList();
        if (!monitorPointIdList.isEmpty()) {
            monitorPointList = this.monitorPointService.selectListByFilter(SearchFilter.instance().match("ID", monitorPointIdList).filter(MatchPattern.OR), new Order[0]).stream().filter(e -> !ObjectUtils.isEmpty(e.getIndexId())).collect(Collectors.toList());
        }
        String monitorTmplId = ((EmcProjSchemeBean)schemeList.get(0)).getMonitorTmplId();
        EmcMonitorTmplBean monitorTmpl = (EmcMonitorTmplBean)this.monitorTmplService.selectById((monitorTmplId));
        String orderCategoryCode = "";
        Long cycle = EmcConstant.ONE;
        Long frequency = EmcConstant.ONE;
        if (!ObjectUtils.isEmpty(monitorTmpl)) {
            orderCategoryCode = ObjectUtils.isEmpty(monitorTmpl.getExt$Item("ordercategorycode")) ? "" : monitorTmpl.getExt$Item("ordercategorycode");
            cycle = ObjectUtils.isEmpty(monitorTmpl.getCycle()) ? EmcConstant.ONE : monitorTmpl.getCycle();
            frequency = ObjectUtils.isEmpty(monitorTmpl.getFrequency()) ? EmcConstant.ONE : monitorTmpl.getFrequency();
        }
        List<Long> capacityItemGroupIdList = orderList.stream().map(EmcProjOrderBean::getItemGroupId).distinct().collect(Collectors.toList());
        List<EmcCapacityItemGroupBean> capacityItemGroupList = this.capacityItemGroupService.selectListByIds(capacityItemGroupIdList);
        List<Long> testMethodIdList = capacityItemGroupList.stream().map(EmcCapacityItemGroupBean::getTestMethodId).distinct().collect(Collectors.toList());
        List<EmcRelatedTestMethodBean> relatedTestMethodList = this.relatedTestMethodService.selectListByFilter(SearchFilter.instance().match("testMethodId", testMethodIdList).filter(MatchPattern.OR).match("replaceFlag2", Arrays.asList("1", null)).filter(MatchPattern.DIFFER), new Order[0]);
        List<Long> relatedTestMethodIdList = relatedTestMethodList.stream().map(EmcRelatedTestMethodBean::getRelatedTestMethodId).distinct().collect(Collectors.toList());
        List<EmcTestMethodBean> testMethodList = this.testMethodService.selectListByIds(relatedTestMethodIdList);
        String parentOrgId = LocalContextHelper.getLoginUser().getExt$Item("parentOrgId");
        List<EmcTestCapacityBean> relatedCapacityList = relatedTestMethodIdList.isEmpty() ? Collections.emptyList() : this.testCapacityService.selectListByFilter(SearchFilter.instance().match("testMethodId", relatedTestMethodIdList).filter(MatchPattern.OR).match("orgId", parentOrgId).filter(MatchPattern.SEQ), new Order[0]);
        List<Long> testCapacityIdList = relatedCapacityList.stream().map(EmcTestCapacityBean::getId).collect(Collectors.toList());
        List<EmcCapacityItemGroupBean> relatedItemGroupList = this.capacityItemGroupService.getDao().selectListByOneColumnValues(testCapacityIdList, "testCapacityId", new Order[0]);
        relatedItemGroupList.stream().filter(related -> !capacityItemGroupIdList.contains(related.getId())).forEach(related -> {
            EmcProjOrderBean relatedProjOrder = new EmcProjOrderBean();
            relatedProjOrder.setItemGroup(related.getItemGroup());
            relatedProjOrder.setItemGroupId(related.getId());
            relatedProjOrder.setSchemeName("default");
            relatedProjOrder.setExt$Item("relatedTest", "1");
            testMethodList.stream().filter(testMethod -> ObjectUtils.equals(related.getTestMethodId(), testMethod.getId())).findFirst().ifPresent(testMethod -> {
                relatedProjOrder.setMethodId(testMethod.getMethodId());
                relatedProjOrder.setTestId(testMethod.getTestId());
            });
            orderList.add(relatedProjOrder);
        });
        List<String> folderNoList = this.generateFolderNosByProjId(emcProject.getId(), emcProject.getProjNo(), monitorTmpl.getId(), orderCategoryCode, qty);
        ArrayList<EmcProjOrderBean> insertOrderList = new ArrayList<>();
        for (i = 0; i < qty; ++i) {
            EmcProjSchemeBean scheme = (EmcProjSchemeBean)schemeList.get(i);
            Long monitorPointId = scheme.getMonitorPointId();
            Long id = ApplicationContextHelper.getNextIdentity();
            scheme.setId(id);
            scheme.setFolderNo(folderNoList.get(i));
            scheme.setSortNum(Long.valueOf(Long.parseLong((folderNoList.get(i)).substring((folderNoList.get(i)).length() - 3))));
            scheme.setCycle(cycle);
            scheme.setFrequency(frequency);
            if (StringUtils.isEmpty(scheme.getSampleOrgId())) {
                scheme.setSampleOrgId(emcProject.getProjectSampleOrgId());
            }
            List<EmcProjOrderBean> copyOrderList = (List<EmcProjOrderBean>)ObjectUtils.clone(orderList);
            for (EmcProjOrderBean order : copyOrderList) {
                order.setId(ApplicationContextHelper.getNextIdentity());
                order.setProjSchemeId(id);
                order.setSampleNum(EmcConstant.ONE);
                if (monitorPointId != null) {
                    EmcMonitorPointBean monitorPoint = monitorPointList.stream()
                            .filter(e -> ObjectUtils.equals(monitorPointId, e.getId())).findFirst().orElse(null);
                    if (monitorPoint != null) {
                        order.setIndexCategoryId(NumberUtils.parseLong(monitorPoint.getExt$Item("indexcategoryid")));
                        order.setIndexId(monitorPoint.getIndexId());
                    }
                }
            }
            insertOrderList.addAll(copyOrderList);
        }
        for (i = 0; i < insertOrderList.size(); ++i) {
            ((EmcProjOrderBean)insertOrderList.get(i)).setSortNum(Long.valueOf((long)i + 1L));
        }
        this.getDao().insert(schemeList);
        this.projOrderService.getDao().insert(insertOrderList);
        return ((EmcProjSchemeBean)schemeList.get(0)).getId();
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void updateProjOrderItemGroup(RestJsonWrapperBean wrapper) {
        List<EmcProjSchemeBean> schemeList = wrapper.parse(EmcProjSchemeBean.class);
        List<EmcProjOrderBean> orderList = wrapper.parse(EmcProjOrderBean.class);
        List<Long> schemeIdList = schemeList.stream().map(EmcProjSchemeBean::getId).collect(Collectors.toList());
        List<EmcProjOrderBean> originalOrderList = projOrderService.getDao().selectListByOneColumnValues(schemeIdList, "PROJSCHEMEID");

        // 删除不需要的测试
        List<Long> itemGroupIdList = orderList.stream().map(EmcProjOrderBean::getItemGroupId).collect(Collectors.toList());
        List<EmcProjOrderBean> deleteOrderList = originalOrderList.stream().filter(o -> !itemGroupIdList.contains(o.getItemGroupId())).collect(Collectors.toList());

        // 已存在的测试
        List<EmcProjOrderBean> existOrderList = originalOrderList.stream().filter(o -> itemGroupIdList.contains(o.getItemGroupId())).collect(Collectors.toList());

        // 需要新增的测试
        List<Long> originalItemGroupIdList = originalOrderList.stream().map(EmcProjOrderBean::getItemGroupId).collect(Collectors.toList());
        List<EmcProjOrderBean> newOrderList = orderList.stream().filter(o -> !originalItemGroupIdList.contains(o.getItemGroupId())).collect(Collectors.toList());

        boolean schemeEdit = StringUtils.equals(orderList.get(0).getExt$Item("source"), "schemeEdit");

        if (!newOrderList.isEmpty()) {
            // 查找新增测试的关联测试
            List<Long> capacityItemGroupIdList = newOrderList.stream().map(EmcProjOrderBean::getItemGroupId).distinct().collect(Collectors.toList());
            List<EmcCapacityItemGroupBean> capacityItemGroupList = capacityItemGroupService.selectListByIds(capacityItemGroupIdList);

            List<Long> testMethodIdList = capacityItemGroupList.stream().map(EmcCapacityItemGroupBean::getTestMethodId).distinct().collect(Collectors.toList());
            List<EmcRelatedTestMethodBean> relatedTestMethodList = relatedTestMethodService.selectListByFilter(SearchFilter.instance()
                    .match("testMethodId", testMethodIdList).filter(MatchPattern.OR)
                    .match("replaceFlag2", Arrays.asList("1", null)).filter(MatchPattern.DIFFER));
            if (!relatedTestMethodList.isEmpty()) {
                List<Long> relatedTestMethodIdList = relatedTestMethodList.stream().map(EmcRelatedTestMethodBean::getRelatedTestMethodId).distinct().collect(Collectors.toList());
                List<EmcTestMethodBean> testMethodList = testMethodService.selectListByIds(relatedTestMethodIdList);

                String parentOrgId = LocalContextHelper.getLoginUser().getExt$Item("parentOrgId");
                List<EmcTestCapacityBean> relatedCapacityList = testCapacityService.selectListByFilter(SearchFilter.instance().match("testMethodId", relatedTestMethodIdList).filter(MatchPattern.OR).match("orgId", parentOrgId).filter(MatchPattern.SEQ));
                List<Long> testCapacityIdList = relatedCapacityList.stream().map(EmcTestCapacityBean::getId).collect(Collectors.toList());

                List<EmcCapacityItemGroupBean> relatedItemGroupList = capacityItemGroupService.getDao().selectListByOneColumnValues(testCapacityIdList, "testCapacityId");

                relatedItemGroupList.stream().filter(related -> !capacityItemGroupIdList.contains(related.getId()) && !originalItemGroupIdList.contains(related.getId())).forEach(related -> {
                    EmcProjOrderBean relatedProjOrder = new EmcProjOrderBean();
                    relatedProjOrder.setItemGroup(related.getItemGroup());
                    relatedProjOrder.setItemGroupId(related.getId());
                    relatedProjOrder.setSchemeName("default");
                    relatedProjOrder.setExt$Item("relatedTest", Constant.YES);
                    testMethodList.stream().filter(testMethod -> ObjectUtils.equals(related.getTestMethodId(), testMethod.getId())).findFirst().ifPresent(testMethod -> {
                        relatedProjOrder.setMethodId(testMethod.getMethodId());
                        relatedProjOrder.setTestId(testMethod.getTestId());
                    });

                    if (schemeEdit) {
                        relatedProjOrder.setExt$Item("status", Constant.YES);
                        relatedProjOrder.setExt$Item("source", "schemeEdit");
                    }

                    newOrderList.add(relatedProjOrder);
                });
            }
        }

        projOrderService.getDao().deleteBy(deleteOrderList);

        if (!newOrderList.isEmpty()) {
            List<EmcProjOrderBean> insertOrderList = new ArrayList<>();
            for (EmcProjSchemeBean scheme : schemeList) {
                List<EmcProjOrderBean> copyOrderList = ObjectUtils.clone(newOrderList);
                copyOrderList.forEach(e -> {
                    e.setId(ApplicationContextHelper.getNextIdentity());
                    e.setProjSchemeId(scheme.getId());
                    e.setSampleNum(EmcConstant.ONE);
                });
                insertOrderList.addAll(copyOrderList);
            }
            if (!ObjectUtils.isEmpty(existOrderList)) {
                Long maxSortNum = existOrderList.stream().filter(e -> !ObjectUtils.isEmpty(e.getSortNum()))
                        .map(EmcProjOrderBean::getSortNum).max(Comparator.naturalOrder()).orElse(0L);
                for (int i = 0; i < insertOrderList.size(); i++) {
                    insertOrderList.get(i).setSortNum(i + 1L + maxSortNum);
                }
            }

            projOrderService.getDao().insert(insertOrderList);
        }
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void copy(RestJsonWrapperBean wrapper) {
        String copies = wrapper.getParamValue("copies");
        if (!NumberUtils.isNumber(copies) || NumberUtils.parseInt(copies) < 1) {
            return;
        }

        List<EmcProjSchemeBean> copySchemeList = wrapper.parse(EmcProjSchemeBean.class);
        List<Long> projSchemeIdList = copySchemeList.stream().map(EmcProjSchemeBean::getId).collect(Collectors.toList());
        List<EmcProjOrderBean> copyOrderList = projOrderService.getDao().selectListByOneColumnValues(projSchemeIdList, "PROJSCHEMEID");

        List<String> monitorTmplIdList = copySchemeList.stream().map(EmcProjSchemeBean::getMonitorTmplId).distinct().collect(Collectors.toList());
        List<EmcMonitorTmplBean> monitorTmplList = monitorTmplService.getDao().selectListByOneColumnValues(monitorTmplIdList, "id", Arrays.asList("id", "orderCategoryCode"));
        Map<String, String> orderCategoryCodeMap = monitorTmplList.stream().collect(Collectors.toMap(EmcMonitorTmplBean::getId, tmpl -> Optional.ofNullable(tmpl.getExt$Item("orderCategoryCode")).orElse("O")));

        EmcProjectBean project = projectService.selectById(copySchemeList.get(0).getProjId());

        List<EmcProjSchemeBean> insertSchemeList = new ArrayList<>();
        List<EmcProjOrderBean> insertOrderList = new ArrayList<>();

        int copyNum = NumberUtils.parseInt(copies);
        Map<String, List<EmcProjSchemeBean>> schemeMap = copySchemeList.stream().collect(Collectors.groupingBy(EmcProjSchemeBean::getMonitorTmplId));
        schemeMap.forEach((monitorTmplId, schemeList) -> {
            List<String> newFolderNoList = generateFolderNosByProjId(project.getId(), project.getProjNo(), monitorTmplId,
                    schemeList.get(0).getExt$Item("ordercategorycode"), schemeList.size() * copyNum);
            AtomicInteger folderNoIndex = new AtomicInteger();
            IntStream.range(0, schemeList.size()).forEach(index -> {
                IntStream.range(0, copyNum).forEach(num -> {
                    EmcProjSchemeBean newScheme = new EmcProjSchemeBean();
                    BeanUtils.copyProperties(schemeList.get(index), newScheme, EmcConstant.CREATED_COLUMNS);
                    newScheme.setId(ApplicationContextHelper.getNextIdentity());
                    String folderNo = newFolderNoList.get(folderNoIndex.getAndIncrement());
                    newScheme.setFolderNo(folderNo);
                    newScheme.setSortNum(Long.parseLong(folderNo.substring(folderNo.length() - 3)));
                    List<EmcProjOrderBean> curOrderList = copyOrderList.stream().filter(order -> ObjectUtils.equals(order.getProjSchemeId(), schemeList.get(index).getId())).collect(Collectors.toList());
                    List<EmcProjOrderBean> newOrderList = ObjectUtils.clone(curOrderList);
                    newOrderList.forEach(order -> {
                        order.setId(ApplicationContextHelper.getNextIdentity());
                        order.setProjSchemeId(newScheme.getId());
                    });
                    insertSchemeList.add(newScheme);
                    insertOrderList.addAll(newOrderList);
                });
            });
        });

        getDao().insert(insertSchemeList);
        projOrderService.getDao().insert(insertOrderList);
    }

    @Override
    public void insertBpData(List<Long> projectIdList) {
        List<EmcProjSchemeBean> schemeList = this.selectListByFilter(SearchFilter.instance()
                .match("PROJID", projectIdList).filter(MatchPattern.OR)
                .match("REFERFOLDER", Arrays.asList("0", null)).filter(MatchPattern.OR));
        if (schemeList.isEmpty()) {
            return;
        }

        this.insertBpDataByScheme(schemeList);
    }

    @Override
    public void insertBpDataByScheme(List<EmcProjSchemeBean> schemeList) {
        // 监测方案样品
        List<Long> schemeIdList = schemeList.stream().map(EmcProjSchemeBean::getId).collect(Collectors.toList());
        List<EmcProjOrderBean> orderList = projOrderService.selectListByFilter(SearchFilter.instance().match("PROJSCHEMEID", schemeIdList).filter(MatchPattern.OR).match("SAMPLEPARAM","1").filter(MatchPattern.DIFFER));
        // 分析项组分析项
        List<Long> capacityItemGroupIdList = orderList.stream().map(EmcProjOrderBean::getItemGroupId).collect(Collectors.toList());
        List<EmcCapacityItemGroupBean> capacityItemGroupList = capacityItemGroupService.getDao().selectListByIds(capacityItemGroupIdList);
        List<EmcItemGroupBean> filterItemGroupList = new ArrayList<>();
        capacityItemGroupList.forEach(group -> {
            EmcItemGroupBean itemGroup = new EmcItemGroupBean();
            itemGroup.setTestMethodId(group.getTestMethodId());
            itemGroup.setItemGroup(group.getItemGroup());
            filterItemGroupList.add(itemGroup);
        });
        List<EmcItemGroupBean> itemGroupList = itemGroupService.getDao().selectList(filterItemGroupList, Arrays.asList("testMethodId", "itemGroup"), Arrays.asList("id", "testMethodId", "itemGroup"));
        List<Long> itemGroupIdList = itemGroupList.stream().map(EmcItemGroupBean::getId).collect(Collectors.toList());
        itemGroupList.forEach(group -> capacityItemGroupList.stream().filter(g -> ObjectUtils.equals(g.getTestMethodId(), group.getTestMethodId()) && StringUtils.equals(g.getItemGroup(), group.getItemGroup())).findFirst().ifPresent(g -> group.setExt$Item("capacityItemGroupId", g.getId() + "")));

        List<EmcGroupItemBean> itemList = groupItemService.selectListByFilter(SearchFilter.instance().match("ITEMGROUPID", itemGroupIdList).filter(MatchPattern.OR));
        itemList.forEach(item -> itemGroupList.stream().filter(group -> ObjectUtils.equals(item.getItemGroupId(), group.getId())).findFirst().ifPresent(group -> item.setExt$Item("capacityItemGroupId", group.getExt$Item("capacityItemGroupId"))));

        List<Long> capacityIdList = capacityItemGroupList.stream().map(EmcCapacityItemGroupBean::getTestCapacityId).distinct().collect(Collectors.toList());
        List<EmcTestCapacityBean> testCapacityList = testCapacityService.getDao().selectListByIds(capacityIdList);

        // 监测点
        List<Long> monitorPointIdList = schemeList.stream().map(e -> e.getMonitorPointId()).filter(e -> !ObjectUtils.isEmpty(e)).collect(Collectors.toList());
        List<EmcMonitorPointBean> pointList = monitorPointService.selectListByIds(monitorPointIdList);
        // 测试方法
        List<Long> testIdList = orderList.stream().map(e -> e.getTestId()).collect(Collectors.toList());
        List<EmcTestMethodBean> testMethodList = testMethodService.selectListByFilter(SearchFilter.instance().match("TESTID", testIdList).filter(MatchPattern.OR).match("SAMPLEPARAM","1").filter(MatchPattern.DIFFER));
        // 测试样品类型
        //后台提交LocalContextHelper找不到用户
/*        CoreUserBean user = LocalContextHelper.getLoginUser();
        //赋值所属单位和流程类型
        String parentOrgId = user.getExt$Item("parentorgid");*/
        EmcProjectBean project = projectService.selectById(schemeList.get(0).getProjId());
        String parentOrgId = project.getOrgId();
        List<Long> testMethodIdList = testMethodList.stream().map(e -> e.getId()).collect(Collectors.toList());
        List<EmcTestMethodOrderCategoryBean> testOrderList = testMethodOrderCategoryService
                .selectListByFilter(SearchFilter.instance().match("TESTMETHODID", testMethodIdList).filter(MatchPattern.OR)
                		.match("ORGID", parentOrgId).filter(MatchPattern.OR));
        // 指标项目
        List<String> indexIdList = orderList.stream().map(e -> e.getIndexId()).distinct().collect(Collectors.toList());
        List<EmcIndexSetBean> indexSetList = indexSetService.selectListByFilter(SearchFilter.instance().match("INDEXID", indexIdList).filter(MatchPattern.OR));
        List<Long> indexItemIdList = indexSetList.stream().map(e -> e.getIndexItemId()).distinct().collect(Collectors.toList());
        List<EmcIndexTestItemBean> indexTestItemList = Collections.emptyList();
        if (!indexItemIdList.isEmpty()) {
            indexTestItemList = indexTestItemService.selectListByFilter(SearchFilter.instance().match("INDEXITEMID", indexItemIdList).filter(MatchPattern.OR));
        }

        List<EmcFolderBean> insertFolderList = new ArrayList<>();// 点位
        List<EmcOrderBean> insertOrderList = new ArrayList<>();// 样品
        List<EmcOrderTaskBean> insertOrderTaskList = new ArrayList<>();// 样品测试
        List<EmcOrderTaskResultBean> insertOrderTaskResultList = new ArrayList<>();// 样品结果
        List<EmcOrderItemGroupBean> insertOrderItemGroupList = new ArrayList<>();// 样品分析项组

        for (EmcProjSchemeBean projScheme : schemeList) {
            List<EmcProjOrderBean> projOrderList = orderList.stream().filter(order -> ObjectUtils.equals(order.getProjSchemeId(), projScheme.getId())).collect(Collectors.toList());
            this.generateBpData(projScheme, projOrderList, itemList, pointList, testMethodList, testOrderList, indexSetList, indexTestItemList, insertFolderList, insertOrderList,
                    insertOrderTaskList, insertOrderTaskResultList, insertOrderItemGroupList, testCapacityList);
        }

        List<EmcPropertyDataBean> folderPropertyList = createFolderPropertyList(insertFolderList);

        // 同步子库分析项上的检出限和修约规则到结果表上
        List<Long> itemIdList = insertOrderTaskResultList.stream().map(EmcOrderTaskResultBean::getItemId).distinct().collect(Collectors.toList());
        List<EmcCapacityItemBean> capacityItemList = capacityItemService.selectListByFilter(SearchFilter.instance().match("itemId", itemIdList).filter(MatchPattern.OR).match("orgId", parentOrgId).filter(MatchPattern.SEQ));
        capacityItemList.forEach(ci -> insertOrderTaskResultList.stream().filter(r -> ObjectUtils.equals(ci.getItemId(), r.getItemId())).forEach(r -> {
            r.setOriginalResult(ci.getDefaultResult());
            r.setLowLimit(ci.getLowLimit());
            r.setRevisionRule(ci.getRevisionRule());
            r.setExt$Item("LIMITUNITNAME", ci.getExt$Item("LIMITUNITNAME"));
        }));

        // 生成样品容器
        orderContainerService.createOrderContainer(insertOrderList, insertOrderTaskList);

        folderService.getDao().insert(insertFolderList);
        orderService.getDao().insert(insertOrderList);
        orderTaskService.getDao().insert(insertOrderTaskList);
        orderTaskResultService.getDao().insert(insertOrderTaskResultList);
        orderItemGroupService.getDao().insert(insertOrderItemGroupList);
        propertyDataService.getDao().insert(folderPropertyList);

        //生成气采样参数主业务数据
        insertSampleParamData(schemeList);
    }

    /**
     * 通过监测方案和监测方案样品生成主业务流程数据
     */
    private void generateBpData(EmcProjSchemeBean projScheme, List<EmcProjOrderBean> projOrderList, List<EmcGroupItemBean> itemList, List<EmcMonitorPointBean> pointList,
                                List<EmcTestMethodBean> testMethodList, List<EmcTestMethodOrderCategoryBean> testOrderList, List<EmcIndexSetBean> indexSetList, List<EmcIndexTestItemBean> indexTestItemList,
                                List<EmcFolderBean> insertFolderList, List<EmcOrderBean> insertOrderList, List<EmcOrderTaskBean> insertOrderTaskList, List<EmcOrderTaskResultBean> insertOrderTaskResultList,
                                List<EmcOrderItemGroupBean> insertOrderItemGroupList, List<EmcTestCapacityBean> testCapacityList) {
        // 生成点位
        EmcFolderBean folder = this.generateFolder(projScheme, pointList);
        insertFolderList.add(folder);
        // 生成样品
        List<EmcOrderBean> orderList = this.generateOrderList(folder, projScheme, projOrderList);
        insertOrderList.addAll(orderList);
        // 生成样品测试和生成样品结果
        List<EmcOrderTaskResultBean> orderTaskResultList = new ArrayList<>();
        List<EmcOrderTaskBean> orderTaskList = this.createOrderTaskList(projScheme, orderList, projOrderList, testMethodList, itemList, testCapacityList, testOrderList, orderTaskResultList, indexSetList,
                indexTestItemList);
        insertOrderTaskList.addAll(orderTaskList);
        insertOrderTaskResultList.addAll(orderTaskResultList);
        // 生成样品分析项组
        List<EmcOrderItemGroupBean> orderItemGroupList = this.generateOrderItemGroup(orderList, projOrderList, orderTaskList);
        insertOrderItemGroupList.addAll(orderItemGroupList);
    }

    /**
     * 根据方案样品过滤重复的分析项
     */
    @Override
    protected List<EmcGroupItemBean> filterItem(EmcProjOrderBean projOrder, List<EmcProjOrderBean> projOrderList, List<EmcGroupItemBean> itemList) {
        List<EmcProjOrderBean> filterProjOrderList = projOrderList.stream()
                .filter(f -> ObjectUtils.equals(projOrder.getTestId(), f.getTestId()) && ObjectUtils.equals(projOrder.getMethodId(), f.getMethodId())).collect(Collectors.toList());
        List<String> capacityItemGroupIdList = filterProjOrderList.stream().map(e -> e.getItemGroupId() + "").collect(Collectors.toList());

        List<EmcGroupItemBean> filterItemList = itemList.stream().filter(item -> capacityItemGroupIdList.contains(item.getExt$Item("capacityItemGroupId"))).collect(Collectors.toList());

        // 分析项组分析项按分析项分组
        return filterItemList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(EmcGroupItemBean::getItemId))), ArrayList::new));
    }

    /**
     * 生成样品数据
     */
    @Override
    protected List<EmcOrderBean> generateOrderList(EmcFolderBean folder, EmcProjSchemeBean projScheme, List<EmcProjOrderBean> projOrderList) {
        List<EmcOrderBean> list = new ArrayList<>();
        Long cycle = projScheme.getCycle();// 周期
        Long frequency = projScheme.getFrequency();// 频次
        long sampleNum = projOrderList.stream().mapToLong(EmcProjOrderBean::getSampleNum).max().orElse(1L);// 最大采样数

        int orderno = 1;
        for (int i = 1; i <= cycle; i++) {
            for (int j = 1; j <= frequency; j++) {
                for (int k = 1; k <= sampleNum; k++) {
                    EmcOrderBean order = new EmcOrderBean();
                    order.setId(ApplicationContextHelper.getNextIdentity());
                    order.setOrderNo(folder.getFolderNo() + StringUtils.leftPad("" + orderno, 3));// 生成样品编码
                    order.setLabelNo(order.getOrderNo());
                    order.setProjId(projScheme.getProjId());
                    order.setFolderId(folder.getId());
                    order.setCycle(0L + i);
                    order.setFrequency(0L + j);
                    order.setMonitorTmplId(projScheme.getMonitorTmplId());
                    order.setOrderCategory(projScheme.getExt$Item("monitortmplname"));
                    order.setSampleNum(0L + k);
                    order.setOrderNum(EmcConstant.ONE);
                    order.setRemark(projScheme.getRemark());
                    order.setTermination(Constant.NO);

                    list.add(order);
                    orderno++;
                }
            }
        }

        return list;
    }

    /**
     * 生成样品测试数据
     */
    private List<EmcOrderTaskBean> createOrderTaskList(EmcProjSchemeBean projScheme, List<EmcOrderBean> orderList, List<EmcProjOrderBean> projOrderList, List<EmcTestMethodBean> testMethodList, List<EmcGroupItemBean> itemList,
                                                       List<EmcTestCapacityBean> testCapacityList, List<EmcTestMethodOrderCategoryBean> testOrderList, List<EmcOrderTaskResultBean> orderTaskResultList, List<EmcIndexSetBean> indexSetList,
                                                       List<EmcIndexTestItemBean> indexTestItemList) {

        // 方案样品按测试和方法分组
        List<EmcProjOrderBean> groupProjOrderList = projOrderList.stream().collect(
                Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(o -> o.getTestId() + "#" + o.getMethodId()))), ArrayList::new));

        // 查询采集编号后缀信息
        Map<Long, String> acqNoSuffixMap = testMethodList.stream().filter(t -> !StringUtils.isEmpty(t.getAcqNoSuffix())).collect(Collectors.toMap(EmcTestMethodBean::getId, EmcTestMethodBean::getAcqNoSuffix));

        boolean containSubcontract = projOrderList.stream().anyMatch(projOrder -> Constant.YES.equals(projOrder.getExt$Item("subcontract2")));
        String subPackageNo = containSubcontract ? ApplicationContextHelper.getNextSequence(orderItemGroupService.getDao().getTable()) : null;

        List<EmcOrderTaskBean> list = new ArrayList<>();
        for (EmcOrderBean order : orderList) {
            for (EmcProjOrderBean projOrder : groupProjOrderList) {
                // 对于年度订单这种合同，同一点位可能对应多个周期频次
                String realFrequency = projOrder.getExt$Item("realFrequency");
                String realCycle = projOrder.getExt$Item("realCycle");
                if (NumberUtils.isNumber(realFrequency) && NumberUtils.isNumber(realCycle)
                        && (order.getFrequency() > NumberUtils.parseLong(realFrequency) || order.getCycle() > NumberUtils.parseLong(realCycle))) {
                    continue;
                }

                List<EmcGroupItemBean> filterItemList = this.filterItem(projOrder, projOrderList, itemList);

                // 查询该样品测试所维护的指标设置的指标项目
                List<EmcIndexSetBean> indexSetOfGroupList = null;
                List<EmcIndexTestItemBean> indexTestItemOfGroupList = null;
                if (!indexTestItemList.isEmpty() && !indexSetList.isEmpty()) {
                    indexSetOfGroupList = indexSetList.stream().filter(e -> ObjectUtils.equals(e.getIndexId(), projOrder.getIndexId())).collect(Collectors.toList());
                    List<Long> indexItemIdOfGroupList = indexSetOfGroupList.stream().map(e -> e.getIndexItemId()).distinct().collect(Collectors.toList());
                    indexTestItemOfGroupList = indexTestItemList.stream().filter(e -> indexItemIdOfGroupList.contains(e.getIndexItemId())).collect(Collectors.toList());
                }

                // 是否需要采样后分包
                boolean samplingSubcontract = Constant.YES.equals(projOrder.getExt$Item("subcontract2"));

                // 取当前监测方案样品的分析类型List
                List<EmcGroupItemBean> analysisCategoryList = getDistinctItemList(filterItemList);

                for (EmcGroupItemBean analysisItem : analysisCategoryList) {
                    EmcTestCapacityBean testCapacity = testCapacityList.stream().filter(f -> ObjectUtils.equals(f.getTestMethodId(), NumberUtils.parseLong(analysisItem.getExt$Item("testmethodid")))).findFirst().orElseGet(EmcTestCapacityBean::new);

                    EmcTestMethodOrderCategoryBean testOrder = testOrderList.stream()
                            .filter(f -> ObjectUtils.equals(f.getTestMethodId(), NumberUtils.parseLong(analysisItem.getExt$Item("testmethodid")))
                                    && ObjectUtils.equals(f.getMonitorTmplId(), order.getMonitorTmplId()))
                            .findFirst().orElse(null);

                    Long orderSampleNum = order.getSampleNum();// 样品采样数
                    Long projOrderSampleNum = projOrder.getSampleNum();// 监测方案样品采样数
                    // 如果【监测方案样品采样数】>=【样品采样数】,则当前监测方案样品生成一条样品测试
                    if (projOrderSampleNum >= orderSampleNum) {
                        EmcOrderTaskBean orderTask = new EmcOrderTaskBean();
                        orderTask.setId(ApplicationContextHelper.getNextIdentity());
                        orderTask.setFolderId(order.getFolderId());
                        orderTask.setOrderId(order.getId());
                        orderTask.setProjId(order.getProjId());
                        orderTask.setSchemeName(projOrder.getSchemeName());
                        orderTask.setTestMethodId(NumberUtils.parseLong(analysisItem.getExt$Item("testmethodid")));
                        orderTask.setTestId(projOrder.getTestId());
                        orderTask.setTestName(projOrder.getExt$Item("testname"));
                        orderTask.setMethodId(projOrder.getMethodId());
                        orderTask.setMethodName(projOrder.getExt$Item("methodname"));
                        orderTask.setTestMethodBasis(projOrder.getExt$Item("testmethodbasis"));
                        orderTask.setMethodVersionId(NumberUtils.parseLong(projOrder.getExt$Item("methodversionid")));
                        orderTask.setAnalysisType(analysisItem.getExt$Item("analysiscategory"));
                        orderTask.setMonitorTmplId(order.getMonitorTmplId());
                        orderTask.setOrderCategory(order.getOrderCategory());
                        orderTask.setQcCategoryId(order.getQcCategoryId());
                        orderTask.setQcCategoryCode(order.getQcCategoryCode());
                        orderTask.setQcName(order.getQcName());
                        boolean isSolidWaste = isSolidWaste(order);
                        String acqNoSuffix = acqNoSuffixMap.get(orderTask.getTestMethodId());
                        String acqNo = (isSolidWaste && acqNoSuffix != null) ? String.join("-", order.getOrderNo(), acqNoSuffix) : order.getOrderNo();

                        orderTask.setEquiptAcquNo(acqNo);
                        orderTask.setIndexId(projOrder.getIndexId());
                        orderTask.setIndexCategoryId(projOrder.getIndexCategoryId());
                        orderTask.setSubcontractorId(projOrder.getSubcontractorId());
                        orderTask.setSubPackageNo(projOrder.getSubPackageNo());
                        orderTask.setDataSharing(projOrder.getDataSharing());
                        orderTask.setSpecifyMethod(projOrder.getSpecifyMethod());
                        orderTask.setTaskLowLimit(projOrder.getTaskLowLimit());
                        orderTask.setTaskHighLimit(projOrder.getTaskHighLimit());
                        orderTask.setExt$Item("relatedTest", projOrder.getExt$Item("relatedTest"));
                        // 如果样品测试的分析类型为采样，样品测试的分析科室为监测方案的采样科室
                        String analysisCategory = analysisItem.getExt$Item("analysiscategory");
                        if (EmcBizConstant.ANALYSISCATEGORY_SAMPLING.equals(analysisCategory)) {
                            orderTask.setAnalysisOrgId(projScheme.getSampleOrgId());
                            orderTask.setAnalysisOrgName(projScheme.getExt$Item("orgname"));
                            if (samplingSubcontract) {
                                orderTask.setSamplingSubpackage(Constant.YES);
                            }
                        } else {
                            orderTask.setAnalysisOrgId(testCapacity.getExt$Item("analysisOrgId"));
                            orderTask.setAnalysisOrgName(testCapacity.getExt$Item("analysisOrgName"));
                            if (samplingSubcontract) {
                                // 由于没有维护分包商，这里的分包商ID随机生成
                                orderTask.setSubcontractorId(ApplicationContextHelper.getNextIdentity());
                                orderTask.setSubPackageNo(subPackageNo);
                                orderTask.setSamplingSubpackage(Constant.YES);
                            }
                        }

                        if (!StringUtils.isEmpty(testCapacity.getExt$Item("inputUserId"))) {
                            orderTask.setInputUserId(testCapacity.getExt$Item("inputUserId"));
                            orderTask.setInputUserName(testCapacity.getExt$Item("inputUserName"));
                        }
                        if (testOrder != null) {
                            orderTask.setContainerCategoryId(testOrder.getContainerCategoryId());
                            orderTask.setContainerCategoryCode(testOrder.getExt$Item("containercategorycode"));
                            orderTask.setContainerCategoryName(testOrder.getExt$Item("containercategoryname"));
                            orderTask.setContainerGroup(testOrder.getContainerGroup());
                            orderTask.setFixative(testOrder.getFixative());
                            orderTask.setStoreCondition(testOrder.getStoreCondition());
                            orderTask.setExt$Item("sampleWeight", testOrder.getSampleWeight());
                        }
                        list.add(orderTask);

                        // 生成样品结果
                        List<EmcOrderTaskResultBean> resultList = generateOrderTaskResult(orderTask, filterItemList, order, indexSetOfGroupList, indexTestItemOfGroupList);
                        orderTaskResultList.addAll(resultList);
                    }
                }
            }
        }
        return list;
    }

    @Override
    protected List<EmcGroupItemBean> getDistinctItemList(List<EmcGroupItemBean> itemList) {
        return new ArrayList<>(itemList.stream().filter(item -> !StringUtils.isEmpty(item.getExt$Item("analysiscategory"))).collect(Collectors.groupingBy(v -> v.getExt$Item("analysiscategory"), Collectors.collectingAndThen(Collectors.toList(), v -> v.get(0)))).values());
    }

    /**
     * 生成样品测试数据
     */
    private List<EmcOrderTaskBean> createOrderTaskListSp(EmcProjSchemeBean projScheme, List<EmcOrderBean> orderList, List<EmcProjOrderBean> projOrderList, List<EmcTestMethodBean> testMethodList, List<EmcGroupItemBean> itemList,
                                                       List<EmcTestCapacityBean> testCapacityList, List<EmcTestMethodOrderCategoryBean> testOrderList, List<EmcOrderTaskResultBean> orderTaskResultList, List<EmcIndexSetBean> indexSetList,
                                                       List<EmcIndexTestItemBean> indexTestItemList) {

        // 方案样品按测试和方法分组
        List<EmcProjOrderBean> groupProjOrderList = projOrderList.stream().collect(
                Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(o -> o.getTestId() + "#" + o.getMethodId()))), ArrayList::new));

        // 查询采集编号后缀信息
        Map<Long, String> acqNoSuffixMap = testMethodList.stream().filter(t -> !StringUtils.isEmpty(t.getAcqNoSuffix())).collect(Collectors.toMap(EmcTestMethodBean::getId, EmcTestMethodBean::getAcqNoSuffix));

        boolean containSubcontract = projOrderList.stream().anyMatch(projOrder -> Constant.YES.equals(projOrder.getExt$Item("subcontract2")));
        String subPackageNo = containSubcontract ? ApplicationContextHelper.getNextSequence(orderItemGroupService.getDao().getTable()) : null;

        List<String> parentOrderNoList = orderList.stream().filter(order -> !StringUtils.isEmpty(order.getParentOrderNo()))
                .map(EmcOrderBean::getParentOrderNo).collect(Collectors.toList());
        List<EmcOrderTaskBean> parentOrderTaskList = Collections.emptyList();
        if (!parentOrderNoList.isEmpty()) {
            List<EmcOrderBean> parentOrderList = orderService.getDao().selectListByOneColumnValues(parentOrderNoList,
                    "ORDERNO", Arrays.asList("ID", "ORDERNO"), new Order[0]);
            List<Long> parentOrderIdList = parentOrderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList());
            parentOrderTaskList = orderTaskService.getDao().selectListByOneColumnValues(parentOrderIdList,
                    "ORDERID", Arrays.asList("ID", "ORDERID", "TESTID", "METHODID"), new Order[0]);
        }
        List<EmcOrderTaskBean> validParentOrderTaskList = parentOrderTaskList;

        List<EmcOrderTaskBean> list = new ArrayList<>();
        for (EmcOrderBean order : orderList) {
            for (EmcProjOrderBean projOrder : groupProjOrderList) {
                // 对于年度订单这种合同，同一点位可能对应多个周期频次
                String realFrequency = projOrder.getExt$Item("realFrequency");
                String realCycle = projOrder.getExt$Item("realCycle");
                if (NumberUtils.isNumber(realFrequency) && NumberUtils.isNumber(realCycle)
                        && (order.getFrequency() > NumberUtils.parseLong(realFrequency) || order.getCycle() > NumberUtils.parseLong(realCycle))) {
                    continue;
                }

                List<EmcGroupItemBean> filterItemList = this.filterItem(projOrder, projOrderList, itemList);

                // 查询该样品测试所维护的指标设置的指标项目
                List<EmcIndexSetBean> indexSetOfGroupList = null;
                List<EmcIndexTestItemBean> indexTestItemOfGroupList = null;
                if (!indexTestItemList.isEmpty() && !indexSetList.isEmpty()) {
                    indexSetOfGroupList = indexSetList.stream().filter(e -> ObjectUtils.equals(e.getIndexId(), projOrder.getIndexId())).collect(Collectors.toList());
                    List<Long> indexItemIdOfGroupList = indexSetOfGroupList.stream().map(e -> e.getIndexItemId()).distinct().collect(Collectors.toList());
                    indexTestItemOfGroupList = indexTestItemList.stream().filter(e -> indexItemIdOfGroupList.contains(e.getIndexItemId())).collect(Collectors.toList());
                }

                // 是否需要采样后分包
                boolean samplingSubcontract = Constant.YES.equals(projOrder.getExt$Item("subcontract2"));

                EmcOrderTaskBean parentOrderTask = validParentOrderTaskList.stream()
                        .filter(t -> ObjectUtils.equals(t.getTestId(), projOrder.getTestId())
                                && ObjectUtils.equals(t.getMethodId(), projOrder.getMethodId()))
                        .findFirst().orElse(null);

                // 取当前监测方案样品的分析类型List
                List<EmcGroupItemBean> analysisCategoryList = getDistinctItemList(filterItemList);

                for (EmcGroupItemBean analysisItem : analysisCategoryList) {
                    EmcTestCapacityBean testCapacity = testCapacityList.stream().filter(f -> ObjectUtils.equals(f.getTestMethodId(), NumberUtils.parseLong(analysisItem.getExt$Item("testmethodid")))).findFirst().orElseGet(EmcTestCapacityBean::new);

                    EmcTestMethodOrderCategoryBean testOrder = testOrderList.stream()
                            .filter(f -> ObjectUtils.equals(f.getTestMethodId(), NumberUtils.parseLong(analysisItem.getExt$Item("testmethodid")))
                                    && ObjectUtils.equals(f.getMonitorTmplId(), order.getMonitorTmplId()))
                            .findFirst().orElse(null);

                    Long orderSampleNum = order.getSampleNum();// 样品采样数
                    Long projOrderSampleNum = projOrder.getSampleNum();// 监测方案样品采样数
                    // 如果【监测方案样品采样数】>=【样品采样数】,则当前监测方案样品生成一条样品测试
                    //if (projOrderSampleNum >= orderSampleNum) {
                        EmcOrderTaskBean orderTask = new EmcOrderTaskBean();
                        orderTask.setId(ApplicationContextHelper.getNextIdentity());
                        orderTask.setFolderId(order.getFolderId());
                        orderTask.setOrderId(order.getId());
                        orderTask.setProjId(order.getProjId());
                        orderTask.setSchemeName(projOrder.getSchemeName());
                        orderTask.setTestMethodId(NumberUtils.parseLong(analysisItem.getExt$Item("testmethodid")));
                        orderTask.setTestId(projOrder.getTestId());
                        orderTask.setTestName(projOrder.getExt$Item("testname"));
                        orderTask.setMethodId(projOrder.getMethodId());
                        orderTask.setMethodName(projOrder.getExt$Item("methodname"));
                        orderTask.setTestMethodBasis(projOrder.getExt$Item("testmethodbasis"));
                        orderTask.setMethodVersionId(NumberUtils.parseLong(projOrder.getExt$Item("methodversionid")));
                        orderTask.setAnalysisType(analysisItem.getExt$Item("analysiscategory"));
                        orderTask.setMonitorTmplId(order.getMonitorTmplId());
                        orderTask.setOrderCategory(order.getOrderCategory());
                        orderTask.setQcCategoryId(order.getQcCategoryId());
                        orderTask.setQcCategoryCode(order.getQcCategoryCode());
                        orderTask.setQcName(order.getQcName());
                        if (parentOrderTask != null) {
                            orderTask.setParentId(parentOrderTask.getId());
                        }

                        boolean isSolidWaste = isSolidWaste(order);
                        String acqNoSuffix = acqNoSuffixMap.get(orderTask.getTestMethodId());
                        String acqNo = (isSolidWaste && acqNoSuffix != null) ? String.join("-", order.getOrderNo(), acqNoSuffix) : order.getOrderNo();

                        orderTask.setEquiptAcquNo(acqNo);
                        orderTask.setIndexId(projOrder.getIndexId());
                        orderTask.setIndexCategoryId(projOrder.getIndexCategoryId());
                        orderTask.setSubcontractorId(projOrder.getSubcontractorId());
                        orderTask.setSubPackageNo(projOrder.getSubPackageNo());
                        orderTask.setDataSharing(projOrder.getDataSharing());
                        orderTask.setExt$Item("relatedTest", projOrder.getExt$Item("relatedTest"));
                        // 如果样品测试的分析类型为采样，样品测试的分析科室为监测方案的采样科室
                        String analysisCategory = analysisItem.getExt$Item("analysiscategory");
                        if (EmcBizConstant.ANALYSISCATEGORY_SAMPLING.equals(analysisCategory)) {
                            orderTask.setAnalysisOrgId(projScheme.getSampleOrgId());
                            orderTask.setAnalysisOrgName(projScheme.getExt$Item("orgname"));
                            if (samplingSubcontract) {
                                orderTask.setSamplingSubpackage(Constant.YES);
                            }
                        } else {
                            orderTask.setAnalysisOrgId(testCapacity.getExt$Item("analysisOrgId"));
                            orderTask.setAnalysisOrgName(testCapacity.getExt$Item("analysisOrgName"));
                            if (samplingSubcontract) {
                                // 由于没有维护分包商，这里的分包商ID随机生成
                                orderTask.setSubcontractorId(ApplicationContextHelper.getNextIdentity());
                                orderTask.setSubPackageNo(subPackageNo);
                                orderTask.setSamplingSubpackage(Constant.YES);
                            }
                        }

                        if (!StringUtils.isEmpty(testCapacity.getExt$Item("inputUserId"))) {
                            orderTask.setInputUserId(testCapacity.getExt$Item("inputUserId"));
                            orderTask.setInputUserName(testCapacity.getExt$Item("inputUserName"));
                        }
                        if (testOrder != null) {
                            orderTask.setContainerCategoryId(testOrder.getContainerCategoryId());
                            orderTask.setContainerCategoryCode(testOrder.getExt$Item("containercategorycode"));
                            orderTask.setContainerCategoryName(testOrder.getExt$Item("containercategoryname"));
                            orderTask.setContainerGroup(testOrder.getContainerGroup());
                            orderTask.setFixative(testOrder.getFixative());
                            orderTask.setStoreCondition(testOrder.getStoreCondition());
                            orderTask.setExt$Item("sampleWeight", testOrder.getSampleWeight());
                        }
                        list.add(orderTask);

                        // 生成样品结果
                        List<EmcOrderTaskResultBean> resultList = generateOrderTaskResult(orderTask, filterItemList, order, indexSetOfGroupList, indexTestItemOfGroupList);
                        orderTaskResultList.addAll(resultList);
                    //}
                }
            }
        }
        return list;
    }

    private List<EmcPropertyDataBean> createFolderPropertyList(List<EmcFolderBean> folderList) {
        List<EmcPropertyDataBean> propertyDataList = new ArrayList<>();
        List<String> monitorTmplIdList = folderList.stream().filter(f -> !StringUtils.isEmpty(f.getMonitorTmplId())).map(EmcFolderBean::getMonitorTmplId).distinct().collect(Collectors.toList());
        List<EmcMonitorTmplBean> monitorTmplList = monitorTmplService.getDao().selectListByIds(monitorTmplIdList);
        folderList.forEach(folder -> monitorTmplList.stream().filter(t -> StringUtils.equals(folder.getMonitorTmplId(), t.getId()) && !StringUtils.isEmpty(t.getExt$Item("pointPropertyTmplId"))).findFirst().ifPresent(t -> {
            EmcPropertyDataBean propertyData = new EmcPropertyDataBean();
            propertyData.setId(ApplicationContextHelper.getNextIdentity());
            propertyData.setTmplId(NumberUtils.parseLong(t.getExt$Item("pointPropertyTmplId")));
            propertyData.setBizId(folder.getId());
            propertyData.setTableName(folderService.getDao().getTable());
            propertyDataList.add(propertyData);
        }));

        return propertyDataList;
    }

    private boolean isSolidWaste(EmcOrderBean order) {
        return Arrays.stream(PfConstant.DIOXIN_SOLID_WASTE_ORDER_CATEGORIES).anyMatch(keyword -> StringUtils.contains(order.getOrderCategory(), keyword));
    }

    @Override
    public List<String> generateFolderNos(EmcProjectBean project, String orderCategoryCode, int qty) {
        // 如果是质量控制项目，则点位编号和样品编号前缀统一设为 M
        boolean isQcProject = StringUtils.endsWith(project.getProjNo(), PfBizConstant.QC_PROJECT_NO_SUFFIX);
        String prefixCode = isQcProject ? PfBizConstant.QC_PROJECT_ORDER_NO_PREFIX : orderCategoryCode;

        CoreUserBean loginUser = LocalContextHelper.getLoginUser();
        String parentOrgId = loginUser.getExt$Item("parentOrgId");
        CoreOrgBean parentOrg = coreOrgService.selectById(parentOrgId);
        String orgCode = Optional.ofNullable(parentOrg.getExt$Item("orgShortName")).orElse("ZK");
        String dateStr = DateTimeUtils.formatLocalDateTime(project.getRegistDate(), "yyMM");
        String prefix = String.join("", Optional.ofNullable(prefixCode).orElse("O"), orgCode, dateStr);

        // 起始点位编号从5000开始，用于区分益铭老系统的点位编号，老系统从0000开始
        String nextSeqNoValue = "5000";
        EmcProjSchemeBean maxNoScheme = selectFirstByFilter(SearchFilter.instance().match("folderNo", prefix).filter(MatchPattern.SB), Order.desc("folderNo"));

        RestJsonWrapperBean queryWrapper = new RestJsonWrapperBean();
        queryWrapper.setFilterValue("targetType", "queryFolderNo");
        List<EmcFolderBean> maxNoFolderList = folderService.selectListByFilter(SearchFilter.instance().match("folderNo", prefix).filter(MatchPattern.SB), queryWrapper);
        maxNoFolderList.sort(Comparator.comparing(f -> f.getFolderNo()));
        Collections.reverse(maxNoFolderList);

        //Order.desc("folderNo"),
        Set<String> curMaxNoSet = new HashSet<>(2);
        Optional.ofNullable(maxNoScheme).ifPresent(scheme -> curMaxNoSet.add(scheme.getFolderNo()));
        if(!maxNoFolderList.isEmpty()){
            EmcFolderBean maxNoFolder = maxNoFolderList.get(0);
            Optional.ofNullable(maxNoFolder).ifPresent(folder -> curMaxNoSet.add(folder.getFolderNo()));
        }
        String curMaxNo = curMaxNoSet.stream().max(Comparator.comparing(Function.identity())).orElse(null);
        String startSeqNo = curMaxNo == null ? nextSeqNoValue : curMaxNo.substring(prefix.length());

        List<String> newSeqNoList = EmcNumberUtils.nextSeqNoList(startSeqNo, qty);

        return newSeqNoList.stream().map(seqNo -> prefix + seqNo).collect(Collectors.toList());
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void syncFolder(RestJsonWrapperBean wrapper) {
        List<EmcProjSchemeBean> projSchemeList = wrapper.parse(EmcProjSchemeBean.class);
        EmcProjSchemeBean  projScheme = projSchemeList.get(0);
        Long id = projScheme.getId();
        String monitorPointName= projScheme.getMonitorPointName();
        List<EmcProjSchemeBean> orgList = selectListByFilter(SearchFilter.instance().match("ID", id).filter(MatchPattern.OR));
        String folderNo = orgList.get(0).getFolderNo();
        List<EmcFolderBean> folderlist = folderService.selectListByFilter(SearchFilter.instance().match("FOLDERNO", folderNo).filter(MatchPattern.OR));
        if (folderlist.isEmpty()) {
            return;
        }
        EmcFolderBean folderBean = folderlist.get(0);
        if(monitorPointName!=null){
            folderBean.setMonitorPointName(monitorPointName);
        }
        if(monitorPointName!=null){
            projScheme.setMonitorPointName(monitorPointName);
        }
        folderService.getDao().update(folderBean, "MONITORPOINTNAME","MONITORPOINTNO");
        getDao().update(projScheme, "MONITORPOINTNAME","MONITORPOINTNO");
    }

    @Override
    public void updateScheme(RestJsonWrapperBean wrapper) {
        List<EmcProjSchemeBean> schemeList = wrapper.parse(EmcProjSchemeBean.class);
        List<Long> specialSchemeIdList = schemeList.stream().filter(scheme -> !ObjectUtils.isEmpty(scheme.getCycle()) || !ObjectUtils.isEmpty(scheme.getFrequency())).map(EmcProjSchemeBean::getId).collect(Collectors.toList());
        if (!specialSchemeIdList.isEmpty()) {
            // 如果是修改周期和频次，需要记录修改之前的值，用于方案修改提交时进行数据比较
            List<EmcProjSchemeBean> originalSchemeList = getDao().selectListByIds(specialSchemeIdList);
            schemeList.stream().filter(scheme -> specialSchemeIdList.contains(scheme.getId())).forEach(scheme -> originalSchemeList.stream().filter(originalScheme -> ObjectUtils.equals(scheme.getId(), originalScheme.getId())).findFirst().ifPresent(originalScheme -> {
                boolean emptyStatus = StringUtils.isEmpty(originalScheme.getExt$Item("status"));
                if (!ObjectUtils.isEmpty(scheme.getCycle()) && emptyStatus && !ObjectUtils.equals(scheme.getCycle(), originalScheme.getCycle())) {
                    scheme.setExt$Item("previousCycle", String.valueOf(originalScheme.getCycle()));
                }

                if (!ObjectUtils.isEmpty(scheme.getFrequency()) && emptyStatus && !ObjectUtils.equals(scheme.getFrequency(), originalScheme.getFrequency())) {
                    scheme.setExt$Item("previousFrequency", String.valueOf(originalScheme.getFrequency()));
                }
            }));

            // 标注周期或频次发生了改变，状态值为 2
            schemeList.stream().filter(scheme -> specialSchemeIdList.contains(scheme.getId()) && StringUtils.isEmpty(scheme.getExt$Item("status"))).forEach(scheme -> scheme.setExt$Item("status", "2"));
        }

        getDao().update(schemeList);
        //给日志信息
        //登陆人信息
        CoreUserBean user = LocalContextHelper.getLoginUser();
        String parentOrgId = user.getExt$Item("parentorgid");
        //项目信息(需要替换projID)
        long projectEditMatchId = schemeList.get(0).getProjId() ;
        if(ObjectUtils.isEmpty(projectEditMatchId)){
            return;
        }
        EmcProjectBean projectEditMatch = projectService.getDao().selectById(projectEditMatchId);
        //操作信息(根据业务种类不同区分)
        //resource层的备注 +
        EmcProjectEditLogBean editLogBean = new EmcProjectEditLogBean();
        editLogBean.setId(ApplicationContextHelper.getNextIdentity());
        editLogBean.setIsSample(projectEditMatch.getSample());
        editLogBean.setOperateInfo("项目方案修改-修改方案信息");
        editLogBean.setOrgId(parentOrgId);
        editLogBean.setProjId(projectEditMatch.getId());
        editLogBean.setProjNo(projectEditMatch.getProjNo());
        editLogBean.setCreatedById(LocalContextHelper.getLoginUserId());
        editLogBean.setCreatedByName(LocalContextHelper.getLoginUserName());
        editLogBean.setCreatedTime(LocalDateTime.now());
        editLogBean.setCreatedByOrgId(LocalContextHelper.getLoginOrgId());
        editLogBean.setCreatedByOrgName(LocalContextHelper.getLoginOrgName());
        projectEditLogService.getDao().insert(editLogBean);
    }

    @Override
    public void updateSchemeOrder(RestJsonWrapperBean wrapper) {
        List<EmcProjOrderBean> schemeOrderList = wrapper.parse(EmcProjOrderBean.class);
        List<Long> specialOrderIdList = schemeOrderList.stream().filter(order -> !ObjectUtils.isEmpty(order.getSampleNum())).map(EmcProjOrderBean::getId).collect(Collectors.toList());
        if (!specialOrderIdList.isEmpty()) {
            List<EmcProjOrderBean> originalOrderList = projOrderService.getDao().selectListByIds(specialOrderIdList);
            // 如果修改了采样数，则需要记录修改之前的采样数，用于方案修改提交时进行数据比较
            schemeOrderList.stream()
                    .filter(order -> specialOrderIdList.contains(order.getId()))
                    .forEach(order -> originalOrderList.stream().filter(originalOrder -> ObjectUtils.equals(originalOrder.getId(), order.getId()))
                            .findFirst().ifPresent(originalOrder -> order.setExt$Item("previousSampleNum", String.valueOf(originalOrder.getSampleNum()))));
        }

        projOrderService.getDao().update(schemeOrderList);
        EmcProjSchemeBean psBean = getDao().selectById(schemeOrderList.get(0).getProjSchemeId());
        if(ObjectUtils.isEmpty(psBean)){
            return;
        }
        //给日志信息
        //登陆人信息
        CoreUserBean user = LocalContextHelper.getLoginUser();
        String parentOrgId = user.getExt$Item("parentorgid");
        //项目信息(需要替换projID)
        long projectEditMatchId = psBean.getProjId() ;
        if(ObjectUtils.isEmpty(projectEditMatchId)){
            return;
        }
        EmcProjectBean projectEditMatch = projectService.getDao().selectById(projectEditMatchId);
        //操作信息(根据业务种类不同区分)
        //resource层的备注 +
        EmcProjectEditLogBean editLogBean = new EmcProjectEditLogBean();
        editLogBean.setId(ApplicationContextHelper.getNextIdentity());
        editLogBean.setIsSample(projectEditMatch.getSample());
        editLogBean.setOperateInfo("项目方案修改-修改测试信息");
        editLogBean.setOrgId(parentOrgId);
        editLogBean.setProjId(projectEditMatch.getId());
        editLogBean.setProjNo(projectEditMatch.getProjNo());
        editLogBean.setCreatedById(LocalContextHelper.getLoginUserId());
        editLogBean.setCreatedByName(LocalContextHelper.getLoginUserName());
        editLogBean.setCreatedTime(LocalDateTime.now());
        editLogBean.setCreatedByOrgId(LocalContextHelper.getLoginOrgId());
        editLogBean.setCreatedByOrgName(LocalContextHelper.getLoginOrgName());
        projectEditLogService.getDao().insert(editLogBean);
    }

    @Override
    @Transactional
    @ValidateDataWith(PfSyncNewFolderValidator.class)
    public void syncNewFolder(RestJsonWrapperBean wrapper) {
        List<EmcProjSchemeBean> schemeList = wrapper.parse(EmcProjSchemeBean.class);
        List<EmcProjSchemeBean> newSchemeList = schemeList.stream().filter(scheme -> StringUtils.equals(scheme.getExt$Item("status"), Constant.YES)).collect(Collectors.toList());
        if (newSchemeList.isEmpty()) {
            return;
        }

        // 生成主业务流程数据
        this.insertBpDataByScheme(newSchemeList);

        // 如果新增了样品类型，则向报告表增加一条数据
        List<EmcReportBean> reportList = reportService.getDao().selectListByOneColumnValue(newSchemeList.get(0).getProjId(), "projId", Arrays.asList("id", "monitorTmplId"));
        List<String> reportMonitorTmplIdList = reportList.stream().filter(r -> !StringUtils.isEmpty(r.getMonitorTmplId())).map(EmcReportBean::getMonitorTmplId).distinct().collect(Collectors.toList());
        List<EmcProjSchemeBean> newOrderCategorySchemeList = newSchemeList.stream().filter(s -> !reportMonitorTmplIdList.contains(s.getMonitorTmplId())).collect(Collectors.toList());
        if (!newOrderCategorySchemeList.isEmpty()) {
            List<EmcReportBean> newReportList = new ArrayList<>(newOrderCategorySchemeList.size());
            newOrderCategorySchemeList.forEach(scheme -> {
                EmcReportBean newReport = new EmcReportBean();
                newReport.setId(ApplicationContextHelper.getNextIdentity());
                newReport.setProjId(newSchemeList.get(0).getProjId());
                newReport.setMonitorTmplId(scheme.getMonitorTmplId());
                newReport.setOrderCategory(scheme.getExt$Item("monitortmplname"));
                newReport.setStatus("draft");
                newReport.setProcessStatus("draft");
                newReportList.add(newReport);
            });
            reportService.getDao().insert(newReportList);
        }

        // 重置监测方案的状态
        newSchemeList.forEach(scheme -> scheme.setExt$Item("status", null));
        this.getDao().update(newSchemeList, "status");

        List<Long> newSchemeIdList = newSchemeList.stream().map(EmcProjSchemeBean::getId).collect(Collectors.toList());
        List<EmcProjOrderBean> newProjOrderList = projOrderService.getDao().selectListByOneColumnValues(newSchemeIdList, "projSchemeId", Collections.singletonList("id"));
        newProjOrderList.forEach(order -> order.setExt$Item("status", null));
        projOrderService.getDao().update(newProjOrderList, "status");

        List<Long> validProjectIdList = newSchemeList.stream().map(EmcProjSchemeBean::getProjId).distinct().collect(Collectors.toList());
        List<EmcFolderBean> folderList = folderService.getDao().selectListByOneColumnValues(validProjectIdList, "projId");

        // 对于新生成的点位，采样日期、现场负责人和采样人设置为和其他点位的一致
        List<String> folderNoList = newSchemeList.stream().map(EmcProjSchemeBean::getFolderNo).collect(Collectors.toList());
        List<EmcFolderBean> newFolderList = folderList.stream().filter(folder -> folderNoList.contains(folder.getFolderNo())).collect(Collectors.toList());
        if (newFolderList.isEmpty()) {
            return;
        }

        List<EmcFolderBean> originalFolderList = folderList.stream().filter(folder -> !folderNoList.contains(folder.getFolderNo())).collect(Collectors.toList());
        newFolderList.forEach(folder -> originalFolderList.stream().filter(originalFolder -> !StringUtils.isEmpty(originalFolder.getSceneUserId())
                && !StringUtils.isEmpty(originalFolder.getSampleUserId())
                && !ObjectUtils.isEmpty(originalFolder.getPlanSampleDate())
                && !StringUtils.equals(originalFolder.getStatus(), "draft")).findFirst().ifPresent(originalFolder -> {
            folder.setPlanSampleDate(originalFolder.getPlanSampleDate());
            folder.setSceneUserId(originalFolder.getSceneUserId());
            folder.setSceneUserName(originalFolder.getSceneUserName());
            folder.setSampleUserId(originalFolder.getSampleUserId());
            folder.setSampleUserName(originalFolder.getSampleUserName());
        }));

        // 如果新点位没有设置采样日期、现场负责人和采样人(实际可只用判断一个字段)，说明该任务下其他点位都位于采样安排，则不自动提交新点位
        List<EmcFolderBean> submitFolderList = newFolderList.stream().filter(folder -> !StringUtils.isEmpty(folder.getSceneUserId())).collect(Collectors.toList());
        if (submitFolderList.isEmpty()) {
            return;
        }

        List<Long> submitFolderIdList = submitFolderList.stream().map(EmcFolderBean::getId).collect(Collectors.toList());
        CoreBpmnParameterDTO parameter = new CoreBpmnParameterDTO();
        parameter.setComment("方案修改提交");

        folderService.getDao().update(submitFolderList, "planSampleDate", "sceneUserId", "sceneUserName", "sampleUserId", "sampleUserName");
        folderService.startProcess(submitFolderIdList, parameter);

        //给日志信息
        //登陆人信息
        CoreUserBean user = LocalContextHelper.getLoginUser();
        String parentOrgId = user.getExt$Item("parentorgid");
        //项目信息(需要替换projID)
        long projectEditMatchId = newSchemeList.get(0).getProjId() ;
        if(ObjectUtils.isEmpty(projectEditMatchId)){
            return;
        }
        EmcProjectBean projectEditMatch = projectService.getDao().selectById(projectEditMatchId);
        //操作信息(根据业务种类不同区分)
        //resource层的备注 +
        EmcProjectEditLogBean editLogBean = new EmcProjectEditLogBean();
        editLogBean.setId(ApplicationContextHelper.getNextIdentity());
        editLogBean.setIsSample(projectEditMatch.getSample());
        editLogBean.setOperateInfo("项目方案修改-同步新增点位");
        editLogBean.setOrgId(parentOrgId);
        editLogBean.setProjId(projectEditMatch.getId());
        editLogBean.setProjNo(projectEditMatch.getProjNo());
        editLogBean.setCreatedById(LocalContextHelper.getLoginUserId());
        editLogBean.setCreatedByName(LocalContextHelper.getLoginUserName());
        editLogBean.setCreatedTime(LocalDateTime.now());
        editLogBean.setCreatedByOrgId(LocalContextHelper.getLoginOrgId());
        editLogBean.setCreatedByOrgName(LocalContextHelper.getLoginOrgName());
        projectEditLogService.getDao().insert(editLogBean);
    }

    @Override
    @Transactional
    @ValidateDataWith(PfSyncNewTestValidator.class)
    public void syncNewTest(RestJsonWrapperBean wrapper) {
        List<EmcProjSchemeBean> schemeList = wrapper.parse(EmcProjSchemeBean.class);
        List<EmcProjSchemeBean> originalSchemeList = schemeList.stream().filter(scheme -> !StringUtils.equals(scheme.getExt$Item("status"), Constant.YES)).collect(Collectors.toList());
        if (originalSchemeList.isEmpty()) {
            return;
        }

        CoreUserBean user = LocalContextHelper.getLoginUser();
        //赋值所属单位和流程类型
        String parentOrgId = user.getExt$Item("parentorgid");

        List<Long> schemeIdList = originalSchemeList.stream().map(EmcProjSchemeBean::getId).collect(Collectors.toList());
        List<EmcProjOrderBean> schemeOrderList = schemeIdList.isEmpty() ? Collections.emptyList() : projOrderService.selectListByFilter(SearchFilter.instance().match("projSchemeId", schemeIdList).filter(MatchPattern.OR));
        List<EmcProjOrderBean> newSchemeOrderList = schemeOrderList.stream().filter(test -> StringUtils.equals(test.getExt$Item("status"), Constant.YES)).collect(Collectors.toList());

        if (newSchemeOrderList.isEmpty()) {
            return;
        }

        List<Long> curSchemeIdList = newSchemeOrderList.stream().map(EmcProjOrderBean::getProjSchemeId).distinct().collect(Collectors.toList());
        List<EmcProjSchemeBean> curSchemeList = schemeList.stream().filter(scheme -> curSchemeIdList.contains(scheme.getId())).collect(Collectors.toList());
        List<String> folderNoList = curSchemeList.stream().map(EmcProjSchemeBean::getFolderNo).collect(Collectors.toList());

        List<EmcFolderBean> folderList = folderService.getDao().selectListByOneColumnValues(folderNoList, "folderNo");
        boolean folderDone = folderList.stream().anyMatch(folder -> StringUtils.equals(EmcBizConstant.FOLDER_PROCESSSTATUS_DONE, folder.getStatus()));

        if (folderDone) {
            return;
        }

        // 查询测试方法和监测能力库方法
        List<Long> capacityItemGroupIdList = newSchemeOrderList.stream().map(EmcProjOrderBean::getItemGroupId).distinct().collect(Collectors.toList());
        List<EmcCapacityItemGroupBean> capacityItemGroupList = capacityItemGroupService.getDao().selectListByOneColumnValues(capacityItemGroupIdList, "id");
        List<Long> testCapacityIdList = capacityItemGroupList.stream().map(EmcCapacityItemGroupBean::getTestCapacityId).collect(Collectors.toList());
        List<Long> testMethodIdList = capacityItemGroupList.stream().map(EmcCapacityItemGroupBean::getTestMethodId).collect(Collectors.toList());

        List<EmcTestMethodBean> testMethodList = testMethodService.getDao().selectListByIds(testMethodIdList);
        List<EmcTestCapacityBean> testCapacityList = testCapacityService.getDao().selectListByIds(testCapacityIdList);

        List<Long> itemGroupIdList = capacityItemGroupList.stream().map(EmcCapacityItemGroupBean::getItemGroupId).collect(Collectors.toList());
        List<EmcGroupItemBean> itemList = itemGroupIdList.isEmpty() ? Collections.emptyList() : groupItemService.selectListByFilter(SearchFilter.instance().match("ITEMGROUPID", itemGroupIdList).filter(MatchPattern.OR));
        itemList.forEach(item -> capacityItemGroupList.stream().filter(group -> ObjectUtils.equals(item.getItemGroupId(), group.getItemGroupId())).findFirst().ifPresent(group -> item.setExt$Item("capacityItemGroupId", group.getId() + "")));

        List<EmcTestMethodOrderCategoryBean> testOrderList = testMethodIdList.isEmpty() ? Collections.emptyList() : testMethodOrderCategoryService
                .selectListByFilter(SearchFilter.instance().match("TESTMETHODID", testMethodIdList).filter(MatchPattern.OR)
                		.match("ORGID", parentOrgId).filter(MatchPattern.OR));
        // 指标项目
        List<String> indexIdList = newSchemeOrderList.stream().map(EmcProjOrderBean::getIndexId).distinct().collect(Collectors.toList());
        List<EmcIndexSetBean> indexSetList = indexIdList.isEmpty() ? Collections.emptyList() : indexSetService.selectListByFilter(SearchFilter.instance().match("INDEXID", indexIdList).filter(MatchPattern.OR));
        List<Long> indexItemIdList = indexSetList.stream().map(EmcIndexSetBean::getIndexItemId).distinct().collect(Collectors.toList());
        List<EmcIndexTestItemBean> indexTestItemList = indexTestItemService.getDao().selectListByOneColumnValues(indexItemIdList, "indexItemId");

        Map<String, EmcFolderBean> folderNoMap = folderList.stream().collect(Collectors.toMap(EmcFolderBean::getFolderNo, Function.identity(), (v1, v2) -> v1));

        List<Long> folderIdList = folderList.stream().map(EmcFolderBean::getId).collect(Collectors.toList());
        List<EmcOrderBean> orderList = orderService.getDao().selectListByOneColumnValues(folderIdList, "folderId");
        List<EmcOrderTaskBean> orderTaskList = orderTaskService.getDao().selectListByOneColumnValues(folderIdList, "folderId");

        List<EmcOrderTaskBean> newOrderTaskList = new ArrayList<>();
        List<EmcOrderTaskResultBean> newTaskResultList = new ArrayList<>();
        List<EmcOrderItemGroupBean> newOrderItemGroupList = new ArrayList<>();

        List<String> defaultTaskStatusList = Arrays.asList("draft", "preLogged", "logged","done");

        curSchemeList.forEach(scheme -> {
            EmcFolderBean curFolder = folderNoMap.get(scheme.getFolderNo());
            Long folderId = curFolder.getId();
            List<String> taskStatusList = orderTaskList.stream().filter(task -> ObjectUtils.equals(task.getFolderId(), folderId)).map(EmcOrderTaskBean::getStatus).distinct().collect(Collectors.toList());
            List<Integer> statusIndexList = taskStatusList.stream().mapToInt(defaultTaskStatusList::indexOf).sorted().boxed().collect(Collectors.toList());
            String newTaskStatus = (statusIndexList.isEmpty() || statusIndexList.get(0) == -1) ? "logged" : defaultTaskStatusList.get(statusIndexList.get(0));

            List<EmcProjOrderBean> curSchemeOrderList = newSchemeOrderList.stream().filter(schemeOrder -> ObjectUtils.equals(schemeOrder.getProjSchemeId(), scheme.getId())).collect(Collectors.toList());
            List<EmcOrderBean> curOrderList = orderList.stream().filter(order -> ObjectUtils.equals(order.getFolderId(), folderId)).collect(Collectors.toList());

            List<EmcOrderTaskResultBean> curResultList = new ArrayList<>();
            List<EmcOrderTaskBean> curNewTaskList = createOrderTaskList(scheme, curOrderList, curSchemeOrderList, testMethodList, itemList, testCapacityList, testOrderList, curResultList, indexSetList, indexTestItemList);
            curNewTaskList.forEach(task -> task.setStatus(newTaskStatus));
            newOrderTaskList.addAll(curNewTaskList);
            newTaskResultList.addAll(curResultList);

            List<EmcOrderItemGroupBean> orderItemGroupList = this.generateOrderItemGroup(curOrderList, curSchemeOrderList, orderTaskList);
            newOrderItemGroupList.addAll(orderItemGroupList);
        });

        orderTaskService.getDao().insert(newOrderTaskList);
        orderTaskResultService.getDao().insert(newTaskResultList);

        newOrderItemGroupList.forEach(group -> {
            group.setQcCategoryCode(null);
            group.setQcName(null);
        });
        orderItemGroupService.getDao().insert(newOrderItemGroupList);

        newSchemeOrderList.forEach(schemeOrder -> schemeOrder.setExt$Item("status", null));
        projOrderService.getDao().update(newSchemeOrderList, "status");

        //给日志信息
        //登陆人信息
        //项目信息(需要替换projID)
        long projectEditMatchId = schemeList.get(0).getProjId() ;
        if(ObjectUtils.isEmpty(projectEditMatchId)){
            return;
        }
        EmcProjectBean projectEditMatch = projectService.getDao().selectById(projectEditMatchId);
        //操作信息(根据业务种类不同区分)
        //resource层的备注 +
        EmcProjectEditLogBean editLogBean = new EmcProjectEditLogBean();
        editLogBean.setId(ApplicationContextHelper.getNextIdentity());
        editLogBean.setIsSample(projectEditMatch.getSample());
        editLogBean.setOperateInfo("项目方案修改-同步新增测试");
        editLogBean.setOrgId(parentOrgId);
        editLogBean.setProjId(projectEditMatch.getId());
        editLogBean.setProjNo(projectEditMatch.getProjNo());
        editLogBean.setCreatedById(LocalContextHelper.getLoginUserId());
        editLogBean.setCreatedByName(LocalContextHelper.getLoginUserName());
        editLogBean.setCreatedTime(LocalDateTime.now());
        editLogBean.setCreatedByOrgId(LocalContextHelper.getLoginOrgId());
        editLogBean.setCreatedByOrgName(LocalContextHelper.getLoginOrgName());
        projectEditLogService.getDao().insert(editLogBean);
    }

    @Override
    @Transactional
    @ValidateDataWith(PfDeleteFolderValidator.class)
    public void deleteFolder(RestJsonWrapperBean wrapper) {
        List<EmcFolderBean> folderList = wrapper.parse(EmcFolderBean.class);
        List<Long> folderIdList = folderList.stream().map(EmcFolderBean::getId).collect(Collectors.toList());

        // 删除点位
        folderService.getDao().deleteByIdList(folderIdList);

        // 删除点位下的样品
        List<EmcOrderBean> orderList = orderService.getDao().selectListByOneColumnValues(folderIdList, "folderId", Collections.singletonList("id"));
        List<Long> orderIdList = orderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList());
        orderService.getDao().deleteByIdList(orderIdList);

        // 删除样品下分析项组
        List<EmcOrderItemGroupBean> deleteOrderItemGroupList = new ArrayList<>(orderIdList.size());
        orderIdList.forEach(orderId -> {
            EmcOrderItemGroupBean deleteOrderItemGroup = new EmcOrderItemGroupBean();
            deleteOrderItemGroup.setOrderId(orderId);
            deleteOrderItemGroupList.add(deleteOrderItemGroup);
        });
        orderItemGroupService.getDao().deleteBy(deleteOrderItemGroupList, "orderId");

        // 删除样品测试
        List<EmcOrderTaskBean> deleteOrderTaskList = new ArrayList<>(folderIdList.size());
        folderIdList.forEach(folderId -> {
            EmcOrderTaskBean deleteOrderTask = new EmcOrderTaskBean();
            deleteOrderTask.setFolderId(folderId);
            deleteOrderTaskList.add(deleteOrderTask);
        });
        orderTaskService.getDao().deleteBy(deleteOrderTaskList, "folderId");

        // 删除样品测试结果
        List<EmcOrderTaskResultBean> deleteTaskResultList = new ArrayList<>(folderIdList.size());
        folderIdList.forEach(folderId -> {
            EmcOrderTaskResultBean deleteTaskResult = new EmcOrderTaskResultBean();
            deleteTaskResult.setFolderId(folderId);
            deleteTaskResultList.add(deleteTaskResult);
        });
        orderTaskResultService.getDao().deleteBy(deleteTaskResultList, "folderId");

        //给日志信息
        //登陆人信息
        CoreUserBean user = LocalContextHelper.getLoginUser();
        String parentOrgId = user.getExt$Item("parentorgid");
        //项目信息(需要替换projID)
        long projectEditMatchId = folderList.get(0).getProjId() ;
        if(ObjectUtils.isEmpty(projectEditMatchId)){
            return;
        }
        EmcProjectBean projectEditMatch = projectService.getDao().selectById(projectEditMatchId);
        //操作信息(根据业务种类不同区分)
        //resource层的备注 +
        EmcProjectEditLogBean editLogBean = new EmcProjectEditLogBean();
        editLogBean.setId(ApplicationContextHelper.getNextIdentity());
        editLogBean.setIsSample(projectEditMatch.getSample());
        editLogBean.setOperateInfo("项目方案修改-删除点位");
        editLogBean.setOrgId(parentOrgId);
        editLogBean.setProjId(projectEditMatch.getId());
        editLogBean.setProjNo(projectEditMatch.getProjNo());
        editLogBean.setCreatedById(LocalContextHelper.getLoginUserId());
        editLogBean.setCreatedByName(LocalContextHelper.getLoginUserName());
        editLogBean.setCreatedTime(LocalDateTime.now());
        editLogBean.setCreatedByOrgId(LocalContextHelper.getLoginOrgId());
        editLogBean.setCreatedByOrgName(LocalContextHelper.getLoginOrgName());
        projectEditLogService.getDao().insert(editLogBean);
    }

    @Override
    @Transactional
    @ValidateDataWith(PfSyncFolderFromSchemeValidator.class)
    public void syncFolderFromScheme(RestJsonWrapperBean wrapper) {
        List<EmcProjSchemeBean> schemeList = wrapper.parse(EmcProjSchemeBean.class);
        if (schemeList.isEmpty()) {
            return;
        }

        // 生成主业务流程数据
        this.insertBpDataByScheme(schemeList);

        List<Long> validProjectIdList = schemeList.stream().map(EmcProjSchemeBean::getProjId).distinct().collect(Collectors.toList());
        List<EmcFolderBean> folderList = folderService.getDao().selectListByOneColumnValues(validProjectIdList, "projId");

        // 对于新生成的点位，采样日期、现场负责人和采样人设置为和其他点位的一致
        List<String> folderNoList = schemeList.stream().map(EmcProjSchemeBean::getFolderNo).collect(Collectors.toList());
        List<EmcFolderBean> newFolderList = folderList.stream().filter(folder -> folderNoList.contains(folder.getFolderNo())).collect(Collectors.toList());
        if (newFolderList.isEmpty()) {
            return;
        }

        List<EmcFolderBean> originalFolderList = folderList.stream().filter(folder -> !folderNoList.contains(folder.getFolderNo())).collect(Collectors.toList());
        newFolderList.forEach(folder -> originalFolderList.stream().filter(originalFolder -> !StringUtils.isEmpty(originalFolder.getSceneUserId())
                && !StringUtils.isEmpty(originalFolder.getSampleUserId())
                && !ObjectUtils.isEmpty(originalFolder.getPlanSampleDate())
                && !StringUtils.equals(originalFolder.getStatus(), "draft")).findFirst().ifPresent(originalFolder -> {
            folder.setPlanSampleDate(originalFolder.getPlanSampleDate());
            folder.setSceneUserId(originalFolder.getSceneUserId());
            folder.setSceneUserName(originalFolder.getSceneUserName());
            folder.setSampleUserId(originalFolder.getSampleUserId());
            folder.setSampleUserName(originalFolder.getSampleUserName());
        }));

        // 如果新点位没有设置采样日期、现场负责人和采样人(实际可只用判断一个字段)，说明该任务下其他点位都位于采样安排，则不自动提交新点位
        List<EmcFolderBean> submitFolderList = newFolderList.stream().filter(folder -> !StringUtils.isEmpty(folder.getSceneUserId())).collect(Collectors.toList());
        if (submitFolderList.isEmpty()) {
            return;
        }

        List<Long> submitFolderIdList = submitFolderList.stream().map(EmcFolderBean::getId).collect(Collectors.toList());
        CoreBpmnParameterDTO parameter = new CoreBpmnParameterDTO();
        parameter.setComment("从方案同步点位");

        folderService.getDao().update(submitFolderList, "planSampleDate", "sceneUserId", "sceneUserName", "sampleUserId", "sampleUserName");
        folderService.startProcess(submitFolderIdList, parameter);

        //给日志信息
        //登陆人信息
        CoreUserBean user = LocalContextHelper.getLoginUser();
        String parentOrgId = user.getExt$Item("parentorgid");
        //项目信息(需要替换projID)
        long projectEditMatchId = schemeList.get(0).getProjId() ;
        if(ObjectUtils.isEmpty(projectEditMatchId)){
            return;
        }
        EmcProjectBean projectEditMatch = projectService.getDao().selectById(projectEditMatchId);
        //操作信息(根据业务种类不同区分)
        //resource层的备注 +
        EmcProjectEditLogBean editLogBean = new EmcProjectEditLogBean();
        editLogBean.setId(ApplicationContextHelper.getNextIdentity());
        editLogBean.setIsSample(projectEditMatch.getSample());
        editLogBean.setOperateInfo("项目方案修改-从方案同步点位");
        editLogBean.setOrgId(parentOrgId);
        editLogBean.setProjId(projectEditMatch.getId());
        editLogBean.setProjNo(projectEditMatch.getProjNo());
        editLogBean.setCreatedById(LocalContextHelper.getLoginUserId());
        editLogBean.setCreatedByName(LocalContextHelper.getLoginUserName());
        editLogBean.setCreatedTime(LocalDateTime.now());
        editLogBean.setCreatedByOrgId(LocalContextHelper.getLoginOrgId());
        editLogBean.setCreatedByOrgName(LocalContextHelper.getLoginOrgName());
        projectEditLogService.getDao().insert(editLogBean);
    }

    @Override
    @ValidateDataWith(PfDeleteOrderValidator.class)
    @Transactional
    public void deleteOrder(RestJsonWrapperBean wrapper) {
        List<EmcOrderBean> orderList = wrapper.parse(EmcOrderBean.class);
        List<Long> orderIdList = orderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList());

        orderService.getDao().deleteByIdList(orderIdList);

        // 删除样品下分析项组
        List<EmcOrderItemGroupBean> deleteOrderItemGroupList = new ArrayList<>(orderIdList.size());
        orderIdList.forEach(orderId -> {
            EmcOrderItemGroupBean deleteOrderItemGroup = new EmcOrderItemGroupBean();
            deleteOrderItemGroup.setOrderId(orderId);
            deleteOrderItemGroupList.add(deleteOrderItemGroup);
        });
        orderItemGroupService.getDao().deleteBy(deleteOrderItemGroupList, "orderId");

        // 删除样品测试
        List<EmcOrderTaskBean> deleteOrderTaskList = new ArrayList<>(orderIdList.size());
        orderIdList.forEach(orderId -> {
            EmcOrderTaskBean deleteOrderTask = new EmcOrderTaskBean();
            deleteOrderTask.setOrderId(orderId);
            deleteOrderTaskList.add(deleteOrderTask);
        });
        orderTaskService.getDao().deleteBy(deleteOrderTaskList, "orderId");

        // 删除样品测试结果
        List<EmcOrderTaskResultBean> deleteTaskResultList = new ArrayList<>(orderIdList.size());
        orderIdList.forEach(orderId -> {
            EmcOrderTaskResultBean deleteTaskResult = new EmcOrderTaskResultBean();
            deleteTaskResult.setOrderId(orderId);
            deleteTaskResultList.add(deleteTaskResult);
        });
        orderTaskResultService.getDao().deleteBy(deleteTaskResultList, "orderId");

        //给日志信息
        //登陆人信息
        CoreUserBean user = LocalContextHelper.getLoginUser();
        String parentOrgId = user.getExt$Item("parentorgid");
        //项目信息(需要替换projID)
        long projectEditMatchId = orderList.get(0).getProjId() ;
        if(ObjectUtils.isEmpty(projectEditMatchId)){
            return;
        }
        EmcProjectBean projectEditMatch = projectService.getDao().selectById(projectEditMatchId);
        //操作信息(根据业务种类不同区分)
        //resource层的备注 +
        EmcProjectEditLogBean editLogBean = new EmcProjectEditLogBean();
        editLogBean.setId(ApplicationContextHelper.getNextIdentity());
        editLogBean.setIsSample(projectEditMatch.getSample());
        editLogBean.setOperateInfo("项目方案修改-删除样品");
        editLogBean.setOrgId(parentOrgId);
        editLogBean.setProjId(projectEditMatch.getId());
        editLogBean.setProjNo(projectEditMatch.getProjNo());
        editLogBean.setCreatedById(LocalContextHelper.getLoginUserId());
        editLogBean.setCreatedByName(LocalContextHelper.getLoginUserName());
        editLogBean.setCreatedTime(LocalDateTime.now());
        editLogBean.setCreatedByOrgId(LocalContextHelper.getLoginOrgId());
        editLogBean.setCreatedByOrgName(LocalContextHelper.getLoginOrgName());
        projectEditLogService.getDao().insert(editLogBean);
    }

    @Override
    public void syncOrderFromScheme(RestJsonWrapperBean wrapper) {
        List<EmcFolderBean> folderList = wrapper.parse(EmcFolderBean.class, 0);
        List<EmcProjSchemeBean> schemeList = wrapper.parse(EmcProjSchemeBean.class, 1);
        List<Long> schemeIdList = schemeList.stream().map(EmcProjSchemeBean::getId).collect(Collectors.toList());

        List<EmcProjOrderBean> projOrderList = schemeIdList.isEmpty() ? Collections.emptyList() : projOrderService.selectListByFilter(SearchFilter.instance().match("projSchemeId", schemeIdList).filter(MatchPattern.OR));

        // 生成样品
        EmcProjSchemeBean projScheme = schemeList.get(0);
        List<EmcOrderBean> orderList = this.generateOrderList(folderList.get(0), projScheme, projOrderList);

        CoreUserBean user = LocalContextHelper.getLoginUser();
        //赋值所属单位和流程类型
        String parentOrgId = user.getExt$Item("parentorgid");

        // 查询测试方法和监测能力库方法
        List<Long> capacityItemGroupIdList = projOrderList.stream().map(EmcProjOrderBean::getItemGroupId).distinct().collect(Collectors.toList());
        List<EmcCapacityItemGroupBean> capacityItemGroupList = capacityItemGroupService.getDao().selectListByOneColumnValues(capacityItemGroupIdList, "id");
        List<Long> testCapacityIdList = capacityItemGroupList.stream().map(EmcCapacityItemGroupBean::getTestCapacityId).collect(Collectors.toList());
        List<Long> testMethodIdList = capacityItemGroupList.stream().map(EmcCapacityItemGroupBean::getTestMethodId).collect(Collectors.toList());

        List<EmcTestMethodBean> testMethodList = testMethodService.getDao().selectListByIds(testMethodIdList);
        List<EmcTestCapacityBean> testCapacityList = testCapacityService.getDao().selectListByIds(testCapacityIdList);

        List<Long> itemGroupIdList = capacityItemGroupList.stream().map(EmcCapacityItemGroupBean::getItemGroupId).collect(Collectors.toList());
        List<EmcGroupItemBean> itemList = itemGroupIdList.isEmpty() ? Collections.emptyList() : groupItemService.selectListByFilter(SearchFilter.instance().match("ITEMGROUPID", itemGroupIdList).filter(MatchPattern.OR));
        itemList.forEach(item -> capacityItemGroupList.stream().filter(group -> ObjectUtils.equals(item.getItemGroupId(), group.getItemGroupId())).findFirst().ifPresent(group -> item.setExt$Item("capacityItemGroupId", group.getId() + "")));

        List<EmcTestMethodOrderCategoryBean> testOrderList = testMethodIdList.isEmpty() ? Collections.emptyList() : testMethodOrderCategoryService
                .selectListByFilter(SearchFilter.instance().match("TESTMETHODID", testMethodIdList).filter(MatchPattern.OR)
                		.match("ORGID", parentOrgId).filter(MatchPattern.OR));
        // 指标项目
        List<String> indexIdList = projOrderList.stream().map(EmcProjOrderBean::getIndexId).distinct().collect(Collectors.toList());
        List<EmcIndexSetBean> indexSetList = indexIdList.isEmpty() ? Collections.emptyList() : indexSetService.selectListByFilter(SearchFilter.instance().match("INDEXID", indexIdList).filter(MatchPattern.OR));
        List<Long> indexItemIdList = indexSetList.stream().map(EmcIndexSetBean::getIndexItemId).distinct().collect(Collectors.toList());
        List<EmcIndexTestItemBean> indexTestItemList = indexTestItemService.getDao().selectListByOneColumnValues(indexItemIdList, "indexItemId");

        // 生成样品测试和生成样品结果
        List<EmcOrderTaskResultBean> orderTaskResultList = new ArrayList<>();
        List<EmcOrderTaskBean> orderTaskList = this.createOrderTaskList(projScheme, orderList, projOrderList, testMethodList, itemList, testCapacityList, testOrderList, orderTaskResultList, indexSetList, indexTestItemList);

        // 生成样品分析项组
        List<EmcOrderItemGroupBean> orderItemGroupList = this.generateOrderItemGroup(orderList, projOrderList, orderTaskList);

        orderService.getDao().insert(orderList);
        orderTaskService.getDao().insert(orderTaskList);
        orderTaskResultService.getDao().insert(orderTaskResultList);
        orderItemGroupService.getDao().insert(orderItemGroupList);
        //给日志信息
        //登陆人信息
        //项目信息(需要替换projID)
        long projectEditMatchId = schemeList.get(0).getProjId() ;
        if(ObjectUtils.isEmpty(projectEditMatchId)){
            return;
        }
        EmcProjectBean projectEditMatch = projectService.getDao().selectById(projectEditMatchId);
        //操作信息(根据业务种类不同区分)
        //resource层的备注 +
        EmcProjectEditLogBean editLogBean = new EmcProjectEditLogBean();
        editLogBean.setId(ApplicationContextHelper.getNextIdentity());
        editLogBean.setIsSample(projectEditMatch.getSample());
        editLogBean.setOperateInfo("项目方案修改-从方案同步样品");
        editLogBean.setOrgId(parentOrgId);
        editLogBean.setProjId(projectEditMatch.getId());
        editLogBean.setProjNo(projectEditMatch.getProjNo());
        editLogBean.setCreatedById(LocalContextHelper.getLoginUserId());
        editLogBean.setCreatedByName(LocalContextHelper.getLoginUserName());
        editLogBean.setCreatedTime(LocalDateTime.now());
        editLogBean.setCreatedByOrgId(LocalContextHelper.getLoginOrgId());
        editLogBean.setCreatedByOrgName(LocalContextHelper.getLoginOrgName());
        projectEditLogService.getDao().insert(editLogBean);
    }

    @Override
    public void copyOrder(RestJsonWrapperBean wrapper) {
        orderService.copyOrders(wrapper);
    }

    @Override
    @ValidateDataWith(PfDeleteTestValidator.class)
    @Transactional
    public void deleteTest(RestJsonWrapperBean wrapper) {
        List<EmcOrderTaskBean> orderTaskList = wrapper.parse(EmcOrderTaskBean.class);
        List<EmcOrderBean> orderList = wrapper.parse(EmcOrderBean.class);

        List<Long> orderIdList = orderList.stream().map(EmcOrderBean :: getId).distinct().collect(Collectors.toList());
        List<Long> testMethodIdList = orderTaskList.stream().map(EmcOrderTaskBean::getTestMethodId).distinct().collect(Collectors.toList());
        List<String> itemGroupList = orderTaskList.stream().map(task -> task.getExt$Item("itemGroup")).distinct().collect(Collectors.toList());

        List<EmcOrderItemGroupBean> orderItemGroupList = orderItemGroupService.selectListByFilter(SearchFilter.instance().match("orderId", orderIdList).filter(MatchPattern.OR).match("testMethodId", testMethodIdList).filter(MatchPattern.OR));
        List<Long> deleteOrderItemGroupIdList = orderItemGroupList.stream().filter(group -> itemGroupList.contains(group.getItemGroup())).map(EmcOrderItemGroupBean::getId).collect(Collectors.toList());

        // 针对一个测试多个分析项组的，如果只删除了其中一个分析项组，那么ORDER TASK 数据不应该被删除
        List<EmcOrderItemGroupBean> retainOrderItemGroupList = orderItemGroupList.stream().filter(group -> !deleteOrderItemGroupIdList.contains(group.getId())).collect(Collectors.toList());
        List<EmcOrderTaskBean> allOrderTaskList = orderTaskService.selectListByFilter(SearchFilter.instance().match("orderId", orderIdList).filter(MatchPattern.OR).match("testMethodId", testMethodIdList).filter(MatchPattern.OR));
        List<EmcOrderTaskBean> deleteOrderTaskList = allOrderTaskList.stream().filter(task -> retainOrderItemGroupList.stream().noneMatch(group -> ObjectUtils.equals(task.getTestMethodId(), group.getTestMethodId()) && ObjectUtils.equals(task.getOrderId(), group.getOrderId()))).collect(Collectors.toList());

        orderItemGroupService.getDao().deleteByIdList(deleteOrderItemGroupIdList);

        List<Long> deleteOrderTaskIdList = deleteOrderTaskList.stream().map(EmcOrderTaskBean::getId).collect(Collectors.toList());
        orderTaskService.getDao().deleteByIdList(deleteOrderTaskIdList);

        List<EmcOrderTaskResultBean> deleteResultList = new ArrayList<>(deleteOrderTaskIdList.size());
        deleteOrderTaskIdList.forEach(taskId -> {
            EmcOrderTaskResultBean deleteResult = new EmcOrderTaskResultBean();
            deleteResult.setOrderTaskId(taskId);
            deleteResultList.add(deleteResult);
        });
        orderTaskResultService.getDao().deleteBy(deleteResultList, "orderTaskId");

        //给日志信息
        //登陆人信息
        CoreUserBean user = LocalContextHelper.getLoginUser();
        String parentOrgId = user.getExt$Item("parentorgid");
        //项目信息(需要替换projID)
        long projectEditMatchId = orderTaskList.get(0).getProjId() ;
        if(ObjectUtils.isEmpty(projectEditMatchId)){
            return;
        }
        EmcProjectBean projectEditMatch = projectService.getDao().selectById(projectEditMatchId);
        //操作信息(根据业务种类不同区分)
        //resource层的备注 +
        EmcProjectEditLogBean editLogBean = new EmcProjectEditLogBean();
        editLogBean.setId(ApplicationContextHelper.getNextIdentity());
        editLogBean.setIsSample(projectEditMatch.getSample());
        editLogBean.setOperateInfo("项目方案修改-删除测试");
        editLogBean.setOrgId(parentOrgId);
        editLogBean.setProjId(projectEditMatch.getId());
        editLogBean.setProjNo(projectEditMatch.getProjNo());
        editLogBean.setCreatedById(LocalContextHelper.getLoginUserId());
        editLogBean.setCreatedByName(LocalContextHelper.getLoginUserName());
        editLogBean.setCreatedTime(LocalDateTime.now());
        editLogBean.setCreatedByOrgId(LocalContextHelper.getLoginOrgId());
        editLogBean.setCreatedByOrgName(LocalContextHelper.getLoginOrgName());
        projectEditLogService.getDao().insert(editLogBean);
    }

    @Override
    @ValidateDataWith(PfSyncTestFromSchemeValidator.class)
    public void syncTestFromScheme(RestJsonWrapperBean wrapper) {
        List<EmcFolderBean> folderList = wrapper.parse(EmcFolderBean.class, 0);
        List<EmcOrderBean> orderList = wrapper.parse(EmcOrderBean.class, 1);
        List<EmcProjSchemeBean> schemeList = wrapper.parse(EmcProjSchemeBean.class, 2);
        List<EmcProjOrderBean> newSchemeOrderList = wrapper.parse(EmcProjOrderBean.class, 3);
        EmcProjectBean projectBean = projectService.selectById(schemeList.get(0).getProjId());
        String contractNo = projectBean.getProjNo().substring(0, 10);
        // 查询测试方法和监测能力库方法
        List<Long> capacityItemGroupIdList = newSchemeOrderList.stream().map(EmcProjOrderBean::getItemGroupId).distinct().collect(Collectors.toList());
        List<EmcCapacityItemGroupBean> capacityItemGroupList = capacityItemGroupService.getDao().selectListByOneColumnValues(capacityItemGroupIdList, "id");
        List<Long> testCapacityIdList = capacityItemGroupList.stream().map(EmcCapacityItemGroupBean::getTestCapacityId).collect(Collectors.toList());
        List<Long> testMethodIdList = capacityItemGroupList.stream().map(EmcCapacityItemGroupBean::getTestMethodId).collect(Collectors.toList());

        List<EmcTestMethodBean> testMethodList = testMethodService.getDao().selectListByIds(testMethodIdList);
        List<EmcTestCapacityBean> testCapacityList = testCapacityService.getDao().selectListByIds(testCapacityIdList);

        List<Long> itemGroupIdList = capacityItemGroupList.stream().map(EmcCapacityItemGroupBean::getItemGroupId).collect(Collectors.toList());
        List<EmcGroupItemBean> itemList = itemGroupIdList.isEmpty() ? Collections.emptyList() : groupItemService.selectListByFilter(SearchFilter.instance().match("ITEMGROUPID", itemGroupIdList).filter(MatchPattern.OR));
        itemList.forEach(item -> capacityItemGroupList.stream().filter(group -> ObjectUtils.equals(item.getItemGroupId(), group.getItemGroupId())).findFirst().ifPresent(group -> item.setExt$Item("capacityItemGroupId", group.getId() + "")));
        CoreUserBean user = LocalContextHelper.getLoginUser();
        //赋值所属单位和流程类型
        String parentOrgId = user.getExt$Item("parentorgid");
        List<EmcTestMethodOrderCategoryBean> testOrderList = testMethodIdList.isEmpty() ? Collections.emptyList() : testMethodOrderCategoryService
                .selectListByFilter(SearchFilter.instance().match("TESTMETHODID", testMethodIdList).filter(MatchPattern.OR)
                		.match("ORGID", parentOrgId).filter(MatchPattern.OR));
        // 指标项目
        List<String> indexIdList = newSchemeOrderList.stream().map(EmcProjOrderBean::getIndexId).distinct().collect(Collectors.toList());
        List<EmcIndexSetBean> indexSetList = indexIdList.isEmpty() ? Collections.emptyList() : indexSetService.selectListByFilter(SearchFilter.instance().match("INDEXID", indexIdList).filter(MatchPattern.OR));
        List<Long> indexItemIdList = indexSetList.stream().map(EmcIndexSetBean::getIndexItemId).distinct().collect(Collectors.toList());
        List<EmcIndexTestItemBean> indexTestItemList = indexTestItemService.getDao().selectListByOneColumnValues(indexItemIdList, "indexItemId");

        Map<String, EmcFolderBean> folderNoMap = folderList.stream().collect(Collectors.toMap(EmcFolderBean::getFolderNo, Function.identity(), (v1, v2) -> v1));
        List<Long> folderIdList = folderList.stream().map(EmcFolderBean::getId).collect(Collectors.toList());
        List<EmcOrderTaskBean> orderTaskList = orderTaskService.getDao().selectListByOneColumnValues(folderIdList, "folderId");

        List<EmcOrderTaskBean> newOrderTaskList = new ArrayList<>();
        List<EmcOrderTaskResultBean> newTaskResultList = new ArrayList<>();
        List<EmcOrderItemGroupBean> newOrderItemGroupList = new ArrayList<>();

        List<String> defaultTaskStatusList = Arrays.asList("draft", "preLogged", "logged");

        schemeList.forEach(scheme -> {
            EmcFolderBean curFolder = folderNoMap.get(scheme.getFolderNo());
            Long folderId = curFolder.getId();
            List<String> taskStatusList = orderTaskList.stream().filter(task -> ObjectUtils.equals(task.getFolderId(), folderId)).map(EmcOrderTaskBean::getStatus).distinct().collect(Collectors.toList());
            List<Integer> statusIndexList = taskStatusList.stream().mapToInt(defaultTaskStatusList::indexOf).sorted().boxed().collect(Collectors.toList());
            String newTaskStatus = statusIndexList.isEmpty() ? "logged" :(statusIndexList.get(0) == -1 ? "logged" : defaultTaskStatusList.get(statusIndexList.get(0)));
            List<EmcProjOrderBean> curSchemeOrderList = newSchemeOrderList.stream().filter(schemeOrder -> ObjectUtils.equals(schemeOrder.getProjSchemeId(), scheme.getId())).collect(Collectors.toList());
            List<EmcOrderBean> curOrderList = orderList.stream().filter(order -> ObjectUtils.equals(order.getFolderId(), folderId)).collect(Collectors.toList());

            List<EmcOrderTaskResultBean> curResultList = new ArrayList<>();
            List<EmcOrderTaskBean> curNewTaskList = createOrderTaskListSp(scheme, curOrderList, curSchemeOrderList, testMethodList, itemList, testCapacityList, testOrderList, curResultList, indexSetList, indexTestItemList);
            curNewTaskList.forEach(task -> task.setStatus(newTaskStatus));
            newOrderTaskList.addAll(curNewTaskList);
            newTaskResultList.addAll(curResultList);

            List<EmcOrderItemGroupBean> orderItemGroupList = this.generateOrderItemGroup(curOrderList, curSchemeOrderList, orderTaskList);
            newOrderItemGroupList.addAll(orderItemGroupList);
        });

        orderTaskService.getDao().insert(newOrderTaskList);
        orderTaskResultService.getDao().insert(newTaskResultList);
        orderItemGroupService.getDao().insert(newOrderItemGroupList);

        //给日志信息
        //登陆人信息
        //项目信息(需要替换projID)
        long projectEditMatchId = folderList.get(0).getProjId() ;
        if(ObjectUtils.isEmpty(projectEditMatchId)){
            return;
        }
        EmcProjectBean projectEditMatch = projectService.getDao().selectById(projectEditMatchId);
        //操作信息(根据业务种类不同区分)
        //resource层的备注 +
        EmcProjectEditLogBean editLogBean = new EmcProjectEditLogBean();
        editLogBean.setId(ApplicationContextHelper.getNextIdentity());
        editLogBean.setIsSample(projectEditMatch.getSample());
        editLogBean.setOperateInfo("项目方案修改-从方案同步测试");
        editLogBean.setOrgId(parentOrgId);
        editLogBean.setProjId(projectEditMatch.getId());
        editLogBean.setProjNo(projectEditMatch.getProjNo());
        editLogBean.setCreatedById(LocalContextHelper.getLoginUserId());
        editLogBean.setCreatedByName(LocalContextHelper.getLoginUserName());
        editLogBean.setCreatedTime(LocalDateTime.now());
        editLogBean.setCreatedByOrgId(LocalContextHelper.getLoginOrgId());
        editLogBean.setCreatedByOrgName(LocalContextHelper.getLoginOrgName());
        projectEditLogService.getDao().insert(editLogBean);
    }

    @Override
    public void completeFolder(RestJsonWrapperBean wrapper) {
        List<EmcFolderBean> folderList = wrapper.parse(EmcFolderBean.class);
        List<Long> folderIdList = folderList.stream().map(EmcFolderBean ::getId).distinct().collect(Collectors.toList());

        List<EmcFolderBean> folderMatchList = folderService.selectListByIds(folderIdList);
        List<Long> projIdList = folderMatchList.stream().map(EmcFolderBean ::getProjId).distinct().collect(Collectors.toList());
        List<EmcProjectBean> projectBeans = projectService.selectListByIds(projIdList);
        EmcProjectBean projectBean = projectBeans.get(0);
        folderMatchList.forEach(f ->{
            f.setStatus("done");
        });
        folderService.getDao().update(folderMatchList,"status");
        projectBean.setStatus("reportEdit");
        projectService.getDao().update(projectBean,"status");

        //给日志信息
        //登陆人信息
        CoreUserBean user = LocalContextHelper.getLoginUser();
        String parentOrgId = user.getExt$Item("parentorgid");
        //项目信息(需要替换projID)
        long projectEditMatchId = folderList.get(0).getProjId() ;
        if(ObjectUtils.isEmpty(projectEditMatchId)){
            return;
        }
        EmcProjectBean projectEditMatch = projectService.getDao().selectById(projectEditMatchId);
        //操作信息(根据业务种类不同区分)
        //resource层的备注 +
        EmcProjectEditLogBean editLogBean = new EmcProjectEditLogBean();
        editLogBean.setId(ApplicationContextHelper.getNextIdentity());
        editLogBean.setIsSample(projectEditMatch.getSample());
        editLogBean.setOperateInfo("项目方案修改-完成点位");
        editLogBean.setOrgId(parentOrgId);
        editLogBean.setProjId(projectEditMatch.getId());
        editLogBean.setProjNo(projectEditMatch.getProjNo());
        editLogBean.setCreatedById(LocalContextHelper.getLoginUserId());
        editLogBean.setCreatedByName(LocalContextHelper.getLoginUserName());
        editLogBean.setCreatedTime(LocalDateTime.now());
        editLogBean.setCreatedByOrgId(LocalContextHelper.getLoginOrgId());
        editLogBean.setCreatedByOrgName(LocalContextHelper.getLoginOrgName());
        projectEditLogService.getDao().insert(editLogBean);
    }

    @Override
    public void cancelFolder(RestJsonWrapperBean wrapper) {
        List<EmcFolderBean> folderList = wrapper.parse(EmcFolderBean.class);
        //取消点位完成状态
        List<Long> folderIdList = folderList.stream().map(EmcFolderBean ::getId).distinct().collect(Collectors.toList());

        List<EmcFolderBean> folderMatchList = folderService.selectListByIds(folderIdList);
        List<Long> projIdList = folderMatchList.stream().map(EmcFolderBean ::getProjId).distinct().collect(Collectors.toList());
        List<EmcProjectBean> projectBeans = projectService.selectListByIds(projIdList);
        EmcProjectBean projectBean = projectBeans.get(0);
        folderMatchList.forEach(f ->{
            f.setStatus("draw");
        });
        //如果还有其他点位在报告编制，项目状态就不需要更改
        folderService.getDao().update(folderMatchList,"status");
        List<EmcFolderBean> folderAllList = folderService.getDao().selectListByOneColumnValues(projIdList,"projId");
        List<String>  statusList = folderAllList.stream().map(EmcFolderBean :: getStatus).distinct().collect(Collectors.toList());
        if(!statusList.contains("done")){
            projectBean.setStatus("auditFinished");
            projectService.getDao().update(projectBean,"status");
        }

        //给日志信息
        //登陆人信息
        CoreUserBean user = LocalContextHelper.getLoginUser();
        String parentOrgId = user.getExt$Item("parentorgid");
        //项目信息(需要替换projID)
        long projectEditMatchId = folderList.get(0).getProjId() ;
        if(ObjectUtils.isEmpty(projectEditMatchId)){
            return;
        }
        EmcProjectBean projectEditMatch = projectService.getDao().selectById(projectEditMatchId);
        //操作信息(根据业务种类不同区分)
        //resource层的备注 +
        EmcProjectEditLogBean editLogBean = new EmcProjectEditLogBean();
        editLogBean.setId(ApplicationContextHelper.getNextIdentity());
        editLogBean.setIsSample(projectEditMatch.getSample());
        editLogBean.setOperateInfo("项目方案修改-取消完成点位");
        editLogBean.setOrgId(parentOrgId);
        editLogBean.setProjId(projectEditMatch.getId());
        editLogBean.setProjNo(projectEditMatch.getProjNo());
        editLogBean.setCreatedById(LocalContextHelper.getLoginUserId());
        editLogBean.setCreatedByName(LocalContextHelper.getLoginUserName());
        editLogBean.setCreatedTime(LocalDateTime.now());
        editLogBean.setCreatedByOrgId(LocalContextHelper.getLoginOrgId());
        editLogBean.setCreatedByOrgName(LocalContextHelper.getLoginOrgName());
        projectEditLogService.getDao().insert(editLogBean);
    }

    @Override
    public void completeFolderForOrder(RestJsonWrapperBean wrapper) {
        List<EmcOrderBean> orderList = wrapper.parse(EmcOrderBean.class);
        List<Long> orderIdList = orderList.stream().map(EmcOrderBean ::getId).distinct().collect(Collectors.toList());
        List<EmcOrderBean> orderMatchList = orderService.getDao().selectListByIds(orderIdList);
        List<Long> folderIdList = orderMatchList.stream().map(EmcOrderBean :: getFolderId).distinct().collect(Collectors.toList());
        List<EmcFolderBean> folderMatchList = folderService.getDao().selectListByIds(folderIdList);
        List<Long> projIdList = folderMatchList.stream().map(EmcFolderBean ::getProjId).distinct().collect(Collectors.toList());
        List<EmcProjectBean> projectBeans = projectService.selectListByIds(projIdList);
        EmcProjectBean projectBean = projectBeans.get(0);
        folderMatchList.forEach(f ->{
            f.setStatus("done");
        });
        folderService.getDao().update(folderMatchList,"status");
        projectBean.setStatus("reportEdit");
        projectService.getDao().update(projectBean,"status");
        //给日志信息
        //登陆人信息
        CoreUserBean user = LocalContextHelper.getLoginUser();
        String parentOrgId = user.getExt$Item("parentorgid");
        //项目信息(需要替换projID)
        long projectEditMatchId = orderList.get(0).getProjId() ;
        if(ObjectUtils.isEmpty(projectEditMatchId)){
            return;
        }
        EmcProjectBean projectEditMatch = projectService.getDao().selectById(projectEditMatchId);
        //操作信息(根据业务种类不同区分)
        //resource层的备注 +
        EmcProjectEditLogBean editLogBean = new EmcProjectEditLogBean();
        editLogBean.setId(ApplicationContextHelper.getNextIdentity());
        editLogBean.setIsSample(projectEditMatch.getSample());
        editLogBean.setOperateInfo("项目方案修改-完成点位按样品");
        editLogBean.setOrgId(parentOrgId);
        editLogBean.setProjId(projectEditMatch.getId());
        editLogBean.setProjNo(projectEditMatch.getProjNo());
        editLogBean.setCreatedById(LocalContextHelper.getLoginUserId());
        editLogBean.setCreatedByName(LocalContextHelper.getLoginUserName());
        editLogBean.setCreatedTime(LocalDateTime.now());
        editLogBean.setCreatedByOrgId(LocalContextHelper.getLoginOrgId());
        editLogBean.setCreatedByOrgName(LocalContextHelper.getLoginOrgName());
        projectEditLogService.getDao().insert(editLogBean);

    }

    @Override
    public void cancelFolderForOrder(RestJsonWrapperBean wrapper) {
        List<EmcOrderBean> orderList = wrapper.parse(EmcOrderBean.class);
        List<Long> orderIdList = orderList.stream().map(EmcOrderBean ::getId).distinct().collect(Collectors.toList());
        List<EmcOrderBean> orderMatchList = orderService.getDao().selectListByIds(orderIdList);
        List<Long> folderIdList = orderMatchList.stream().map(EmcOrderBean :: getFolderId).distinct().collect(Collectors.toList());
        List<EmcFolderBean> folderMatchList = folderService.getDao().selectListByIds(folderIdList);
        List<Long> projIdList = folderMatchList.stream().map(EmcFolderBean ::getProjId).distinct().collect(Collectors.toList());

        List<EmcProjectBean> projectBeans = projectService.selectListByIds(projIdList);
        EmcProjectBean projectBean = projectBeans.get(0);
        folderMatchList.forEach(f ->{
            f.setStatus("draw");
        });
        folderService.getDao().update(folderMatchList,"status");

        List<EmcFolderBean> folderAllList = folderService.getDao().selectListByOneColumnValues(projIdList,"projId");
        List<String>  statusList = folderAllList.stream().map(EmcFolderBean :: getStatus).distinct().collect(Collectors.toList());
        if(!statusList.contains("done")) {
            projectBean.setStatus("auditFinished");
            projectService.getDao().update(projectBean,"status");
        }
        //给日志信息
        //登陆人信息
        CoreUserBean user = LocalContextHelper.getLoginUser();
        String parentOrgId = user.getExt$Item("parentorgid");
        //项目信息(需要替换projID)
        long projectEditMatchId = orderList.get(0).getProjId() ;
        if(ObjectUtils.isEmpty(projectEditMatchId)){
            return;
        }
        EmcProjectBean projectEditMatch = projectService.getDao().selectById(projectEditMatchId);
        //操作信息(根据业务种类不同区分)
        //resource层的备注 +
        EmcProjectEditLogBean editLogBean = new EmcProjectEditLogBean();
        editLogBean.setId(ApplicationContextHelper.getNextIdentity());
        editLogBean.setIsSample(projectEditMatch.getSample());
        editLogBean.setOperateInfo("项目方案修改-取消完成点位按样品");
        editLogBean.setOrgId(parentOrgId);
        editLogBean.setProjId(projectEditMatch.getId());
        editLogBean.setProjNo(projectEditMatch.getProjNo());
        editLogBean.setCreatedById(LocalContextHelper.getLoginUserId());
        editLogBean.setCreatedByName(LocalContextHelper.getLoginUserName());
        editLogBean.setCreatedTime(LocalDateTime.now());
        editLogBean.setCreatedByOrgId(LocalContextHelper.getLoginOrgId());
        editLogBean.setCreatedByOrgName(LocalContextHelper.getLoginOrgName());
        projectEditLogService.getDao().insert(editLogBean);
    }

    @Override
    public String removeOrderTask(RestJsonWrapperBean wrapper) {
        //成功返回true,错误返回空，有已经健批的返回测试
        String note ="true";
        //移除测试的样品与接收测试的样品是一对一的关系(且不能重复)
        //选中的样品测试 --需要移除的
        List<EmcOrderTaskBean> orderTaskList =wrapper.parse(EmcOrderTaskBean.class);
        List<Long> orderTaskIdList = orderTaskList.stream().map(EmcOrderTaskBean ::getId).distinct().collect(Collectors.toList());
        List<Long> testMethodIdList = orderTaskList.stream().map(EmcOrderTaskBean ::getTestMethodId).distinct().collect(Collectors.toList());
        List<String> itemGroupList = new ArrayList<>();
        //原始orderId
        Long oldOrderId =orderTaskList.get(0).getOrderId();
        //该样品下所有的测试
        List<EmcOrderTaskBean> orderAllTaskList = orderTaskService.getDao().selectListByOneColumnValue(oldOrderId.toString(),"orderId");
        //要被移除的测试
        List<EmcOrderTaskBean> orderTaskCurrentList = orderTaskService.selectListByIds(orderTaskIdList);
        //若有测试已经建批，不允许移除
        List<String> runIdList = orderTaskCurrentList.stream().map(EmcOrderTaskBean ::getRunId)
                .filter(t ->!StringUtils.isEmpty(t)).distinct().collect(Collectors.toList());
        if(!runIdList.isEmpty()){
            //有批的测试
            List<EmcOrderTaskBean> orderTaskInRunList = orderTaskCurrentList.stream().filter(ok ->runIdList.contains(ok.getRunId())).collect(Collectors.toList());;
            List<Long> testIdList = orderTaskInRunList.stream().map(EmcOrderTaskBean ::getTestId).distinct().collect(Collectors.toList());
            List<EmcTestBean> testBeanList = testService.selectListByIds(testIdList);
            List<String> testNameList = testBeanList.stream().map(EmcTestBean::getTestName).distinct().collect(Collectors.toList());
            String inrunTests = StringUtils.join(testNameList,",");
            return  inrunTests;
        }
        if(orderTaskList.size() == orderAllTaskList.size()){
            return "";
        }
        orderTaskList.forEach(ok ->{
            itemGroupList.add(ok.getExt$Item("itemgroup"));
        });
        //样品选择的样品-- 新增移除测试
        EmcOrderBean acceptTestOrder = wrapper.parseUnique(EmcOrderBean.class);
        List<EmcOrderTaskBean> orderExistTaskList = orderTaskService.getDao().selectListByOneColumnValue(acceptTestOrder.getId().toString(),"orderId");
        List<Long> testMethodIdExistList = orderExistTaskList.stream().map(EmcOrderTaskBean ::getTestMethodId).distinct().collect(Collectors.toList());
        //testMethodIdList 需要过滤掉接受的样品已存在的样品测试
        List<Long> testMethodIdMatchList = testMethodIdList.stream().filter(t ->!testMethodIdExistList.contains(t)).collect(Collectors.toList());
        orderTaskList.forEach(ok ->{
            ok.setOrderId(acceptTestOrder.getId());
        });

        //对应的样品分析项组表
        List<EmcOrderItemGroupBean> removeOrderItemGroupList = orderItemGroupService.selectListByFilter(SearchFilter.instance()
                .match("orderId",oldOrderId ).filter(MatchPattern.OR).match("itemgroup", itemGroupList).filter(MatchPattern.OR)
                .match("testMethodId", testMethodIdMatchList).filter(MatchPattern.OR));
        List<EmcOrderItemGroupBean> allOrderItemGroupList = orderItemGroupService.selectListByFilter(SearchFilter.instance()
                .match("orderId", oldOrderId).filter(MatchPattern.OR)
                .match("testMethodId", testMethodIdMatchList).filter(MatchPattern.OR));
/*        if(removeOrderItemGroupList.size() == allOrderItemGroupList.size()){
            return "false";
        }*/

        List<Long>  removeOrderItemGroupIdList = removeOrderItemGroupList.stream().map(EmcOrderItemGroupBean :: getId).distinct().collect(Collectors.toList());
        //对应的样品分析项表
        List<EmcOrderItemBean> removeOrderItemList = orderItemService.getDao().selectListByOneColumnValues(removeOrderItemGroupIdList,"orderItemGroupId");
        removeOrderItemList.forEach(oi ->{
            oi.setOrderId(acceptTestOrder.getId());
        });
        removeOrderItemGroupList.forEach(oig ->{
            oig.setOrderId(acceptTestOrder.getId());
        });
        orderTaskService.getDao().update(orderTaskList,"orderId");
        orderItemGroupService.getDao().update(removeOrderItemGroupList,"orderId");
        orderItemService.getDao().update(removeOrderItemList,"orderId");

        //给日志信息
        //登陆人信息
        CoreUserBean user = LocalContextHelper.getLoginUser();
        String parentOrgId = user.getExt$Item("parentorgid");
        //项目信息(需要替换projID)
        long projectEditMatchId = orderTaskList.get(0).getProjId() ;
        if(ObjectUtils.isEmpty(projectEditMatchId)){
            return note;
        }
        EmcProjectBean projectEditMatch = projectService.getDao().selectById(projectEditMatchId);
        //操作信息(根据业务种类不同区分)
        //resource层的备注 +
        EmcProjectEditLogBean editLogBean = new EmcProjectEditLogBean();
        editLogBean.setId(ApplicationContextHelper.getNextIdentity());
        editLogBean.setIsSample(projectEditMatch.getSample());
        editLogBean.setOperateInfo("项目方案修改-从样品移除测试");
        editLogBean.setOrgId(parentOrgId);
        editLogBean.setProjId(projectEditMatch.getId());
        editLogBean.setProjNo(projectEditMatch.getProjNo());
        editLogBean.setCreatedById(LocalContextHelper.getLoginUserId());
        editLogBean.setCreatedByName(LocalContextHelper.getLoginUserName());
        editLogBean.setCreatedTime(LocalDateTime.now());
        editLogBean.setCreatedByOrgId(LocalContextHelper.getLoginOrgId());
        editLogBean.setCreatedByOrgName(LocalContextHelper.getLoginOrgName());
        projectEditLogService.getDao().insert(editLogBean);
        return note;
    }

    @Override
    public Page<EmcProjOrderBean> selectProjOrderProjectPagationByProjScheme(Long id, RestJsonWrapperBean wrapper) {
        return projOrderService.selectPaginationByFilter(SearchFilter.instance().match("PROJID", id).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    public Page<EmcProjOrderBean> selectProjOrderOrderPagationByProjScheme(Long id, RestJsonWrapperBean wrapper) {
        return projOrderService.selectPaginationByFilter(SearchFilter.instance().match("PROJSCHEMEID", id).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    public void syncNewProjScheme(List<EmcProjSchemeBean> schemeList) {
        List<EmcProjSchemeBean> newSchemeList = schemeList.stream().filter(scheme -> StringUtils.equals(scheme.getExt$Item("status"), Constant.YES)).collect(Collectors.toList());
        if (newSchemeList.isEmpty()) {
            return;
        }

        // 生成主业务流程数据
        this.insertBpDataByScheme(newSchemeList);

        // 如果新增了样品类型，则向报告表增加一条数据
        List<EmcReportBean> reportList = reportService.getDao().selectListByOneColumnValue(newSchemeList.get(0).getProjId(), "projId", Arrays.asList("id", "monitorTmplId"));
        List<String> reportMonitorTmplIdList = reportList.stream().filter(r -> !StringUtils.isEmpty(r.getMonitorTmplId())).map(EmcReportBean::getMonitorTmplId).distinct().collect(Collectors.toList());
        List<EmcProjSchemeBean> newOrderCategorySchemeList = newSchemeList.stream().filter(s -> !reportMonitorTmplIdList.contains(s.getMonitorTmplId())).collect(Collectors.toList());
        if (!newOrderCategorySchemeList.isEmpty()) {
            List<EmcReportBean> newReportList = new ArrayList<>(newOrderCategorySchemeList.size());
            newOrderCategorySchemeList.forEach(scheme -> {
                EmcReportBean newReport = new EmcReportBean();
                newReport.setId(ApplicationContextHelper.getNextIdentity());
                newReport.setProjId(newSchemeList.get(0).getProjId());
                newReport.setMonitorTmplId(scheme.getMonitorTmplId());
                newReport.setOrderCategory(scheme.getExt$Item("monitortmplname"));
                newReport.setStatus("draft");
                newReport.setProcessStatus("draft");
                newReportList.add(newReport);
            });
            reportService.getDao().insert(newReportList);
        }

        // 重置监测方案的状态
        newSchemeList.forEach(scheme -> scheme.setExt$Item("status", null));
        this.getDao().update(newSchemeList, "status");

        List<Long> newSchemeIdList = newSchemeList.stream().map(EmcProjSchemeBean::getId).collect(Collectors.toList());
        List<EmcProjOrderBean> newProjOrderList = projOrderService.getDao().selectListByOneColumnValues(newSchemeIdList, "projSchemeId", Collections.singletonList("id"));
        newProjOrderList.forEach(order -> order.setExt$Item("status", null));
        projOrderService.getDao().update(newProjOrderList, "status");

        List<Long> validProjectIdList = newSchemeList.stream().map(EmcProjSchemeBean::getProjId).distinct().collect(Collectors.toList());
        List<EmcFolderBean> folderList = folderService.getDao().selectListByOneColumnValues(validProjectIdList, "projId");

        // 对于新生成的点位，采样日期、现场负责人和采样人设置为和其他点位的一致
        List<String> folderNoList = newSchemeList.stream().map(EmcProjSchemeBean::getFolderNo).collect(Collectors.toList());
        List<EmcFolderBean> newFolderList = folderList.stream().filter(folder -> folderNoList.contains(folder.getFolderNo())).collect(Collectors.toList());
        if (newFolderList.isEmpty()) {
            return;
        }

        List<EmcFolderBean> originalFolderList = folderList.stream().filter(folder -> !folderNoList.contains(folder.getFolderNo())).collect(Collectors.toList());
        newFolderList.forEach(folder -> originalFolderList.stream().filter(originalFolder -> !StringUtils.isEmpty(originalFolder.getSceneUserId())
                && !StringUtils.isEmpty(originalFolder.getSampleUserId())
                && !ObjectUtils.isEmpty(originalFolder.getPlanSampleDate())
                && !StringUtils.equals(originalFolder.getStatus(), "draft")).findFirst().ifPresent(originalFolder -> {
            folder.setPlanSampleDate(originalFolder.getPlanSampleDate());
            folder.setSceneUserId(originalFolder.getSceneUserId());
            folder.setSceneUserName(originalFolder.getSceneUserName());
            folder.setSampleUserId(originalFolder.getSampleUserId());
            folder.setSampleUserName(originalFolder.getSampleUserName());
        }));

        // 如果新点位没有设置采样日期、现场负责人和采样人(实际可只用判断一个字段)，说明该任务下其他点位都位于采样安排，则不自动提交新点位
        List<EmcFolderBean> submitFolderList = newFolderList.stream().filter(folder -> !StringUtils.isEmpty(folder.getSceneUserId())).collect(Collectors.toList());
        if (submitFolderList.isEmpty()) {
            return;
        }

        List<Long> submitFolderIdList = submitFolderList.stream().map(EmcFolderBean::getId).collect(Collectors.toList());
        CoreBpmnParameterDTO parameter = new CoreBpmnParameterDTO();
        parameter.setComment("方案修改提交");

        folderService.getDao().update(submitFolderList, "planSampleDate", "sceneUserId", "sceneUserName", "sampleUserId", "sampleUserName");
        folderService.startProcess(submitFolderIdList, parameter);
    }

    @Override
    public void syncNewProjOrder(List<EmcProjSchemeBean> schemeList) {
        List<EmcProjSchemeBean> originalSchemeList = schemeList.stream().filter(scheme -> !StringUtils.equals(scheme.getExt$Item("status"), Constant.YES)).collect(Collectors.toList());
        if (originalSchemeList.isEmpty()) {
            return;
        }

        CoreUserBean user = LocalContextHelper.getLoginUser();
        //赋值所属单位和流程类型
        String parentOrgId = user.getExt$Item("parentorgid");

        List<Long> schemeIdList = originalSchemeList.stream().map(EmcProjSchemeBean::getId).collect(Collectors.toList());
        List<EmcProjOrderBean> schemeOrderList = schemeIdList.isEmpty() ? Collections.emptyList() : projOrderService.selectListByFilter(SearchFilter.instance().match("projSchemeId", schemeIdList).filter(MatchPattern.OR));
        List<EmcProjOrderBean> newSchemeOrderList = schemeOrderList.stream().filter(test -> StringUtils.equals(test.getExt$Item("status"), Constant.YES)).collect(Collectors.toList());

        if (newSchemeOrderList.isEmpty()) {
            return;
        }

        List<Long> curSchemeIdList = newSchemeOrderList.stream().map(EmcProjOrderBean::getProjSchemeId).distinct().collect(Collectors.toList());
        List<EmcProjSchemeBean> curSchemeList = schemeList.stream().filter(scheme -> curSchemeIdList.contains(scheme.getId())).collect(Collectors.toList());
        List<String> folderNoList = curSchemeList.stream().map(EmcProjSchemeBean::getFolderNo).collect(Collectors.toList());

        List<EmcFolderBean> folderList = folderService.getDao().selectListByOneColumnValues(folderNoList, "folderNo");
        boolean folderDone = folderList.stream().anyMatch(folder -> StringUtils.equals(EmcBizConstant.FOLDER_PROCESSSTATUS_DONE, folder.getStatus()));

        if (folderDone) {
            return;
        }

        // 查询测试方法和监测能力库方法
        List<Long> capacityItemGroupIdList = newSchemeOrderList.stream().map(EmcProjOrderBean::getItemGroupId).distinct().collect(Collectors.toList());
        List<EmcCapacityItemGroupBean> capacityItemGroupList = capacityItemGroupService.getDao().selectListByOneColumnValues(capacityItemGroupIdList, "id");
        List<Long> testCapacityIdList = capacityItemGroupList.stream().map(EmcCapacityItemGroupBean::getTestCapacityId).collect(Collectors.toList());
        List<Long> testMethodIdList = capacityItemGroupList.stream().map(EmcCapacityItemGroupBean::getTestMethodId).collect(Collectors.toList());

        List<EmcTestMethodBean> testMethodList = testMethodService.getDao().selectListByIds(testMethodIdList);
        List<EmcTestCapacityBean> testCapacityList = testCapacityService.getDao().selectListByIds(testCapacityIdList);

        List<Long> itemGroupIdList = capacityItemGroupList.stream().map(EmcCapacityItemGroupBean::getItemGroupId).collect(Collectors.toList());
        List<EmcGroupItemBean> itemList = itemGroupIdList.isEmpty() ? Collections.emptyList() : groupItemService.selectListByFilter(SearchFilter.instance().match("ITEMGROUPID", itemGroupIdList).filter(MatchPattern.OR));
        itemList.forEach(item -> capacityItemGroupList.stream().filter(group -> ObjectUtils.equals(item.getItemGroupId(), group.getItemGroupId())).findFirst().ifPresent(group -> item.setExt$Item("capacityItemGroupId", group.getId() + "")));

        List<EmcTestMethodOrderCategoryBean> testOrderList = testMethodIdList.isEmpty() ? Collections.emptyList() : testMethodOrderCategoryService
                .selectListByFilter(SearchFilter.instance().match("TESTMETHODID", testMethodIdList).filter(MatchPattern.OR)
                        .match("ORGID", parentOrgId).filter(MatchPattern.OR));
        // 指标项目
        List<String> indexIdList = newSchemeOrderList.stream().map(EmcProjOrderBean::getIndexId).distinct().collect(Collectors.toList());
        List<EmcIndexSetBean> indexSetList = indexIdList.isEmpty() ? Collections.emptyList() : indexSetService.selectListByFilter(SearchFilter.instance().match("INDEXID", indexIdList).filter(MatchPattern.OR));
        List<Long> indexItemIdList = indexSetList.stream().map(EmcIndexSetBean::getIndexItemId).distinct().collect(Collectors.toList());
        List<EmcIndexTestItemBean> indexTestItemList = indexTestItemService.getDao().selectListByOneColumnValues(indexItemIdList, "indexItemId");

        Map<String, EmcFolderBean> folderNoMap = folderList.stream().collect(Collectors.toMap(EmcFolderBean::getFolderNo, Function.identity(), (v1, v2) -> v1));

        List<Long> folderIdList = folderList.stream().map(EmcFolderBean::getId).collect(Collectors.toList());
        List<EmcOrderBean> orderList = orderService.getDao().selectListByOneColumnValues(folderIdList, "folderId");
        List<EmcOrderTaskBean> orderTaskList = orderTaskService.getDao().selectListByOneColumnValues(folderIdList, "folderId");

        List<EmcOrderTaskBean> newOrderTaskList = new ArrayList<>();
        List<EmcOrderTaskResultBean> newTaskResultList = new ArrayList<>();
        List<EmcOrderItemGroupBean> newOrderItemGroupList = new ArrayList<>();

        List<String> defaultTaskStatusList = Arrays.asList("draft", "preLogged", "logged","done");

        curSchemeList.forEach(scheme -> {
            EmcFolderBean curFolder = folderNoMap.get(scheme.getFolderNo());
            Long folderId = curFolder.getId();
            List<String> taskStatusList = orderTaskList.stream().filter(task -> ObjectUtils.equals(task.getFolderId(), folderId)).map(EmcOrderTaskBean::getStatus).distinct().collect(Collectors.toList());
            List<Integer> statusIndexList = taskStatusList.stream().mapToInt(defaultTaskStatusList::indexOf).sorted().boxed().collect(Collectors.toList());
            String newTaskStatus = (statusIndexList.isEmpty() || statusIndexList.get(0) == -1) ? "logged" : defaultTaskStatusList.get(statusIndexList.get(0));

            List<EmcProjOrderBean> curSchemeOrderList = newSchemeOrderList.stream().filter(schemeOrder -> ObjectUtils.equals(schemeOrder.getProjSchemeId(), scheme.getId())).collect(Collectors.toList());
            List<EmcOrderBean> curOrderList = orderList.stream().filter(order -> ObjectUtils.equals(order.getFolderId(), folderId)).collect(Collectors.toList());

            List<EmcOrderTaskResultBean> curResultList = new ArrayList<>();
            List<EmcOrderTaskBean> curNewTaskList = createOrderTaskList(scheme, curOrderList, curSchemeOrderList, testMethodList, itemList, testCapacityList, testOrderList, curResultList, indexSetList, indexTestItemList);
            curNewTaskList.forEach(task -> task.setStatus(newTaskStatus));
            newOrderTaskList.addAll(curNewTaskList);
            newTaskResultList.addAll(curResultList);

            List<EmcOrderItemGroupBean> orderItemGroupList = this.generateOrderItemGroup(curOrderList, curSchemeOrderList, orderTaskList);
            newOrderItemGroupList.addAll(orderItemGroupList);
        });

        orderTaskService.getDao().insert(newOrderTaskList);
        orderTaskResultService.getDao().insert(newTaskResultList);

        newOrderItemGroupList.forEach(group -> {
            group.setQcCategoryCode(null);
            group.setQcName(null);
        });
        orderItemGroupService.getDao().insert(newOrderItemGroupList);

        newSchemeOrderList.forEach(schemeOrder -> schemeOrder.setExt$Item("status", null));
        projOrderService.getDao().update(newSchemeOrderList, "status");
    }

    @Override
    public void deleteSyncProjScheme(List<EmcProjSchemeBean> schemeList) {
        List<String> folderNoList = schemeList.stream().map(EmcProjSchemeBean::getFolderNo).collect(Collectors.toList());
        List<EmcFolderBean> folderList = folderService.getDao().selectListByOneColumnValues(folderNoList, "folderNo", Arrays.asList("id", "folderNo"));
        List<Long> folderIdList = folderList.stream().map(EmcFolderBean::getId).collect(Collectors.toList());

        // 删除点位
        folderService.getDao().deleteByIdList(folderIdList);

        // 删除点位下的样品
        List<EmcOrderBean> orderList = orderService.getDao().selectListByOneColumnValues(folderIdList, "folderId", Collections.singletonList("id"));
        List<Long> orderIdList = orderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList());
        orderService.getDao().deleteByIdList(orderIdList);

        // 删除样品下分析项组
        List<EmcOrderItemGroupBean> deleteOrderItemGroupList = new ArrayList<>(orderIdList.size());
        orderIdList.forEach(orderId -> {
            EmcOrderItemGroupBean deleteOrderItemGroup = new EmcOrderItemGroupBean();
            deleteOrderItemGroup.setOrderId(orderId);
            deleteOrderItemGroupList.add(deleteOrderItemGroup);
        });
        orderItemGroupService.getDao().deleteBy(deleteOrderItemGroupList, "orderId");

        // 删除样品测试
        List<EmcOrderTaskBean> deleteOrderTaskList = new ArrayList<>(folderIdList.size());
        folderIdList.forEach(folderId -> {
            EmcOrderTaskBean deleteOrderTask = new EmcOrderTaskBean();
            deleteOrderTask.setFolderId(folderId);
            deleteOrderTaskList.add(deleteOrderTask);
        });
        orderTaskService.getDao().deleteBy(deleteOrderTaskList, "folderId");

        // 删除样品测试结果
        List<EmcOrderTaskResultBean> deleteTaskResultList = new ArrayList<>(folderIdList.size());
        folderIdList.forEach(folderId -> {
            EmcOrderTaskResultBean deleteTaskResult = new EmcOrderTaskResultBean();
            deleteTaskResult.setFolderId(folderId);
            deleteTaskResultList.add(deleteTaskResult);
        });
        orderTaskResultService.getDao().deleteBy(deleteTaskResultList, "folderId");
    }

    @Override
    public void deleteSyncProjOrder(List<EmcProjSchemeBean> schemeList, List<EmcProjOrderBean> projOrderList) {
        List<String> folderNoList = schemeList.stream().map(EmcProjSchemeBean::getFolderNo).collect(Collectors.toList());
        List<EmcFolderBean> folderList = folderService.getDao().selectListByOneColumnValues(folderNoList, "folderNo", Arrays.asList("id", "folderNo"));
        List<Long> folderIdList = folderList.stream().map(EmcFolderBean::getId).collect(Collectors.toList());
        List<EmcOrderBean> orderList = orderService.getDao().selectListByOneColumnValues(folderIdList, "folderId");

        List<Long> orderIdList = orderList.stream().map(EmcOrderBean :: getId).distinct().collect(Collectors.toList());
        List<Long> testMethodIdList = projOrderList.stream().map(projOrder -> projOrder.getExt$Item("testMehtodId")).distinct().filter(NumberUtils::isNumber).mapToLong(NumberUtils::parseLong).boxed().collect(Collectors.toList());
        List<String> itemGroupList = projOrderList.stream().map(EmcProjOrderBean::getItemGroup).distinct().collect(Collectors.toList());

        List<EmcOrderItemGroupBean> orderItemGroupList = orderItemGroupService.selectListByFilter(SearchFilter.instance().match("orderId", orderIdList).filter(MatchPattern.OR).match("testMethodId", testMethodIdList).filter(MatchPattern.OR));
        List<Long> deleteOrderItemGroupIdList = orderItemGroupList.stream().filter(group -> itemGroupList.contains(group.getItemGroup())).map(EmcOrderItemGroupBean::getId).collect(Collectors.toList());

        // 针对一个测试多个分析项组的，如果只删除了其中一个分析项组，那么ORDER TASK 数据不应该被删除
        List<EmcOrderItemGroupBean> retainOrderItemGroupList = orderItemGroupList.stream().filter(group -> !deleteOrderItemGroupIdList.contains(group.getId())).collect(Collectors.toList());
        List<EmcOrderTaskBean> allOrderTaskList = orderTaskService.selectListByFilter(SearchFilter.instance().match("orderId", orderIdList).filter(MatchPattern.OR).match("testMethodId", testMethodIdList).filter(MatchPattern.OR));
        List<EmcOrderTaskBean> deleteOrderTaskList = allOrderTaskList.stream().filter(task -> retainOrderItemGroupList.stream().noneMatch(group -> ObjectUtils.equals(task.getTestMethodId(), group.getTestMethodId()) && ObjectUtils.equals(task.getOrderId(), group.getOrderId()))).collect(Collectors.toList());

        orderItemGroupService.getDao().deleteByIdList(deleteOrderItemGroupIdList);

        List<Long> deleteOrderTaskIdList = deleteOrderTaskList.stream().map(EmcOrderTaskBean::getId).collect(Collectors.toList());
        orderTaskService.getDao().deleteByIdList(deleteOrderTaskIdList);

        List<EmcOrderTaskResultBean> deleteResultList = new ArrayList<>(deleteOrderTaskIdList.size());
        deleteOrderTaskIdList.forEach(taskId -> {
            EmcOrderTaskResultBean deleteResult = new EmcOrderTaskResultBean();
            deleteResult.setOrderTaskId(taskId);
            deleteResultList.add(deleteResult);
        });
        orderTaskResultService.getDao().deleteBy(deleteResultList, "orderTaskId");
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void move(RestJsonWrapperBean wrapper) {
        List<EmcProjSchemeBean> projSchemeList = wrapper.parse(EmcProjSchemeBean.class);
        List<Long> projSchemeIdList = projSchemeList.stream().map(EmcProjSchemeBean::getId).collect(Collectors.toList());
        List<EmcProjSchemeBean> findProjSchemeList = selectListByIds(projSchemeIdList);
        List<EmcProjSchemeBean> updateProjSchemeList = new ArrayList<>();
        projSchemeList.forEach(ps -> {
            EmcProjSchemeBean findBean = findProjSchemeList.stream().filter(f -> ObjectUtils.equals(ps.getId(), f.getId())).findFirst().orElse(null);
            if (!ObjectUtils.isEmpty(findBean)) {
                findBean.setSortNum(ps.getSortNum());
                updateProjSchemeList.add(findBean);
            }
        });
        getDao().update(updateProjSchemeList, "SORTNUM");
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void moveProjOrder(RestJsonWrapperBean wrapper) {
        List<EmcProjOrderBean> projOrderList = wrapper.parse(EmcProjOrderBean.class);
        List<Long> projOrderIdList = projOrderList.stream().map(EmcProjOrderBean::getId).collect(Collectors.toList());
        List<EmcProjOrderBean> findProjOrderList = projOrderService.selectListByIds(projOrderIdList);
        List<EmcProjOrderBean> updateProjOrderList = new ArrayList<>();
        projOrderList.forEach(po -> {
            EmcProjOrderBean findBean = findProjOrderList.stream().filter(f -> ObjectUtils.equals(f.getId(), po.getId())).findFirst().orElse(null);
            if (!ObjectUtils.isEmpty(findBean)) {
                findBean.setSortNum(po.getSortNum());
                updateProjOrderList.add(findBean);
            }
        });
        projOrderService.getDao().update(updateProjOrderList,"SORTNUM");
    }

    /**
     * 通过监测方案和监测方案样品生成气采样参数主业务流程数据
     */
    private void insertSampleParamData(List<EmcProjSchemeBean> schemeList){
        // 监测方案样品
        List<Long> schemeIdList = schemeList.stream().map(e -> e.getId()).collect(Collectors.toList());
        List<EmcProjOrderBean> orderList = projOrderService.selectListByFilter(SearchFilter.instance().match("PROJSCHEMEID", schemeIdList).filter(MatchPattern.OR)
                .match("SAMPLEPARAM","1").filter(MatchPattern.EQ));//筛选出气采样参数的分析项组
        if(!orderList.isEmpty()){
            // 分析项组分析项
            List<Long> capacityItemGroupIdList = orderList.stream().map(e -> e.getItemGroupId()).collect(Collectors.toList());
            List<EmcCapacityItemGroupBean> capacityItemGroupList = capacityItemGroupService.getDao().selectListByIds(capacityItemGroupIdList);
            List<Long> itemGroupIdList = capacityItemGroupList.stream().map(EmcCapacityItemGroupBean::getItemGroupId).distinct().collect(Collectors.toList());

            List<Long> testCapacityIdList = capacityItemGroupList.stream().map(EmcCapacityItemGroupBean::getTestCapacityId).distinct().collect(Collectors.toList());

            List<EmcGroupItemBean> itemList = groupItemService.selectListByFilter(SearchFilter.instance().match("ITEMGROUPID", itemGroupIdList).filter(MatchPattern.OR)
                    .match("SAMPLEPARAM","1").filter(MatchPattern.EQ));//筛选出气采样参数的分析项

            List<EmcItemGroupBean> filterItemGroupList = new ArrayList<>();
            capacityItemGroupList.forEach(group -> {
                EmcItemGroupBean itemGroup = new EmcItemGroupBean();
                itemGroup.setTestMethodId(group.getTestMethodId());
                itemGroup.setItemGroup(group.getItemGroup());
                filterItemGroupList.add(itemGroup);
            });
            List<EmcItemGroupBean> itemGroupList = itemGroupService.getDao().selectList(filterItemGroupList, Arrays.asList("testMethodId", "itemGroup"), Arrays.asList("id", "testMethodId", "itemGroup"));
            itemGroupList.forEach(group -> capacityItemGroupList.stream().filter(g -> ObjectUtils.equals(g.getTestMethodId(), group.getTestMethodId()) && StringUtils.equals(g.getItemGroup(), group.getItemGroup())).findFirst().ifPresent(g -> group.setExt$Item("capacityItemGroupId", g.getId() + "")));

            itemList.forEach(item -> itemGroupList.stream().filter(group -> ObjectUtils.equals(item.getItemGroupId(), group.getId())).findFirst().ifPresent(group -> item.setExt$Item("capacityItemGroupId", group.getExt$Item("capacityItemGroupId"))));

            if(!itemList.isEmpty()){
                // 监测点
                List<Long> monitorPointIdList = schemeList.stream().map(e -> e.getMonitorPointId()).filter(e -> !ObjectUtils.isEmpty(e)).collect(Collectors.toList());
                List<EmcMonitorPointBean> pointList = monitorPointService.selectListByIds(monitorPointIdList);
                // 测试方法
                List<Long> testIdList = orderList.stream().map(e -> e.getTestId()).collect(Collectors.toList());
                List<EmcTestMethodBean> testMethodList = testMethodService.selectListByFilter(SearchFilter.instance().match("TESTID", testIdList).filter(MatchPattern.OR)
                        .match("SAMPLEPARAM","1").filter(MatchPattern.EQ));//筛选出气采样参数的测试项目

                // 监测能力方法
                List<EmcTestCapacityBean> testCapacityList = testCapacityService.selectListByIds(testCapacityIdList);
                // 测试样品类型
                List<Long> testMethodIdList = testMethodList.stream().map(e -> e.getId()).collect(Collectors.toList());
                List<EmcTestMethodOrderCategoryBean> testOrderList = testMethodOrderCategoryService
                        .selectListByFilter(SearchFilter.instance().match("TESTMETHODID", testMethodIdList).filter(MatchPattern.OR));
                // 指标项目
                List<String> indexIdList = orderList.stream().map(e -> e.getIndexId()).distinct().collect(Collectors.toList());
                List<EmcIndexSetBean> indexSetList = indexSetService.selectListByFilter(SearchFilter.instance().match("INDEXID", indexIdList).filter(MatchPattern.OR));
                List<Long> indexItemIdList = indexSetList.stream().map(e -> e.getIndexItemId()).distinct().collect(Collectors.toList());
                List<EmcIndexTestItemBean> indexTestItemList = Collections.emptyList();
                if (!indexItemIdList.isEmpty()) {
                    indexTestItemList = indexTestItemService.selectListByFilter(SearchFilter.instance().match("INDEXITEMID", indexItemIdList).filter(MatchPattern.OR));
                }

                List<EmcFolderBean> insertFolderList = new ArrayList<>();// 点位
                List<EmcOrderBean> insertOrderList = new ArrayList<>();// 样品
                List<EmcOrderTaskBean> insertOrderTaskList = new ArrayList<>();// 样品测试
                List<EmcOrderTaskResultBean> insertOrderTaskResultList = new ArrayList<>();// 样品结果
                List<EmcOrderItemGroupBean> insertOrderItemGroupList = new ArrayList<>();// 样品分析项组
                int orderno = 1; //样品编号从1开始，依次递增
                for (EmcProjSchemeBean projScheme : schemeList) {
                    List<EmcProjOrderBean> projOrderList = orderList.stream().filter(order -> ObjectUtils.equals(order.getProjSchemeId(), projScheme.getId())).collect(Collectors.toList());
                    if(!projOrderList.isEmpty()){
                        this.generateBpSampleParamData(projScheme, projOrderList, itemList, pointList, testMethodList, testOrderList, indexSetList, indexTestItemList, insertFolderList, insertOrderList,
                                insertOrderTaskList, insertOrderTaskResultList, insertOrderItemGroupList, testCapacityList, orderno);
                        String maxOrderNo = insertOrderList.stream().mapToLong(r -> NumberUtils.parseLong(r.getSampleParamOrderNo())).max().orElse(0L) + "";
//                        String maxOrderNo = insertOrderList.stream().max(Comparator.comparing(EmcOrderBean::getSampleParamOrderNo)).get().getSampleParamOrderNo();
                        orderno = Integer.valueOf(maxOrderNo) +1;
                    }
                }

                folderService.getDao().insert(insertFolderList);
                orderService.getDao().insert(insertOrderList);
                orderTaskService.getDao().insert(insertOrderTaskList);
                orderTaskResultService.getDao().insert(insertOrderTaskResultList);
                orderItemGroupService.getDao().insert(insertOrderItemGroupList);
            }

        }

    }

    // ---------------------------------------------------------------------
    // 私有方法
    // ---------------------------------------------------------------------
    /**
     * 通过监测方案和监测方案样品生成主业务流程数据
     */
    private void generateBpSampleParamData(EmcProjSchemeBean projScheme,
                                           List<EmcProjOrderBean> projOrderList,
                                           List<EmcGroupItemBean> itemList,
                                           List<EmcMonitorPointBean> pointList,
                                           List<EmcTestMethodBean> testMethodList,
                                           List<EmcTestMethodOrderCategoryBean> testOrderList,
                                           List<EmcIndexSetBean> indexSetList,
                                           List<EmcIndexTestItemBean> indexTestItemList,
                                           List<EmcFolderBean> insertFolderList,
                                           List<EmcOrderBean> insertOrderList,
                                           List<EmcOrderTaskBean> insertOrderTaskList,
                                           List<EmcOrderTaskResultBean> insertOrderTaskResultList,
                                           List<EmcOrderItemGroupBean> insertOrderItemGroupList,
                                           List<EmcTestCapacityBean> testCapacityList, int orderno) {
        // 生成点位
        EmcFolderBean folder = this.generateSampleParamFolder(projScheme, pointList);
        insertFolderList.add(folder);
        // 生成样品
        List<EmcOrderBean> orderList = this.generateSampleParamOrderList(folder, projScheme, projOrderList,orderno);
        insertOrderList.addAll(orderList);
        // 生成样品测试和生成样品结果
        List<EmcOrderTaskResultBean> orderTaskResultList = new ArrayList<>();

        List<EmcOrderTaskBean> orderTaskList = this.createOrderTaskList(projScheme, orderList, projOrderList, testMethodList, itemList, testCapacityList, testOrderList, orderTaskResultList, indexSetList,
                indexTestItemList);

        /*List<EmcOrderTaskBean> orderTaskList = this.generateOrderTaskList(projScheme, orderList, projOrderList, itemList, testMethodList, testOrderList, orderTaskResultList, indexSetList,
                indexTestItemList);*/
        insertOrderTaskList.addAll(orderTaskList);
        insertOrderTaskResultList.addAll(orderTaskResultList);
        // 生成样品分析项组
        List<EmcOrderItemGroupBean> orderItemGroupList = this.generateOrderItemGroup(orderList, projOrderList, orderTaskList);

        insertOrderList.forEach(f ->{
            Map<String,List<EmcOrderTaskBean>> map = insertOrderTaskList.stream().filter(o ->!ObjectUtils.isEmpty(o.getContainerGroup()) && ObjectUtils.equals(f.getId().toString(), o.getOrderId().toString())).collect(Collectors.groupingBy(EmcOrderTaskBean::getContainerGroup));
            Object[] object = map.keySet().toArray();
            for(int i = 1 ; i < object.length + 1 ; i++) {
                List<EmcOrderTaskBean> filterOrderTaskList = map.get(object[i - 1]);
                String num = "";
                if(i < 10) {
                    num = "0" + i;
                }else {
                    num = "" + i;
                }
                for(int j = 0 ; j < filterOrderTaskList.size() ; j++) {
                    filterOrderTaskList.get(j).setExt$Item("groupno", num);
                }
            }
        });

        insertOrderItemGroupList.addAll(orderItemGroupList);
    }

    /**
     * 生成气采样数据点位
     * @param projScheme
     * @param pointList
     * @return
     */
    protected EmcFolderBean generateSampleParamFolder(EmcProjSchemeBean projScheme, List<EmcMonitorPointBean> pointList) {
        EmcMonitorPointBean point = pointList.stream().filter(f -> ObjectUtils.equals(f.getMonitorPointNo(), projScheme.getMonitorPointNo())).findFirst().orElse(null);
        EmcFolderBean folder = new EmcFolderBean();
        folder.setId(ApplicationContextHelper.getNextIdentity());
        folder.setFolderNo(projScheme.getFolderNo()+"param");//气参数点位号
        folder.setParamProjId(projScheme.getProjId());
        folder.setSampleOrgId(projScheme.getSampleOrgId());
        folder.setSampleOrgName(projScheme.getExt$Item("orgname"));
        folder.setOrderCategory(projScheme.getExt$Item("monitortmplname"));
        folder.setSource(EmcBizConstant.BP_SOURCE_MONITORSCHEMESAMPLEPARAM);
        if(ObjectUtils.isEmpty(projScheme.getMonitorPointId())) {
            folder.setMonitorPointCategoryId(projScheme.getExt$Item("POINTCATEGORY"));
        }else {
            folder.setMonitorPointCategoryId(projScheme.getMonitorPointCategoryId());
            folder.setMonitorPointCategory(projScheme.getMonitorPointCategoryName());
        }
        folder.setMonitorPointId(projScheme.getMonitorPointId());
        folder.setMonitorPointNo(projScheme.getMonitorPointNo());
        folder.setMonitorPointName(projScheme.getMonitorPointName());
        folder.setRemark(projScheme.getRemark());
        folder.setTermination(Constant.NO);
        folder.setMonitorTmplId(projScheme.getMonitorTmplId());
        if (point != null) {
            folder.setLongitude(point.getLongitude());
            folder.setLatitude(point.getLatitude());
            folder.setRiver(point.getRiver());
            folder.setTributary(point.getTributary());
        }
        folder.setLongitude(projScheme.getExt$Item("longitude"));
        folder.setLatitude(projScheme.getExt$Item("latitude"));


        return folder;
    }

    /**
     * 生成气采样参数样品数据
     */
    protected List<EmcOrderBean> generateSampleParamOrderList(EmcFolderBean folder, EmcProjSchemeBean projScheme, List<EmcProjOrderBean> projOrderList,int orderno) {
        List<EmcOrderBean> list = new ArrayList<>();
        Long cycle = projScheme.getCycle();// 周期
        Long frequency = projScheme.getFrequency();// 频次

        for (int i = 1; i <= cycle; i++) {
            for (int j = 1; j <= frequency; j++) {
                EmcOrderBean order = new EmcOrderBean();
                order.setId(ApplicationContextHelper.getNextIdentity());
                //order.setOrderNo(orderno+"");// 生成样品编码
                order.setSampleParamOrderNo(orderno+"");
                order.setLabelNo(order.getOrderNo());
                order.setProjId(null);
                order.setFolderId(folder.getId());
                order.setCycle(0L + i);
                order.setFrequency(0L + j);
                order.setMonitorTmplId(projScheme.getMonitorTmplId());
                order.setOrderCategory(projScheme.getExt$Item("monitortmplname"));
                order.setSampleNum(1L);
                order.setOrderNum(EmcConstant.ONE);
                order.setRemark(projScheme.getRemark());
                order.setTermination(Constant.NO);

                list.add(order);
                orderno = orderno+1;
            }
        }

        return list;
    }
    @Override
    @Transactional
    public void delProjOrder(RestJsonWrapperBean wrapper) {
        this.projOrderService.delete(wrapper);
    }

    @Override
    @Transactional
    public void sortFolderNo(RestJsonWrapperBean wrapper) {
        List<EmcProjSchemeBean> schemes = wrapper.parse(EmcProjSchemeBean.class);
        String monitorTmplId = schemes.get(0).getMonitorTmplId();
        String orderCategoryCode = ((EmcMonitorTmplBean) monitorTmplService.selectById(monitorTmplId)).getExt$Item("ordercategorycode");
        if (StringUtils.isEmpty(orderCategoryCode)) {
            orderCategoryCode = "";
        }
        Long projId = schemes.get(0).getProjId();
        String prefix = ((EmcProjectBean) projectService.selectById(projId)).getProjNo() + orderCategoryCode;
        List<EmcProjSchemeBean> updateList = new ArrayList<>();
        schemes.sort(Comparator.comparing(EmcProjSchemeBean::getSortNum, Comparator.nullsLast(Long::compareTo)));
        for (int i = 0; i <= schemes.size() - 1; ++i) {
            EmcProjSchemeBean scheme = schemes.get(i);
            scheme.setFolderNo(prefix + String.format("%03d", i + 1));
            updateList.add(scheme);
        }
        this.getDao().update(updateList, new String[]{"FOLDERNO"});
    }

    @Override
    @Transactional
    public void batchDelProjOrder(RestJsonWrapperBean wrapper) {
        List<EmcProjSchemeBean> schemes = wrapper.parse(EmcProjSchemeBean.class);
        List<EmcProjOrderBean> orders = wrapper.parse(EmcProjOrderBean.class);
        List<Long> schemeIds = schemes.stream().map(EmcProjSchemeBean::getId).distinct().collect(Collectors.toList());
        List<Long> itemGroupIds = orders.stream().map(EmcProjOrderBean::getItemGroupId).distinct().collect(Collectors.toList());
        List<EmcProjOrderBean> deleteList = this.projOrderService.selectListByFilter(
                SearchFilter.instance().match("PROJSCHEMEID", schemeIds).filter(MatchPattern.OR)
                        .match("ITEMGROUPID", itemGroupIds).filter(MatchPattern.OR), new Order[0]);
        this.projOrderService.getDao().deleteBy(deleteList, new String[0]);
    }


    public void syncEvalStd(RestJsonWrapperBean wrapper) {
        List<EmcProjSchemeBean> selectedSchemeList = wrapper.parse(EmcProjSchemeBean.class);
        List<Long> selectedSchemeIdList = selectedSchemeList.stream().map(EmcProjSchemeBean::getId).collect(Collectors.toList());
        List<EmcProjOrderBean> projOrderList = this.projOrderService.getDao().selectListByOneColumnValues(selectedSchemeIdList, "projSchemeId", new Order[0]);
        List<EmcProjOrderBean> targetProjOrderList = projOrderList.stream().filter(po -> !ObjectUtils.isEmpty((Object)po.getIndexId())).collect(Collectors.toList());
        if (targetProjOrderList.isEmpty()) {
            return;
        }
        List<Long> capacityItemGroupIdList = targetProjOrderList.stream().map(EmcProjOrderBean::getItemGroupId).distinct().collect(Collectors.toList());
        List<EmcCapacityItemGroupBean> capacityItemGroupList = this.capacityItemGroupService.getDao().selectListByOneColumnValues(capacityItemGroupIdList, "id", new Order[0]);
        List<Long> targetTestMethodIdList = capacityItemGroupList.stream().map(EmcCapacityItemGroupBean::getTestMethodId).distinct().collect(Collectors.toList());
        List<String> indexIdList = targetProjOrderList.stream().filter(order -> !ObjectUtils.isEmpty((Object)order.getIndexId())).map(EmcProjOrderBean::getIndexId).distinct().collect(Collectors.toList());
        List<EmcIndexSetBean> indexSetList = indexIdList.isEmpty() ? Collections.<EmcIndexSetBean>emptyList() : this.indexSetService.selectListByFilter(SearchFilter.instance().match("INDEXID", indexIdList).filter(MatchPattern.OR), new Order[0]);
        List<Long> indexItemIdList = indexSetList.stream().map(EmcIndexSetBean::getIndexItemId).distinct().collect(Collectors.toList());
        List<EmcIndexTestItemBean> indexTestItemList = indexItemIdList.isEmpty() ? Collections.<EmcIndexTestItemBean>emptyList() : this.indexTestItemService.selectListByFilter(SearchFilter.instance().match("INDEXITEMID", indexItemIdList).filter(MatchPattern.OR), new Order[0]);
        List<Long> schemeIdList = targetProjOrderList.stream().map(EmcProjOrderBean::getProjSchemeId).distinct().collect(Collectors.toList());
        List<EmcProjSchemeBean> targetSchemeList = selectedSchemeList.stream().filter(scheme -> schemeIdList.contains(scheme.getId())).collect(Collectors.toList());
        List<String> folderNoList = targetSchemeList.stream().map(EmcProjSchemeBean::getFolderNo).collect(Collectors.toList());
        List<EmcFolderBean> folderList = this.folderService.getDao().selectListByOneColumnValues(folderNoList, "folderNo", Arrays.asList("id", "folderNo"), new Order[0]);
        List<Long> folderIdList = folderList.stream().map(EmcFolderBean::getId).collect(Collectors.toList());
        List<EmcOrderTaskBean> orderTaskList = this.orderTaskService.selectListByFilter(SearchFilter.instance().match("folderId", folderIdList).filter(MatchPattern.OR).match("testMethodId", targetTestMethodIdList).filter(MatchPattern.OR), new Order[0]);
        List<EmcOrderTaskBean> targetTaskList = orderTaskList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<EmcOrderTaskBean>(Comparator.comparing(EmcOrderTaskBean::getId))), ArrayList::new));
        List<Long> targetTaskIdList = targetTaskList.stream().map(EmcOrderTaskBean::getId).distinct().collect(Collectors.toList());
        List<EmcOrderTaskResultBean> taskResultList = this.orderTaskResultService.getDao().selectListByOneColumnValues(targetTaskIdList, "orderTaskId", Arrays.asList("id", "folderId", "orderTaskId", "itemId"), new Order[0]);
        ArrayList<EmcOrderTaskBean> updateTaskList = new ArrayList();
        ArrayList<EmcOrderTaskResultBean> updateResultList = new ArrayList();
        targetSchemeList.forEach(scheme -> {
            EmcFolderBean curFolder = folderList.stream().filter(folder -> StringUtils.equals((String)folder.getFolderNo(), (String)scheme.getFolderNo())).findFirst().orElse(null);
            if (Objects.isNull((Object)curFolder)) {
                return;
            }
            List<EmcProjOrderBean> curProjOrderList = targetProjOrderList.stream().filter(po -> ObjectUtils.equals((Object)scheme.getId(), (Object)po.getProjSchemeId())).collect(Collectors.toList());
            curProjOrderList.forEach(projOrder -> {
                Long testMethodId = capacityItemGroupList.stream().filter(cig -> ObjectUtils.equals((Object)projOrder.getItemGroupId(), (Object)cig.getId())).map(EmcCapacityItemGroupBean::getTestMethodId).findFirst().orElse(null);
                List<EmcIndexSetBean> curIndexSetList = indexSetList.stream().filter(e -> ObjectUtils.equals((Object)e.getIndexId(), (Object)projOrder.getIndexId())).collect(Collectors.toList());
                List<Long> curIndexItemIdList = curIndexSetList.stream().map(EmcIndexSetBean::getIndexItemId).distinct().collect(Collectors.toList());
                List<EmcIndexTestItemBean> curIndexTestItemList = indexTestItemList.stream().filter(e -> curIndexItemIdList.contains(e.getIndexItemId())).collect(Collectors.toList());
                List<Long> curItemIdList = curIndexTestItemList.stream().map(EmcIndexTestItemBean::getItemId).distinct().collect(Collectors.toList());
                targetTaskList.stream().filter(task -> ObjectUtils.equals((Object)task.getFolderId(), (Object)curFolder.getId()) && ObjectUtils.equals((Object)testMethodId, (Object)task.getTestMethodId())).forEach(task -> {
                    task.setIndexCategoryId(projOrder.getIndexCategoryId());
                    task.setIndexId(projOrder.getIndexId());
                    updateTaskList.add(task);
                    List<EmcOrderTaskResultBean> curResultList = taskResultList.stream().filter(r -> ObjectUtils.equals((Object)r.getOrderTaskId(), (Object)task.getId()) && curItemIdList.contains(r.getItemId())).collect(Collectors.toList());
                    curResultList.forEach(r -> curIndexTestItemList.stream().filter(ti -> ObjectUtils.equals((Object)r.getItemId(), (Object)ti.getItemId())).findFirst().ifPresent(ti -> curIndexSetList.stream().filter(is -> ObjectUtils.equals((Object)is.getIndexItemId(), (Object)ti.getIndexItemId())).findFirst().ifPresent(is -> {
                        r.setIndexLowLimit(is.getIndexLowLimit());
                        r.setIndexHighLimit(is.getIndexHighLimit());
                        r.setIndexLimitUnit(is.getUnit());
                        updateResultList.add(r);
                    })));
                });
            });
        });
        this.orderTaskService.getDao().fastUpdate(updateTaskList, new String[]{"indexCategoryId", "indexId"});
        this.orderTaskResultService.getDao().fastUpdate(updateResultList, new String[]{"IndexLowLimit", "IndexHighLimit", "IndexLimitUnit"});
    }
}
