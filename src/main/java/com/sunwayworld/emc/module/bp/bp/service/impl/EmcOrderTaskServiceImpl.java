package com.sunwayworld.emc.module.bp.bp.service.impl;

import java.io.File;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.framework.constant.EmcBizConstant;
import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.emc.module.bp.bp.bean.EmcFolderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskLogBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskResultBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcResultLogBean;
import com.sunwayworld.emc.module.bp.bp.dao.EmcOrderTaskDao;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderService;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskLogService;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskResultService;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskService;
import com.sunwayworld.emc.module.bp.bp.service.EmcResultLogService;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcOrderItemBean;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcOrderItemGroupBean;
import com.sunwayworld.emc.module.bp.project.project.service.EmcOrderItemGroupService;
import com.sunwayworld.emc.module.bp.project.project.service.EmcOrderItemService;
import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportBean;
import com.sunwayworld.emc.module.bp.report.report.service.EmcReportService;
import com.sunwayworld.emc.module.mdm.basemdm.indexitem.bean.EmcIndexTestItemBean;
import com.sunwayworld.emc.module.mdm.basemdm.indexitem.service.EmcIndexTestItemService;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.bean.EmcPropertyDataBean;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.bean.EmcPropertyFieldBean;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.service.EmcPropertyDataService;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.service.EmcPropertyFieldService;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.service.EmcPropertyTmplService;
import com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.bean.EmcIndexCategoryBean;
import com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.bean.EmcIndexSetBean;
import com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.bean.EmcOrderIndexBean;
import com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.service.EmcIndexCategoryService;
import com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.service.EmcOrderIndexService;
import com.sunwayworld.emc.module.mdm.bizmdm.method.bean.EmcMethodBean;
import com.sunwayworld.emc.module.mdm.bizmdm.method.service.EmcMethodService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcGroupItemBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcItemBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcTestMethodBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcTestMethodOrderCategoryBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcGroupItemService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcItemService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcTestMethodOrderCategoryService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.io.file.FileRepresentation;
import com.sunwayworld.framework.io.file.FileScope;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.mybatis.page.MybatisPageHelper;
import com.sunwayworld.framework.mybatis.page.PageRowBounds;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.ArithUtils;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.cloud.CoreFileCloudService;
import com.sunwayworld.module.item.file.service.CoreFileService;
import com.sunwayworld.module.sys.constant.bean.CoreConstantBean;
import com.sunwayworld.module.sys.constant.service.CoreConstantService;

/**
 * @author zhangsy@sunwayworld.com
 * @date 2019-10-12
 */

@Repository
@GikamBean
public class EmcOrderTaskServiceImpl implements EmcOrderTaskService {
    @Autowired
    private EmcOrderTaskDao orderTaskDao;

    @Autowired
    private EmcOrderItemGroupService orderItemGroupService;

    @Autowired
    private EmcOrderItemService orderItemService;

    @Autowired
    private EmcItemService itemService;

    @Autowired
    private EmcOrderTaskResultService orderTaskResultService;

    @Autowired
    private EmcReportService reportService;

    @Autowired
    private EmcMethodService methodService;

    @Autowired
    private EmcResultLogService resultLogService;

    @Autowired
    private EmcOrderTaskLogService orderTaskLogService;

    @Autowired
    @Lazy
    private EmcOrderService orderService;

    @Autowired
    private CoreFileService coreFileService;

    @Autowired
    private EmcTestMethodOrderCategoryService testMethodOrderCategoryService;

    @Autowired
    private EmcGroupItemService groupItemService;

    @Autowired
    private EmcOrderIndexService orderIndexService;

    @Autowired
    private EmcIndexTestItemService indexTestItemService;

    @Autowired
    private EmcIndexCategoryService indexCategoryService;

    @Autowired
    private EmcPropertyDataService propertyDataService;

    @Autowired
    private CoreFileCloudService fileCloudService;

    @Autowired
    private CoreConstantService coreConstantService;

    @Autowired
    private EmcPropertyFieldService propertyFieldService;

    @Autowired
    private EmcPropertyTmplService propertyTmplService;

    @Override
    @SuppressWarnings("unchecked")
    public EmcOrderTaskDao getDao() {
        return orderTaskDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        EmcOrderTaskBean emcOrderTask = jsonWrapper.parseUnique(EmcOrderTaskBean.class);
        emcOrderTask.setId(ApplicationContextHelper.getNextIdentity());
        Long orderId = emcOrderTask.getOrderId();
        if (!ObjectUtils.isEmpty(orderId)) {
            EmcOrderBean order = orderService.getDao().selectById(orderId);
            emcOrderTask.setEquiptAcquNo(order.getLabelNo());
        }
        getDao().insert(emcOrderTask);
        return emcOrderTask.getId();
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.DELETE)
    public void delete(RestJsonWrapperBean wrapper) {
        String source = wrapper.getParamValue("source");
        if (source != null) {
            // 插入样品测试记录表
            orderTaskLogService.insert(wrapper.parse(EmcOrderTaskBean.class), EmcBizConstant.ORDER_TASK_LOG_DELETE_TEST, EmcBizConstant.ORDER_TASK_LOG_CHECK_PROJECT_EDIT);
        }
        EmcOrderTaskService.super.delete(wrapper);
    }

    @Override
    public Page<EmcOrderTaskBean> selectContainerInfoByOrderId(Long orderId, RestJsonWrapperBean wrapper) {
        MapperParameter parameter = wrapper.extractMapFilter();
        PageRowBounds rowBounds = wrapper.extractPageRowBounds();
        parameter.put("orderId", orderId);

        Page<EmcOrderTaskBean> page = MybatisPageHelper.get(rowBounds, () -> getDao().selectContainerInfo(parameter));
        List<EmcOrderTaskBean> orderTaskList = page.getRows();

        /*
         * List<EmcOrderTaskBean> emptyContainerTaskList =
         * orderTaskList.stream().filter(task ->
         * StringUtils.isEmpty(task.getContainerCategoryName())).collect(
         * Collectors.toList()); if (emptyContainerTaskList.size() > 1) {
         * EmcOrderTaskBean samplingOrderTask = emptyContainerTaskList.get(0);
         * EmcOrderTaskBean analysisOrderTask = emptyContainerTaskList.get(1);
         * String analysisTestName = analysisOrderTask.getTestName(); String
         * samplingTestName = samplingOrderTask.getTestName(); String[]
         * analysisTestNames =
         * StringUtils.split(analysisOrderTask.getTestName(), ","); String[]
         * sampleTestNames = StringUtils.split(samplingOrderTask.getTestName(),
         * ","); String[] samplingOrderTaskIds =
         * StringUtils.split(samplingOrderTask.getExt$Item("multitestid"), ",");
         * if (sampleTestNames.length > analysisTestNames.length) { String[]
         * strings1 = Arrays.copyOfRange(samplingOrderTaskIds,
         * analysisTestNames.length, samplingOrderTaskIds.length); String
         * replacedSamplingTestName = StringUtils.replace(samplingTestName,
         * analysisTestName, ""); if (replacedSamplingTestName.startsWith(","))
         * { replacedSamplingTestName = replacedSamplingTestName.substring(1); }
         * if (replacedSamplingTestName.endsWith(",")) {
         * replacedSamplingTestName = replacedSamplingTestName.substring(0,
         * replacedSamplingTestName.length() - 1); }
         * samplingOrderTask.setTestName(replacedSamplingTestName);
         * samplingOrderTask.setExt$Item("multitestid",
         * StringUtils.join(strings1, ",")); } }
         */

        return new Pagination<>(page, orderTaskList);
    }

    @Override
    public Page<EmcOrderTaskResultBean> selectOrderTaskResultByOrderTaskId(Long orderTaskId, RestJsonWrapperBean wrapper) {
        return orderTaskResultService.selectPaginationByFilter(SearchFilter.instance().match("ORDERTASKID", orderTaskId).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    public Page<EmcOrderIndexBean> selectOrderIndexByIndexCategoryId(Long indexCategoryId, RestJsonWrapperBean wrapper) {
        return orderIndexService.selectPaginationByFilter(SearchFilter.instance().match("INDEXCATEGORYID", indexCategoryId).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void insertByOrder(List<EmcOrderBean> orderList) {

        List<EmcOrderTaskBean> insertOrderTaskList = new ArrayList<>();
        List<EmcOrderTaskResultBean> insertOrderTaskResultList = new ArrayList<>();

        List<Long> orderIdList = orderList.stream().map(e -> e.getId()).collect(Collectors.toList());

        List<EmcOrderItemGroupBean> orderItemGroupList = orderItemGroupService.getDao().selectListByOneColumnValues(orderIdList, "ORDERID");

        List<EmcOrderItemBean> orderItemList = orderItemService.getDao().selectListByOneColumnValues(orderIdList, "ORDERID");

        List<EmcItemBean> itemList = itemService.getDao().selectListByOneColumnValues(orderItemList.stream().map(e -> e.getItemId()).collect(Collectors.toList()), "ID");

        for (EmcOrderBean order : orderList) {

            // 当前样品的样品分析项组
            List<EmcOrderItemGroupBean> filterOrderItemGroupList = orderItemGroupList.stream().filter(e -> ObjectUtils.equals(e.getOrderId(), order.getId())).collect(Collectors.toList());

            // 样品分析项组按照测试、方法分组决定生成几条样品测试
            List<EmcOrderItemGroupBean> distinctTestMethodOrderItemGroup = filterOrderItemGroupList.stream().collect(Collectors
                    .collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(o -> o.getTestId() + "#" + o.getMethodId()))), ArrayList<EmcOrderItemGroupBean>::new));

            for (EmcOrderItemGroupBean orderItemGroup : distinctTestMethodOrderItemGroup) {

                List<EmcOrderItemGroupBean> testMethodOrderItemGroupList = filterOrderItemGroupList.stream()
                        .filter(e -> ObjectUtils.equals(orderItemGroup.getTestId(), e.getTestId()) && ObjectUtils.equals(orderItemGroup.getMethodId(), e.getMethodId())).collect(Collectors.toList());

                List<Long> testMethodOrderItemGroupIdList = testMethodOrderItemGroupList.stream().map(e -> e.getId()).collect(Collectors.toList());

                List<EmcOrderItemBean> filterOrderItemList = orderItemList.stream().filter(e -> testMethodOrderItemGroupIdList.contains(e.getOrderItemGroupId())).collect(Collectors.toList());

                List<Long> itemIdList = filterOrderItemList.stream().map(e -> e.getItemId()).distinct().collect(Collectors.toList());

                List<EmcItemBean> filterItemList = itemList.stream().filter(e -> itemIdList.contains(e.getId()) && "1".equals(e.getActivatedFlag())).collect(Collectors.toList());

                // 判断当前样品测试分析项组下启用的分析项有几种分析类型,决定插入几条样品测试数据
                List<String> analysisCategoryList = filterItemList.stream().map(e -> e.getAnalysisCategory()).distinct().collect(Collectors.toList());

                for (String analysisCategory : analysisCategoryList) {

                    // 样品测试
                    EmcOrderTaskBean orderTask = new EmcOrderTaskBean();

                    BeanUtils.copyProperties(orderItemGroup, orderTask);

                    orderTask.setId(ApplicationContextHelper.getNextIdentity());
                    orderTask.setAnalysisType(analysisCategory);
                    orderTask.setFolderId(order.getFolderId());
                    orderTask.setProjId(order.getProjId());
                    orderTask.setMonitorTmplId(order.getMonitorTmplId());
                    orderTask.setOrderCategory(order.getOrderCategory());
                    orderTask.setEquiptAcquNo(order.getLabelNo());

                    insertOrderTaskList.add(orderTask);

                    // 样品测试结果
                    List<EmcItemBean> analysisCategoryItemList = filterItemList.stream().filter(e -> ObjectUtils.equals(e.getAnalysisCategory(), analysisCategory)).collect(Collectors.toList());

                    insertOrderTaskResultList.addAll(getInsertOrderTaskResultList(orderTask, analysisCategoryItemList, order));
                }

            }

        }

        getDao().insert(insertOrderTaskList);
        orderTaskResultService.getDao().insert(insertOrderTaskResultList);

    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void updatePartake(RestJsonWrapperBean jsonWrapper) {
        List<EmcOrderTaskBean> orderTaskList = jsonWrapper.parse(EmcOrderTaskBean.class);

        if (orderTaskList.isEmpty()) {
            return;
        }

        String partakeUserNames = orderTaskList.stream().map(i -> i.getPartakeUserName()).filter(i -> !StringUtils.isEmpty(i)).collect(Collectors.joining(","));

        String partakeUserIds = orderTaskList.stream().map(i -> i.getPartakeUserId()).filter(i -> !StringUtils.isEmpty(i)).collect(Collectors.joining(","));

        orderTaskList.stream().forEach(e -> {
            e.setPartakeUserId(partakeUserIds);
            e.setPartakeUserName(partakeUserNames);
        });

        getDao().update(orderTaskList, "PARTAKEUSERID", "PARTAKEUSERNAME");
    }

    @Override
    public Page<EmcOrderTaskBean> selectRunnableList(RestJsonWrapperBean wrapper) {
        MapperParameter parameter = wrapper.extractMapFilter();
        PageRowBounds rowBounds = wrapper.extractPageRowBounds();

        return MybatisPageHelper.get(rowBounds, () -> getDao().selectRunnableList(parameter));
    }

    @Override
    public Page<EmcOrderTaskBean> selectRunAnalysisOrderTaskList(RestJsonWrapperBean wrapper) {
        MapperParameter parameter = wrapper.extractMapFilter();
        PageRowBounds rowBounds = wrapper.extractPageRowBounds();

        return MybatisPageHelper.get(rowBounds, () -> getDao().selectRunAnalysisOrderTaskList(parameter));
    }

    @Override
    public Page<EmcOrderTaskBean> selectProjOrderCategoryPagination(RestJsonWrapperBean wrapper) {
        MapperParameter parameter = wrapper.extractMapFilter();
        PageRowBounds rowBounds = wrapper.extractPageRowBounds();

        return selectPagination(() -> getDao().selectProjOrderCategoryByCondition(parameter), rowBounds);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void submitProjOrderCategory(RestJsonWrapperBean wrapper) {

        // 查询当前用户所属科室完成分析的全部样品测试
        MapperParameter parameter = wrapper.extractMapFilter();
        List<EmcOrderTaskBean> orderTaskList = getDao().selectOrgProjOrderCategoryByCondition(parameter);

        List<EmcOrderTaskBean> filterOrderTask = wrapper.parse(EmcOrderTaskBean.class);
        List<EmcOrderTaskBean> updatedOrderTaskList = new ArrayList<>();
        // 过滤出提交的样品测试
        filterOrderTask.forEach(f -> {

            List<EmcOrderTaskBean> projTmplOrderTaskList = orderTaskList.stream()
                    .filter(e -> ObjectUtils.equals(e.getProjId(), f.getProjId()) && ObjectUtils.equals(e.getMonitorTmplId(), f.getMonitorTmplId())).collect(Collectors.toList());

            updatedOrderTaskList.addAll(projTmplOrderTaskList);
        });

        // 将对应的样品测试的状态置为done
        EmcOrderTaskBean updateOrderTask = new EmcOrderTaskBean();

        updateOrderTask.setStatus(EmcBizConstant.ORDERTASK_STATUS_DONE);

        getDao().updateByIds(updateOrderTask, updatedOrderTaskList.stream().map(e -> e.getId()).collect(Collectors.toList()));

        // 插入样品测试操作记录表
        orderTaskLogService.insert(updatedOrderTaskList, EmcBizConstant.ORDER_TASK_LOG_LEADER_AUDIT, EmcBizConstant.ORDER_TASK_LOG_ANALYSE_AUDIT);

        // 判断更新的样品测试对应的项目样品类型下的样品测试是不是都已经完成，完成的话将报告状态置为待录入
        List<EmcReportBean> updateReportList = new ArrayList<>();

        List<EmcOrderTaskBean> reportOrderTaskList = orderTaskList.stream().collect(
                Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(o -> o.getProjId() + "#" + o.getMonitorTmplId()))), ArrayList<EmcOrderTaskBean>::new));

        List<EmcOrderTaskBean> allReportOrderTaskList = getDao().selectList(reportOrderTaskList, Arrays.asList("PROJID", "MONITORTMPLID"), Collections.emptyList());

        for (EmcOrderTaskBean reportOrderTask : reportOrderTaskList) {

            if (allReportOrderTaskList.stream()
                    .filter(e -> ObjectUtils.equals(e.getProjId(), reportOrderTask.getProjId()) && ObjectUtils.equals(e.getMonitorTmplId(), reportOrderTask.getMonitorTmplId()))
                    .allMatch(e -> EmcBizConstant.ORDERTASK_STATUS_DONE.equals(e.getStatus()))) {

                EmcReportBean report = new EmcReportBean();

                report.setProjId(reportOrderTask.getProjId());
                report.setMonitorTmplId(reportOrderTask.getMonitorTmplId());

                updateReportList.add(report);

            }

        }

        if (!updateReportList.isEmpty()) {

            List<Long> updateReportIdList = reportService.getDao().selectList(updateReportList, Arrays.asList("PROJID", "MONITORTMPLID"), Arrays.asList("ID")).stream().map(e -> e.getId())
                    .collect(Collectors.toList());

            EmcReportBean report = new EmcReportBean();
            report.setStatus(EmcBizConstant.REPORT_STATUS_REPORTEDIT);

            reportService.getDao().updateByIds(report, updateReportIdList, "STATUS");

        }

        // 合成项目pdf报表
        composeProjectPdfReport(filterOrderTask);

    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void updateContainerInfo(RestJsonWrapperBean wrapper) {
        // 原容器信息 按测试名称来修改
        List<EmcOrderTaskBean> orderOriginTaskList = wrapper.parse(EmcOrderTaskBean.class, 0);
        // 获取所有的 orderTask 的 id
        String orderTaskIds = orderOriginTaskList.get(0).getExt$Item("orderTaskId");

        List<EmcOrderTaskBean> combineOrderTaskList = new ArrayList<>();

        // 按 orderTaskId 将全部样品测试数据重新设置 id
        List<String> orderTaskIdArray = new ArrayList<>(Arrays.asList(orderTaskIds.split(",")));
        for (String orderTaskId : orderTaskIdArray) {
            for (EmcOrderTaskBean orderTask : orderOriginTaskList) {
                if (Long.parseLong(orderTaskId) != orderTask.getId()) {
                    EmcOrderTaskBean newOrderTask = new EmcOrderTaskBean();
                    BeanUtils.copyProperties(orderTask, newOrderTask);
                    combineOrderTaskList.add(newOrderTask);
                    int location = combineOrderTaskList.size() - 1;
                    combineOrderTaskList.get(location).setId(Long.parseLong(orderTaskId));
                }
            }
        }
        orderOriginTaskList.addAll(combineOrderTaskList);

        // 修改后容器信息 录入信息 实际只有一条数据
        EmcOrderTaskBean newOrderTask = wrapper.parse(EmcOrderTaskBean.class, 1).get(0);

        List<EmcOrderTaskBean> orderTaskList = getDao().selectList(orderOriginTaskList, Arrays.asList("TESTNAME", "CONTAINERCATEGORYNAME", "CONTAINERGROUP", "FIXATIVE", "ID"),
                Collections.emptyList());
        orderTaskList.stream().forEach(e -> {
            if (newOrderTask.getContainerCategoryId() != null) {
                e.setContainerCategoryId(newOrderTask.getContainerCategoryId());
                e.setContainerCategoryCode(newOrderTask.getContainerCategoryCode());
                e.setContainerCategoryName(newOrderTask.getContainerCategoryName());
            }

            if (!StringUtils.isEmpty(newOrderTask.getContainerGroup())) {
                e.setContainerGroup(newOrderTask.getContainerGroup());
            }

            if (!StringUtils.isEmpty(newOrderTask.getFixative())) {
                e.setFixative(newOrderTask.getFixative());
            }

            if (!StringUtils.isEmpty(newOrderTask.getStoreCondition())) {
                e.setStoreCondition(newOrderTask.getStoreCondition());
            }
        });

        if (!orderTaskList.isEmpty()) {
            getDao().updateIfChanged(orderTaskList);
        }
    }

    @Override
    public Page<EmcOrderTaskBean> selectOrderTaskPagationByOrder(RestJsonWrapperBean wrapper) {
        // 获取 样品信息
        List<EmcOrderBean> orderList = wrapper.parse(EmcOrderBean.class, 0);
        // 获取样品对应的容器信息，实际只有一条数据(只能选择一条) 测试名称为拼接
        EmcOrderTaskBean containerOrderTask = wrapper.parse(EmcOrderTaskBean.class, 1).get(0);

        List<EmcOrderTaskBean> orderTasklList = new ArrayList<>();
        orderList.stream().forEach(e -> {
            EmcOrderTaskBean orderTask = new EmcOrderTaskBean();

            orderTask.setOrderId(e.getId());
            if (!ObjectUtils.isEmpty(containerOrderTask.getContainerCategoryId())) {
                orderTask.setContainerCategoryId(containerOrderTask.getContainerCategoryId());
            }
            if (!StringUtils.isEmpty(containerOrderTask.getContainerGroup())) {
                orderTask.setContainerGroup(containerOrderTask.getContainerGroup());
            }
            if (!StringUtils.isEmpty(containerOrderTask.getFixative())) {
                orderTask.setFixative(containerOrderTask.getFixative());
            }
            if (!StringUtils.isEmpty(containerOrderTask.getStoreCondition())) {
                orderTask.setStoreCondition(containerOrderTask.getStoreCondition());
            }

            orderTasklList.add(orderTask);

        });
        List<EmcOrderTaskBean> orderTaskList = getDao().selectList(orderTasklList, Arrays.asList("CONTAINERCATEGORYID", "CONTAINERGROUP", "FIXATIVE", "ORDERID"), Collections.emptyList());

        final String orderTaskIdString = orderTaskList.stream().map(i -> i.getId().toString()).filter(i -> !StringUtils.isEmpty(i)).collect(Collectors.joining(","));

        orderTaskList.stream().forEach(e -> {
            e.setExt$Item("orderTaskId", orderTaskIdString);
        });

        // 根据样品编号，容器名称，容器组，固定剂 查询全部信息, 并过滤出和选择的右侧样品测试相同的
        List<EmcOrderTaskBean> orderTaskContainerList = orderTaskList.stream()
                .collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(o -> o.getTestName()))), ArrayList<EmcOrderTaskBean>::new));

        return new Pagination<>(orderTaskContainerList);
    }

    @Override
    public void editResult(Long orderTaskId, RestJsonWrapperBean wrapper) {

        EmcOrderTaskBean orderTask = getDao().selectById(orderTaskId);

        EmcOrderBean order = orderService.getDao().selectById(orderTask.getOrderId());

        List<EmcItemBean> newItemList = wrapper.parse(EmcItemBean.class);

        List<Long> newItemIdList = newItemList.stream().map(e -> e.getId()).collect(Collectors.toList());

        List<EmcOrderTaskResultBean> oldOrderTaskResultList = orderTaskResultService.getDao().selectListByOneColumnValue(orderTaskId, "orderTaskId");

        List<Long> oldOrderTaskResultIdList = oldOrderTaskResultList.stream().map(e -> e.getItemId()).collect(Collectors.toList());

        // 需要新生成的数据
        List<EmcItemBean> insertItemList = newItemList.stream().filter(e -> !oldOrderTaskResultIdList.contains(e.getId())).collect(Collectors.toList());
        orderTaskResultService.getDao().insert(getInsertOrderTaskResultList(orderTask, insertItemList, order));

        // 需要删除的数据
        List<Long> deleteOrderTaskResultIdList = oldOrderTaskResultList.stream().filter(e -> !newItemIdList.contains(e.getItemId())).map(e -> e.getId()).collect(Collectors.toList());
        orderTaskResultService.getDao().deleteByIdList(deleteOrderTaskResultIdList);

    }

    @Override
    public Page<EmcOrderTaskBean> selectOrderTaskList(RestJsonWrapperBean wrapper) {
        MapperParameter parameter = wrapper.extractMapFilter();
        PageRowBounds rowBounds = wrapper.extractPageRowBounds();
        List<EmcOrderTaskBean> orderTaskList = getDao().selectOrderTaskList(parameter);
        // 根据样品编码分组
        Map<Long, List<EmcOrderTaskBean>> map = orderTaskList.stream().collect(Collectors.groupingBy(o -> o.getOrderId()));
        List<EmcOrderTaskBean> orderTaskGroupByList = new ArrayList<>();
        // 分组后每组取第一条
        map.forEach((key, value) -> orderTaskGroupByList.add(value.get(0)));
        List<EmcOrderTaskBean> orderTaskSortList = orderTaskGroupByList.stream().sorted((o1, o2) -> o1.getCupNo().compareTo(o2.getCupNo())).collect(Collectors.toList());
        return MybatisPageHelper.get(rowBounds, () -> orderTaskSortList);
    }

    @Override
    public Page<EmcOrderTaskBean> selectOrderTaskByRunsList(RestJsonWrapperBean wrapper) {
        MapperParameter parameter = wrapper.extractMapFilter();
        PageRowBounds rowBounds = wrapper.extractPageRowBounds();
        List<EmcOrderTaskBean> orderTaskList = getDao().selectOrderTaskByRunsList(parameter);
        // 根据样品编码分组
        Map<Long, List<EmcOrderTaskBean>> map = orderTaskList.stream().collect(Collectors.groupingBy(o -> o.getOrderId()));
        List<EmcOrderTaskBean> orderTaskGroupByList = new ArrayList<>();
        // 分组后每组取第一条
        map.forEach((key, value) -> orderTaskGroupByList.add(value.get(0)));
        List<EmcOrderTaskBean> orderTaskSortList = orderTaskGroupByList.stream().sorted((o1, o2) -> o1.getCupNo().compareTo(o2.getCupNo())).collect(Collectors.toList());
        return MybatisPageHelper.get(rowBounds, () -> orderTaskSortList);
    }

    @Override
    public Page<EmcOrderTaskBean> selectUniqueOrderTask(RestJsonWrapperBean wrapper) {
        MapperParameter parameter = wrapper.extractMapFilter();
        PageRowBounds rowBounds = wrapper.extractPageRowBounds();
        List<EmcOrderTaskBean> orderTaskList = getDao().selectUniqueOrderTask(parameter);

        return MybatisPageHelper.get(rowBounds, () -> orderTaskList);
    }

    @Override
    public void editSubcontractor(RestJsonWrapperBean wrapper) {
        if (wrapper.getBodyList().size() < 2) {
            List<EmcOrderTaskBean> selectedOrderTaskList = wrapper.parse(EmcOrderTaskBean.class, 0);
            // 同一个任务，共用一个分包流水单号
            Long projectId = selectedOrderTaskList.get(0).getProjId();
            List<EmcOrderTaskBean> subOrderTaskList = getDao().selectListByOneColumnValue(projectId, "PROJID", Arrays.asList("ID", "SUBCONTRACTORID", "SUBPACKAGENO"));
            Optional<EmcOrderTaskBean> subOrderTask = subOrderTaskList.stream().filter(task -> !ObjectUtils.isEmpty(task.getSubcontractorId()) && !ObjectUtils.isEmpty(task.getSubPackageNo())).findFirst();
            String subPackageNo = subOrderTask.isPresent() ? subOrderTask.get().getSubPackageNo() : ApplicationContextHelper.getNextSequence(orderItemGroupService.getDao().getTable());

            selectedOrderTaskList.forEach(task -> {
                task.setSubPackageNo(subPackageNo);
                task.setSamplingSubpackage(Constant.YES);
            });
            getDao().update(selectedOrderTaskList, Arrays.asList("SUBCONTRACTORID", "SUBPACKAGENO", "SAMPLINGSUBPACKAGE"));

            return;
        }

        List<EmcOrderBean> selectedOrderList = wrapper.parse(EmcOrderBean.class, 0);
        List<EmcOrderTaskBean> selectedOrderTaskList = wrapper.parse(EmcOrderTaskBean.class, 1);
        List<Long> selectedTestIdList = selectedOrderTaskList.stream().map(EmcOrderTaskBean::getTestId).collect(Collectors.toList());

        List<EmcOrderTaskBean> filterOrderTaskList = new ArrayList<>();
        selectedOrderList.forEach(order -> {
            EmcOrderTaskBean filterTask = new EmcOrderTaskBean();
            filterTask.setOrderId(order.getId());
            filterTask.setAnalysisType(EmcBizConstant.ANALYSISCATEGORY_ANALYSIS);
            filterOrderTaskList.add(filterTask);
        });

        List<EmcOrderTaskBean> orderTaskList = getDao().selectList(filterOrderTaskList, Arrays.asList("ORDERID", "ANALYSISTYPE"), Arrays.asList("ID", "ORDERID", "TESTID"));
        List<EmcOrderTaskBean> updateOrderTaskList = orderTaskList.stream().filter(task -> selectedTestIdList.contains(task.getTestId())).collect(Collectors.toList());

        // 同一个任务，共用一个分包流水单号
        Long projectId = selectedOrderList.get(0).getProjId();
        List<EmcOrderTaskBean> subOrderTaskList = getDao().selectListByOneColumnValue(projectId, "PROJID", Arrays.asList("ID", "SUBCONTRACTORID", "SUBPACKAGENO"));
        Optional<EmcOrderTaskBean> subOrderTask = subOrderTaskList.stream().filter(task -> !ObjectUtils.isEmpty(task.getSubcontractorId()) && !ObjectUtils.isEmpty(task.getSubPackageNo())).findFirst();
        String subPackageNo = subOrderTask.isPresent() ? subOrderTask.get().getSubPackageNo() : ApplicationContextHelper.getNextSequence(getDao().getTable());

        updateOrderTaskList.forEach(task -> {
            task.setSubcontractorId(selectedOrderTaskList.get(0).getSubcontractorId());
            task.setSubPackageNo(subPackageNo);
            task.setSamplingSubpackage(Constant.YES);
        });

        getDao().update(updateOrderTaskList, Arrays.asList("SUBCONTRACTORID", "SUBPACKAGENO", "SAMPLINGSUBPACKAGE"));
    }

    @Override
    public void deleteSubcontractor(RestJsonWrapperBean wrapper) {
        if (wrapper.getBodyList().size() < 2) {
            List<EmcOrderTaskBean> selectedOrderTaskList = wrapper.parse(EmcOrderTaskBean.class, 0);
            selectedOrderTaskList.forEach(task -> {
                task.setSubPackageNo(null);
                task.setSubcontractorId(null);
                task.setSamplingSubpackage(null);
            });
            getDao().update(selectedOrderTaskList, Arrays.asList("SUBCONTRACTORID", "SUBPACKAGENO", "SAMPLINGSUBPACKAGE"));

            return;
        }

        List<EmcOrderBean> selectedOrderList = wrapper.parse(EmcOrderBean.class, 0);
        List<EmcOrderTaskBean> selectedOrderTaskList = wrapper.parse(EmcOrderTaskBean.class, 1);
        List<Long> selectedTestIdList = selectedOrderTaskList.stream().map(EmcOrderTaskBean::getTestId).collect(Collectors.toList());

        List<EmcOrderTaskBean> filterOrderTaskList = new ArrayList<>();
        selectedOrderList.forEach(order -> {
            EmcOrderTaskBean filterTask = new EmcOrderTaskBean();
            filterTask.setOrderId(order.getId());
            filterTask.setAnalysisType(EmcBizConstant.ANALYSISCATEGORY_ANALYSIS);
            filterOrderTaskList.add(filterTask);
        });

        List<EmcOrderTaskBean> orderTaskList = getDao().selectList(filterOrderTaskList, Arrays.asList("ORDERID", "ANALYSISTYPE"), Arrays.asList("ID", "ORDERID", "TESTID"));
        List<EmcOrderTaskBean> updateOrderTaskList = orderTaskList.stream().filter(task -> selectedTestIdList.contains(task.getTestId())).collect(Collectors.toList());

        updateOrderTaskList.forEach(task -> {
            task.setSubPackageNo(null);
            task.setSubcontractorId(null);
        });
        getDao().update(updateOrderTaskList, Arrays.asList("SUBCONTRACTORID", "SUBPACKAGENO"));
    }

    @Override
    public Page<EmcOrderTaskBean> selectSubpackageOrderTask(RestJsonWrapperBean wrapper) {
        MapperParameter parameter = wrapper.extractMapFilter();
        PageRowBounds rowBounds = wrapper.extractPageRowBounds();
        List<EmcOrderTaskBean> orderTaskList = getDao().selectSubpackageOrderTask(parameter);

        return MybatisPageHelper.get(rowBounds, () -> orderTaskList);
    }

    @Override
    public void syncOrderContainerInfo(RestJsonWrapperBean wrapper) {
        List<EmcOrderBean> orderList = wrapper.parse(EmcOrderBean.class);
        if (orderList.isEmpty()) {
            return;
        }

        List<Long> orderIdList = orderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList());
        List<EmcOrderTaskBean> relatedOrderTaskList = getDao().selectListByOneColumnValues(orderIdList, "ORDERID", Arrays.asList("ID", "TESTMETHODID", "MONITORTMPLID"));
        syncContainerInfo(relatedOrderTaskList);
    }

    @Override
    public void syncContainerInfo(RestJsonWrapperBean wrapper) {
        List<EmcOrderTaskBean> orderTaskList = wrapper.parse(EmcOrderTaskBean.class);
        if (orderTaskList.isEmpty()) {
            return;
        }

        List<String> multiTestIdList = orderTaskList.stream().map(task -> task.getExt$Item("multitestid")).collect(Collectors.toList());
        List<Long> orderTaskIdList = new ArrayList<>();
        multiTestIdList.forEach(ids -> orderTaskIdList.addAll(Arrays.stream(StringUtils.split(ids, ",")).mapToLong(NumberUtils::parseLong).boxed().collect(Collectors.toList())));

        List<EmcOrderTaskBean> relatedOrderTaskList = getDao().selectListByIds(orderTaskIdList, Arrays.asList("ID", "TESTMETHODID", "MONITORTMPLID"));
        syncContainerInfo(relatedOrderTaskList);
    }

    @Override
    public void syncContainerInfo(List<EmcOrderTaskBean> orderTaskList) {
        List<EmcOrderTaskBean> notEmptyOrderTaskList = orderTaskList.stream().filter(task -> !ObjectUtils.isEmpty(task.getTestMethodId())).collect(Collectors.toList());
        List<Long> testMethodIdList = notEmptyOrderTaskList.stream().map(EmcOrderTaskBean::getTestMethodId).distinct().collect(Collectors.toList());

        if (testMethodIdList.isEmpty()) {
            return;
        }

        List<EmcTestMethodOrderCategoryBean> orderCategoryList = testMethodOrderCategoryService.selectListByFilter(SearchFilter.instance().match("TESTMETHODID", testMethodIdList).filter(MatchPattern.OR));
        List<EmcOrderTaskBean> updateOrderTaskList = new ArrayList<>();
        notEmptyOrderTaskList.forEach(orderTask -> orderCategoryList.stream().filter(orderCategory -> ObjectUtils.equals(orderTask.getTestMethodId(), orderCategory.getTestMethodId())
                && StringUtils.equals(orderTask.getMonitorTmplId(), orderCategory.getMonitorTmplId())).findFirst().ifPresent(orderCategory -> {

            EmcOrderTaskBean updateOrderTask = new EmcOrderTaskBean();
            updateOrderTask.setId(orderTask.getId());
            updateOrderTask.setContainerCategoryCode(orderCategory.getExt$Item("containerCategoryCode"));
            updateOrderTask.setContainerCategoryId(orderCategory.getContainerCategoryId());
            updateOrderTask.setContainerCategoryName(orderCategory.getExt$Item("containerCategoryName"));
            updateOrderTask.setContainerGroup(orderCategory.getContainerGroup());
            updateOrderTask.setFixative(orderCategory.getFixative());
            updateOrderTask.setStoreCondition(orderCategory.getStoreCondition());
            updateOrderTaskList.add(updateOrderTask);
        }));

        if (!updateOrderTaskList.isEmpty()) {
            getDao().update(updateOrderTaskList);
        }
    }

    @Override
    public void editMethod(Long orderTaskId, RestJsonWrapperBean wrapper) {
        EmcTestMethodBean testMethod = wrapper.parseUnique(EmcTestMethodBean.class);

        EmcOrderTaskBean orderTask = selectDetail(orderTaskId);
        EmcOrderBean order = orderService.selectDetail(orderTask.getOrderId());

        // 更新样品测试的方法信息
        EmcOrderTaskBean updateOrderTask = new EmcOrderTaskBean();
        BeanUtils.copyProperties(orderTask, updateOrderTask);
        updateOrderTask.setId(orderTaskId);
        updateOrderTask.setMethodId(testMethod.getMethodId());
        updateOrderTask.setMethodName(testMethod.getExt$Item("methodname"));
        EmcMethodBean method = methodService.selectDetail(testMethod.getMethodId());
        updateOrderTask.setMethodVersionId(method.getMaxVersionId());
        updateOrderTask.setTestMethodBasis(method.getTestMethodBasis());

        getDao().update(updateOrderTask, "methodId", "methodName", "methodVersionId", "testMethodBasis");

        // 插入样品测试记录表
        List<EmcOrderTaskBean> updateOrderTaskList = new ArrayList<EmcOrderTaskBean>();
        updateOrderTaskList.add(updateOrderTask);
        orderTaskLogService.insert(updateOrderTaskList, EmcBizConstant.ORDER_TASK_LOG_EDIT_METHOD, EmcBizConstant.ORDER_TASK_LOG_CHECK_METHOD_EDIT);

        // 更新分析项，根据分析项名称，更新相同的，添加新增的，删除没有的
        List<EmcItemBean> newItemList = itemService.getDao().selectListByOneColumnValue(testMethod.getId(), "testMethodId").stream()
                .filter(e -> ObjectUtils.equals(orderTask.getAnalysisType(), e.getAnalysisCategory())).collect(Collectors.toList());
        List<EmcOrderTaskResultBean> oldOrderTaskResultList = orderTaskResultService.getDao().selectListByOneColumnValue(orderTaskId, "orderTaskId");

        List<String> newItemNameList = newItemList.stream().map(e -> e.getItemName()).distinct().collect(Collectors.toList());
        List<Long> delOrderTaskResultIdList = oldOrderTaskResultList.stream().filter(e -> !newItemNameList.contains(e.getItemName())).map(e -> e.getId()).collect(Collectors.toList());

        List<String> oldItemNameList = oldOrderTaskResultList.stream().map(e -> e.getItemName()).distinct().collect(Collectors.toList());
        List<EmcItemBean> newOrderTaskItemList = newItemList.stream().filter(e -> !oldItemNameList.contains(e.getItemName())).collect(Collectors.toList());
        List<EmcOrderTaskResultBean> insertOrderTaskResultList = getInsertOrderTaskResultList(orderTask, newOrderTaskItemList, order);

        List<EmcOrderTaskResultBean> updateOrderTaskResultList = oldOrderTaskResultList.stream().filter(e -> newItemNameList.contains(e.getItemName())).collect(Collectors.toList());

        updateOrderTaskResultList.forEach(e -> {
            e.setMethodId(testMethod.getMethodId());
            e.setMethodVersionId(method.getMaxVersionId());
        });

        orderTaskResultService.getDao().deleteByIdList(delOrderTaskResultIdList);
        orderTaskResultService.getDao().insert(insertOrderTaskResultList);
        orderTaskResultService.getDao().update(updateOrderTaskResultList, "methodId", "methodVersionId");
    }

    @Override
    public Long insertResultLog(Long id, RestJsonWrapperBean wrapper) {
        Long resultLogId = resultLogService.insert(id, wrapper);
        orderTaskResultService.updateResult(wrapper);
        return resultLogId;
    }

    @Override
    public Page<EmcResultLogBean> selectResultLogRawPaginationByOrderTaskId(Long id, RestJsonWrapperBean wrapper) {
        return resultLogService.selectPaginationByFilter(SearchFilter.instance().match("ORDERTASKID", id).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    public Page<EmcOrderTaskLogBean> selectOrderTaskLogRawPaginationById(Long id, RestJsonWrapperBean wrapper) {
        return orderTaskLogService.selectPaginationByFilter(SearchFilter.instance().match("ORDERTASKID", id).filter(MatchPattern.EQ), wrapper);
    }

    // ----------------------------------------------------------------------
    // 私有方法
    // ----------------------------------------------------------------------
    protected List<EmcOrderTaskResultBean> getInsertOrderTaskResultList(EmcOrderTaskBean orderTask, List<EmcItemBean> itemList, EmcOrderBean order) {
        List<EmcOrderTaskResultBean> insertOrderTaskResultList = new ArrayList<>();

        for (EmcItemBean item : itemList) {
            for (int index = 1; index <= item.getRepeatItem(); index++) {
                EmcOrderTaskResultBean orderTaskResult = new EmcOrderTaskResultBean();
                BeanUtils.copyProperties(item, orderTaskResult);

                orderTaskResult.setId(ApplicationContextHelper.getNextIdentity());
                orderTaskResult.setFolderId(orderTask.getFolderId());
                orderTaskResult.setOrderId(orderTask.getOrderId());
                orderTaskResult.setProjId(orderTask.getProjId());
                orderTaskResult.setOrderTaskId(orderTask.getId());
                orderTaskResult.setTestMethodId(orderTask.getTestMethodId());
                orderTaskResult.setMethodId(orderTask.getMethodId());
                orderTaskResult.setMethodVersionId(orderTask.getMethodVersionId());
                orderTaskResult.setItemId(item.getId());
                orderTaskResult.setItemOrderNo(item.getOrderNo());
                orderTaskResult.setRepeatNo(0L + index);
                orderTaskResult.setRepeatQty(item.getRepeatItem());
                orderTaskResult.setQcCategoryId(orderTask.getQcCategoryId());
                orderTaskResult.setQcCategoryCode(orderTask.getQcCategoryCode());
                orderTaskResult.setQcName(orderTask.getQcName());
                orderTaskResult.setTestId(orderTask.getTestId());
                orderTaskResult.setScriptFlag(item.getScriptFlag());
                orderTaskResult.setResultCategory(item.getResultCategory());
                orderTaskResult.setCycle(order.getCycle());
                orderTaskResult.setOrderNo(order.getOrderNo());
                orderTaskResult.setParentOrderNo(order.getParentOrderNo());
                orderTaskResult.setFrequency(order.getFrequency());
                orderTaskResult.setAnalysisType(item.getAnalysisCategory());

                insertOrderTaskResultList.add(orderTaskResult);
            }
        }

        return insertOrderTaskResultList;
    }

    @Override
    public Page<EmcOrderTaskBean> selectAnalysisDepartmentStatistics(RestJsonWrapperBean wrapper) {
        MapperParameter parameter = wrapper.extractMapFilter();
        PageRowBounds rowBounds = wrapper.extractPageRowBounds();
        return selectPagination(() -> getDao().selectAnalysisDepartmentStatistics(parameter), rowBounds);
    }

    @Override
    public Page<EmcOrderTaskBean> selectPersonnelWorkloadStatistics(RestJsonWrapperBean wrapper) {
        MapperParameter parameter = wrapper.extractMapFilter();
        PageRowBounds rowBounds = wrapper.extractPageRowBounds();
        return selectPagination(() -> getDao().selectPersonnelWorkloadStatistics(parameter), rowBounds);
    }

    /**
     * 合成项目PDF总报表
     *
     * @param orderTaskList
     */
    private void composeProjectPdfReport(List<EmcOrderTaskBean> orderTaskList) {
        // 通过系统参数COMPOSE_PDF来配置是否需要合成PDF
        CoreConstantBean composePdfConstant = coreConstantService.selectByIdIfPresent("COMPOSE_PDF");
        if (composePdfConstant == null || !Constant.YES.equals(composePdfConstant.getConstantValue())) {
            return;
        }

        List<EmcOrderTaskBean> validOrderTaskList = orderTaskList.stream().filter(task -> !StringUtils.isEmpty(task.getRunId())).collect(Collectors.toList());
        if (validOrderTaskList.isEmpty()) {
            return;
        }

        List<String> runIdList = validOrderTaskList.stream().map(EmcOrderTaskBean::getRunId).distinct().collect(Collectors.toList());
        List<String> targetIdList = runIdList.stream().map(runId -> getDao().getTable() + "$" + runId).collect(Collectors.toList());
        List<CoreFileBean> coreFileBeanList = coreFileService.selectListByFilter(SearchFilter.instance().match("TARGETID", targetIdList).filter(MatchPattern.OR).match("EXT", "composePdf").filter(MatchPattern.EQ));

        Map<Long, List<FileRepresentation>> projectPdfMap = new HashMap<>(orderTaskList.size());
        Map<Long, List<EmcOrderTaskBean>> projectOrderTaskMap = validOrderTaskList.stream().filter(task -> !ObjectUtils.isEmpty(task.getProjId())).collect(Collectors.groupingBy(EmcOrderTaskBean::getProjId));
        projectOrderTaskMap.forEach((projectId, projectOrderTaskList) -> {
            List<FileRepresentation> pdfFileList = new ArrayList<>();
            List<String> projectRunIdList = projectOrderTaskList.stream().map(EmcOrderTaskBean::getRunId).distinct().collect(Collectors.toList());

            List<String> projectFileTargetIdList = projectRunIdList.stream().map(s -> getDao().getTable() + "$" + s).collect(Collectors.toList());
            coreFileBeanList.stream().filter(file -> projectFileTargetIdList.contains(file.getTargetId())).forEach(file -> pdfFileList.add(FileRepresentation.of(file)));

            if (!pdfFileList.isEmpty()) {
                projectPdfMap.put(projectId, pdfFileList);
            }
        });

        if (!projectPdfMap.isEmpty()) {
            String projectReportPdfName = I18nHelper.getMessage("T_EMC_RUN.DOWNLOAD.PROJECT_REPORT_PDF_NAME");
            projectPdfMap.entrySet().stream().forEach(r -> {
                String tempFilePath = fileCloudService.mergePdfFile(r.getValue(),
                        FileRepresentation.of(FileScope.temp.name(), "" + System.currentTimeMillis(), StringUtils.randomUUID(8), projectReportPdfName));
                if (!StringUtils.isEmpty(tempFilePath)) {
                    CoreFileBean coreFileBean = new CoreFileBean();
                    coreFileBean.setTargetId("T_EMC_PROJECT" + "$" + r.getKey());
                    coreFileBean.setScope(FileScope.secure.name());
                    coreFileService.upload(coreFileBean, new File(tempFilePath));
                }
            });
        }
    }

    /**
     * 编辑测试中，添加新的样品测试和结果
     *
     * @param orderList
     * @param allOldOrderTaskList
     * @param allNewOrderItemGroupList
     * @param model
     *            数据来源于哪个模块
     */
    @Override
    public void setDataByOrderList(List<EmcOrderBean> orderList, List<EmcOrderTaskBean> allOldOrderTaskList, List<EmcOrderItemGroupBean> allNewOrderItemGroupList, String model) {
        this.setDataByOrderList(orderList, allOldOrderTaskList, allNewOrderItemGroupList, model, null);
    }

    /**
     * 编辑测试中，添加新的样品测试和结果,并设置样品测试表初始状态
     *
     * @param orderList
     * @param allOldOrderTaskList
     * @param allNewOrderItemGroupList
     * @param model
     *            数据来源于哪个模块
     * @param status
     *            样品测试表设置的初始状态
     */
    @Override
    @Transactional
    public void setDataByOrderList(List<EmcOrderBean> orderList, List<EmcOrderTaskBean> allOldOrderTaskList, List<EmcOrderItemGroupBean> allNewOrderItemGroupList, String model, String status) {
        // 查询容器信息
        List<Long> testMethodIdList = allNewOrderItemGroupList.stream().map(e -> e.getTestMethodId()).distinct().collect(Collectors.toList());
        List<EmcTestMethodOrderCategoryBean> filterTestMethodOrderCategoryList = testMethodIdList.stream().map(testMethodId -> {
            EmcTestMethodOrderCategoryBean testMethodOrderCategory = new EmcTestMethodOrderCategoryBean();

            testMethodOrderCategory.setTestMethodId(testMethodId);
            testMethodOrderCategory.setMonitorTmplId(orderList.get(0).getMonitorTmplId());

            return testMethodOrderCategory;
        }).collect(Collectors.toList());

        List<Long> testMethodOrderCategoryIdList = testMethodOrderCategoryService.getDao()
                .selectList(filterTestMethodOrderCategoryList, Arrays.asList("TESTMETHODID", "MONITORTMPLID"), Arrays.asList("ID")).stream().map(e -> e.getId()).collect(Collectors.toList());
        List<EmcTestMethodOrderCategoryBean> testMethodOrderCategoryList = testMethodOrderCategoryIdList.isEmpty() ? Collections.emptyList()
                : testMethodOrderCategoryService.selectListByFilter(SearchFilter.instance().match("ID", testMethodOrderCategoryIdList).filter(MatchPattern.OR));

        List<EmcGroupItemBean> groupItemList = groupItemService.getDao().selectListByOneColumnValues(allNewOrderItemGroupList.stream().map(e -> e.getItemGroupId()).collect(Collectors.toList()),
                "ITEMGROUPID");

        List<EmcItemBean> itemList = itemService.getDao().selectListByOneColumnValues(groupItemList.stream().map(e -> e.getItemId()).collect(Collectors.toList()), "ID");

        List<EmcOrderTaskBean> insertOrderTaskList = new ArrayList<>();
        List<EmcOrderTaskResultBean> insertOrderTaskResultList = new ArrayList<>();

        // 判断当前编辑的样品是否有父样，对于有父样的，需要关联对应的父样样品测试ID
        List<String> parentOrderNoList = orderList.stream().filter(order -> !StringUtils.isEmpty(order.getParentOrderNo())).map(EmcOrderBean::getParentOrderNo).collect(Collectors.toList());
        List<EmcOrderTaskBean> parentOrderTaskList = Collections.emptyList();
        if (!parentOrderNoList.isEmpty()) {
            List<EmcOrderBean> parentOrderList = orderService.getDao().selectListByOneColumnValues(parentOrderNoList, "ORDERNO", Arrays.asList("ID", "ORDERNO"));
            List<Long> parentOrderIdList = parentOrderList.stream().map(EmcOrderBean::getId).collect(Collectors.toList());
            parentOrderTaskList = getDao().selectListByOneColumnValues(parentOrderIdList, "ORDERID", Arrays.asList("ID", "ORDERID", "TESTID", "TESTMETHODID"));
        }
        List<EmcOrderTaskBean> validParentOrderTaskList = parentOrderTaskList;

        // 添加新的分析项组下的样品测试和样品测试结果
        for (EmcOrderBean o : orderList) {

            // 每一个样品下需要添加的分析项组
            List<EmcOrderTaskBean> oldOrderTaskList = allOldOrderTaskList.stream().filter(e -> ObjectUtils.equals(o.getId(), e.getOrderId())).collect(Collectors.toList());

            List<EmcOrderItemGroupBean> newOrderItemGroupList = allNewOrderItemGroupList.stream().filter(e -> ObjectUtils.equals(o.getId(), e.getOrderId())).collect(Collectors.toList());
            newOrderItemGroupList.forEach(a -> {
                EmcOrderTaskBean matchOrderItemGroup = oldOrderTaskList.stream().filter(e -> ObjectUtils.equals(a.getTestMethodId(), e.getTestMethodId())).findFirst().orElse(null);

                // 这个分析项组需要添加
                if (matchOrderItemGroup == null) {

                    // 添加样品测试容器信息
                    List<EmcTestMethodOrderCategoryBean> orderCategoryList = testMethodOrderCategoryList.stream().filter(c -> ObjectUtils.equals(a.getTestMethodId(), c.getTestMethodId()))
                            .collect(Collectors.toList());

                    if (!orderCategoryList.isEmpty()) {

                        EmcTestMethodOrderCategoryBean orderCategory = orderCategoryList.get(0);

                        a.setContainerCategoryId(orderCategory.getContainerCategoryId());
                        a.setContainerCategoryCode(orderCategory.getExt$Item("containercategorycode"));
                        a.setContainerCategoryName(orderCategory.getExt$Item("containercategoryname"));
                        a.setContainerGroup(orderCategory.getContainerGroup());
                        a.setFixative(orderCategory.getFixative());

                    }

                    // 添加样品测试
                    List<EmcGroupItemBean> filterGroupItemList = groupItemList.stream().filter(e -> ObjectUtils.equals(e.getItemGroupId(), a.getItemGroupId())).collect(Collectors.toList());
                    List<Long> itemIdList = filterGroupItemList.stream().map(e -> e.getItemId()).collect(Collectors.toList());

                    List<EmcItemBean> filterItemList = itemList.stream().filter(e -> itemIdList.contains(e.getId()) && "1".equals(e.getActivatedFlag())).collect(Collectors.toList());

                    // 判断当前样品分析项组下的启用的分析项有几种分析类型，决定插入几条样品测试数据
                    List<String> analysisCategoryList = filterItemList.stream().map(e -> e.getAnalysisCategory()).distinct().collect(Collectors.toList());

                    EmcOrderTaskBean parentOrderTask = validParentOrderTaskList.stream().filter(t -> ObjectUtils.equals(t.getTestId(), a.getTestId()) && ObjectUtils.equals(t.getTestMethodId(), a.getTestMethodId())).findFirst().orElse(null);

                    for (String analysisCategory : analysisCategoryList) {

                        // 样品测试
                        EmcOrderTaskBean orderTask = new EmcOrderTaskBean();

                        BeanUtils.copyProperties(a, orderTask);

                        orderTask.setId(ApplicationContextHelper.getNextIdentity());
                        orderTask.setAnalysisType(analysisCategory);
                        orderTask.setFolderId(o.getFolderId());
                        orderTask.setProjId(o.getProjId());
                        orderTask.setOrderId(o.getId());
                        if (!StringUtils.isEmpty(status)) {
                            orderTask.setStatus(status);
                        }
                        orderTask.setMonitorTmplId(o.getMonitorTmplId());
                        orderTask.setOrderCategory(o.getOrderCategory());
                        orderTask.setEquiptAcquNo(o.getLabelNo());
                        orderTask.setQcCategoryId(o.getQcCategoryId());
                        orderTask.setQcCategoryCode(o.getQcCategoryCode());
                        orderTask.setQcName(o.getQcName());

                        // 如果父测试不为空，关联父样测试ID
                        if (parentOrderTask != null) {
                            orderTask.setParentId(parentOrderTask.getId());
                        }

                        insertOrderTaskList.add(orderTask);

                        // 样品测试结果
                        List<EmcItemBean> analysisCategoryItemList = filterItemList.stream().filter(e -> ObjectUtils.equals(e.getAnalysisCategory(), analysisCategory)).collect(Collectors.toList());

                        insertOrderTaskResultList.addAll(getInsertOrderTaskResultList(orderTask, analysisCategoryItemList, o));
                    }
                }
            });
        }

        getDao().insert(insertOrderTaskList);
        orderTaskResultService.getDao().insert(insertOrderTaskResultList);

        // 插入样品测试记录表
        orderTaskLogService.insert(insertOrderTaskList, EmcBizConstant.ORDER_TASK_LOG_ADD_TEST, model);

        // 插入新的样品分析项组
        orderItemGroupService.getDao().insert(allNewOrderItemGroupList);

    }

    @Override
    public Page<EmcOrderTaskResultBean> selectUpdateIndexOrderTaskResult(Long orderTaskId, RestJsonWrapperBean wrapper) {
        return orderTaskResultService.selectUpdateIndexOrderTaskResult(SearchFilter.instance().match("ORDERTASKID", orderTaskId).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    public Page<EmcOrderTaskResultBean> selectAnalysisProjectTaskResultValidation(RestJsonWrapperBean wrapper) {
        return orderTaskResultService.selectAnalysisProjectTaskResultValidation(wrapper);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void cleanResultIndex(RestJsonWrapperBean jsonWrapper) {
        List<Long> orderTaskIdList = jsonWrapper.parseId(Long.class);
        List<EmcOrderTaskResultBean> resultList = orderTaskResultService.getDao().selectListByOneColumnValues(orderTaskIdList, "ORDERTASKID");
        resultList.stream().forEach(e -> {
            e.setIndexLowLimit(null);
            e.setIndexHighLimit(null);
        });

        orderTaskResultService.getDao().update(resultList, "INDEXLOWLIMIT", "INDEXHIGHLIMIT");
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void updateResultIndex(RestJsonWrapperBean jsonWrapper) {
        List<EmcOrderTaskBean> orderTaskList = jsonWrapper.parse(EmcOrderTaskBean.class);
        List<EmcIndexSetBean> indexSetList = jsonWrapper.parse(EmcIndexSetBean.class);
        List<Long> orderTaskIdList = orderTaskList.stream().map(e -> e.getId()).collect(Collectors.toList());
        List<EmcOrderTaskResultBean> resultList = orderTaskResultService.getDao().selectListByOneColumnValues(orderTaskIdList, "ORDERTASKID");
        Map<Long,String> propertytmplId2NameMap = propertyTmplService.getDao().selectListByOneColumnValue("evaluationStandard", "TEMPLATEUSE").stream().collect(Collectors.toMap(e -> e.getId(),e -> e.getTemplateName(),  (s1, s2) -> s1));
        List<Long> indexItemIdList = indexSetList.stream().map(e -> e.getIndexItemId()).collect(Collectors.toList());
        List<EmcIndexTestItemBean> indexTestItemList = indexTestItemService.getDao().selectListByOneColumnValues(indexItemIdList, "INDEXITEMID");

        for (EmcIndexSetBean indexSet : indexSetList) {
            Long indexItemId = indexSet.getIndexItemId();// 指标项目ID
            String indexLowLimit = indexSet.getIndexLowLimit(); // 指标低限
            String indexHighLimit = indexSet.getIndexHighLimit(); // 指标高限

            List<EmcIndexTestItemBean> filterIndexTestItemList = indexTestItemList.stream().filter(f -> ObjectUtils.equals(indexItemId, f.getIndexItemId())).collect(Collectors.toList());
            resultList.stream().forEach(e -> {
                if (filterIndexTestItemList.stream().anyMatch(b -> ObjectUtils.equals(e.getItemId(), b.getItemId()))) {
                    e.setIndexLowLimit(indexLowLimit);
                    e.setIndexHighLimit(indexHighLimit);
                }
            });
        }

        // 更新昼夜评价的指标高低限
        Optional<Long> dayNightEvalPropertyTmplIdOption = propertytmplId2NameMap.entrySet().stream().filter(e -> e.getValue().contains(EmcConstant.DAY_NIGHT_EVAL_PROPERTY_TMPL_NAME)).map(e -> e.getKey()).findFirst();
        if(dayNightEvalPropertyTmplIdOption.isPresent()){
            List<EmcIndexCategoryBean> indexCategoryList = indexCategoryService.getDao().selectListByOneColumnValue(dayNightEvalPropertyTmplIdOption.get(), "PROPERTYTMPLID");
            if (!indexCategoryList.isEmpty()) {
                List<Long> indexCategoryIdList = indexCategoryList.stream().map(EmcIndexCategoryBean::getId).collect(Collectors.toList());
                List<String> indexIdList = indexSetList.stream().filter(s -> indexCategoryIdList.contains(s.getIndexCategoryId())).map(EmcIndexSetBean::getIndexId).distinct().collect(Collectors.toList());

                if (!indexIdList.isEmpty()) {
                    // 从指标设置中过滤指标项目ID
                    List<Long> validIndexItemIdList = indexSetList.stream().filter(s -> indexIdList.contains(s.getIndexId())).map(EmcIndexSetBean::getIndexItemId).distinct().collect(Collectors.toList());
                    List<Long> filterItemIdList = indexTestItemList.stream().filter(i -> validIndexItemIdList.contains(i.getIndexItemId())).map(EmcIndexTestItemBean::getItemId)
                            .collect(Collectors.toList());
                    List<Long> indexSetIdList = indexSetList.stream().filter(s -> indexIdList.contains(s.getIndexId())).map(EmcIndexSetBean::getId).collect(Collectors.toList());
                    List<EmcPropertyDataBean> propertyDataList = propertyDataService.getDao().selectListByOneColumnValues(indexSetIdList, "BIZID");

                    LocalTime dayStartTime = LocalTime.of(5, 59); // 昼间开始时间 6:00
                    LocalTime dayEndTime = LocalTime.of(22, 00); // 昼间结束时间 21:59

                    List<Long> orderIdList = orderTaskList.stream().map(EmcOrderTaskBean::getOrderId).distinct().collect(Collectors.toList());
                    List<EmcOrderBean> orderList = orderService.getDao().selectListByIds(orderIdList);

                    resultList.stream().filter(r -> filterItemIdList.contains(r.getItemId())).forEach(r -> {
                        EmcOrderTaskBean orderTaskBean = orderTaskList.stream().filter(t -> ObjectUtils.equals(t.getId(), r.getOrderTaskId())).findFirst().get();
                        indexSetList.stream().filter(s -> ObjectUtils.equals(s.getIndexId(), orderTaskBean.getIndexId())).findFirst().ifPresent(s -> {
                            EmcOrderBean orderBean = orderList.stream().filter(o -> ObjectUtils.equals(o.getId(), r.getOrderId())).findFirst().get();
                            propertyDataList.stream().filter(d -> ObjectUtils.equals(d.getBizId(), s.getId())).findFirst().ifPresent(d -> {
                                String sampleBeginTime = orderBean.getSampleBeginTime();
                                if (!StringUtils.isEmpty(sampleBeginTime)) {
                                    String[] pairs = sampleBeginTime.split(":");
                                    LocalTime sampleTime = LocalTime.of(NumberUtils.parseInt(pairs[0]), NumberUtils.parseInt(pairs[1]));
                                    if (sampleTime.isAfter(dayStartTime) && sampleTime.isBefore(dayEndTime)) {
                                        r.setIndexHighLimit(d.getExt1()); // 昼间指标高限
                                    } else {
                                        r.setIndexHighLimit(d.getExt2()); // 夜间指标低限
                                    }
                                }
                            });
                        });
                    });
                }
            }
        }


        //更新内插法、外插法的指标高限
        Optional<Long> interpolateEvalPropertyTmplIdOption = propertytmplId2NameMap.entrySet().stream().filter(e -> e.getValue().contains(EmcConstant.INTERPOLATE_EVAL_PROPERTY_TMPL_NAME)).map(e -> e.getKey()).findFirst();

        if(interpolateEvalPropertyTmplIdOption.isPresent()){
            List<EmcIndexCategoryBean> indexCategoryOfInterpolationList = indexCategoryService.getDao().selectListByOneColumnValue(interpolateEvalPropertyTmplIdOption.get(), "PROPERTYTMPLID");
            if (!indexCategoryOfInterpolationList.isEmpty()) {
                List<Long> indexCategoryIdList = indexCategoryOfInterpolationList.stream().map(EmcIndexCategoryBean::getId).collect(Collectors.toList());
                List<String> indexIdList = indexSetList.stream().filter(s -> indexCategoryIdList.contains(s.getIndexCategoryId())).map(EmcIndexSetBean::getIndexId).distinct().collect(Collectors.toList());
                // 从指标设置中过滤指标项目ID
                List<Long> validIndexItemIdList = indexSetList.stream().filter(s -> indexIdList.contains(s.getIndexId())).map(EmcIndexSetBean::getIndexItemId).distinct().collect(Collectors.toList());
                List<Long> filterItemIdList = indexTestItemList.stream().filter(i -> validIndexItemIdList.contains(i.getIndexItemId())).map(EmcIndexTestItemBean::getItemId)
                        .collect(Collectors.toList());
                List<Long> indexSetIdList = indexSetList.stream().filter(s -> indexIdList.contains(s.getIndexId())).map(EmcIndexSetBean::getId).collect(Collectors.toList());
                List<EmcPropertyDataBean> propertyDataList = propertyDataService.getDao().selectListByOneColumnValues(indexSetIdList, "BIZID");

                //获取烟囱高度,属于一个点位
                Long folderId = resultList.stream().map(e -> e.getFolderId()).findFirst().get();
                List<EmcPropertyDataBean> propertyDataListOfChimney = propertyDataService.selectListByFilter(SearchFilter.instance().match("TABLENAME", "T_EMC_FOLDER").filter(MatchPattern.EQ).match("bizId", folderId).filter(MatchPattern.EQ));
                Double chimneyHeight = NumberUtils.parseDouble(propertyDataListOfChimney.get(0).getExt50());
                //通过烟囱高度比较确定使用哪个方法进行计算
                List<EmcPropertyFieldBean> propertyFieldList = propertyFieldService.getDao().selectListByOneColumnValue(interpolateEvalPropertyTmplIdOption.get(), "TMPLID");
                List<EmcPropertyFieldBean> filedNameListBySorted = propertyFieldList.stream().sorted(Comparator.comparing(EmcPropertyFieldBean::getFieldName)).collect(Collectors.toList());
                double maxFieldName = propertyFieldList.stream().mapToDouble(e -> NumberUtils.parseDouble(e.getFieldName())).max().orElse(0.00);
                //比较最大高度
                EmcPropertyFieldBean field = propertyFieldList.stream().filter(p -> ObjectUtils.equals(maxFieldName, NumberUtils.parseDouble(p.getFieldName()))).findFirst().get();
                if (chimneyHeight == maxFieldName) {
                    //说明就是最高高度
                    String internalField = field.getInternalField();
                    indexSetList.forEach(e -> {
                        EmcPropertyDataBean emcPropertyDataBean = propertyDataList.stream().filter(f -> ObjectUtils.equals(e.getId(), f.getBizId())).findFirst().get();
                        String propertyValue = BeanUtils.getPropertyValue(emcPropertyDataBean, internalField).toString();
                        resultList.stream().forEach(r -> {
                            if (filterItemIdList.stream().anyMatch(b -> ObjectUtils.equals(r.getItemId(), b))) {
                                r.setIndexHighLimit(propertyValue);
                            }
                        });
                    });
                } else if (chimneyHeight > maxFieldName) {
                    //使用外推法
                    String internalField = field.getInternalField();
                    indexSetList.forEach(e -> {
                        EmcPropertyDataBean emcPropertyDataBean = propertyDataList.stream().filter(f -> ObjectUtils.equals(e.getId(), f.getBizId())).findFirst().get();
                        String propertyValue = BeanUtils.getPropertyValue(emcPropertyDataBean, internalField).toString();
                        double mul = ArithUtils.mul(ArithUtils.div(chimneyHeight, maxFieldName), ArithUtils.div(chimneyHeight, maxFieldName));
                        String highLimit = ObjectUtils.toString(ArithUtils.mul(NumberUtils.parseDouble(propertyValue), mul));
                        resultList.stream().forEach(r -> {
                            if (filterItemIdList.stream().anyMatch(b -> ObjectUtils.equals(r.getItemId(), b))) {
                                r.setIndexHighLimit(highLimit);
                            }
                        });
                    });
                } else {
                    //内插法,如果区间有相等的值则直接更新
                    for (EmcPropertyFieldBean fieldBean : filedNameListBySorted) {
                        if (chimneyHeight == NumberUtils.parseDouble(fieldBean.getFieldName())) {
                            String internalField = fieldBean.getInternalField();
                            indexSetList.forEach(e -> {
                                EmcPropertyDataBean emcPropertyDataBean = propertyDataList.stream().filter(f -> ObjectUtils.equals(e.getId(), f.getBizId())).findFirst().get();
                                String propertyValue = BeanUtils.getPropertyValue(emcPropertyDataBean, internalField).toString();
                                resultList.stream().forEach(r -> {
                                    if (filterItemIdList.stream().anyMatch(b -> ObjectUtils.equals(r.getItemId(), b))) {
                                        r.setIndexHighLimit(propertyValue);
                                    }
                                });
                            });
                        } else {
                            for (int i = 1; i < filedNameListBySorted.size(); i++) {
                                if (NumberUtils.parseDouble(filedNameListBySorted.get(i - 1).getFieldName()) < chimneyHeight && chimneyHeight < NumberUtils.parseDouble(filedNameListBySorted.get(i).getFieldName())) {
                                    String maxFieldNameForChimneyHeight = filedNameListBySorted.get(i - 1).getFieldName();
                                    String minFieldNameForChimneyHeight = filedNameListBySorted.get(i).getFieldName();
                                    String maxInternalField = filedNameListBySorted.get(i - 1).getInternalField();
                                    String minInternalField = filedNameListBySorted.get(i).getInternalField();
                                    indexSetList.forEach(e -> {
                                        EmcPropertyDataBean emcPropertyDataBean = propertyDataList.stream().filter(f -> ObjectUtils.equals(e.getId(), f.getBizId())).findFirst().get();
                                        String maxPropertyValue = BeanUtils.getPropertyValue(emcPropertyDataBean, maxInternalField).toString();
                                        String minPropertyValue = BeanUtils.getPropertyValue(emcPropertyDataBean, minInternalField).toString();
                                        double sub = ArithUtils.sub(NumberUtils.parseDouble(minPropertyValue), NumberUtils.parseDouble(maxPropertyValue));
                                        double subHeightQ1 = ArithUtils.sub(chimneyHeight, NumberUtils.parseDouble(maxFieldNameForChimneyHeight));
                                        double subHeightQ2 = ArithUtils.sub(NumberUtils.parseDouble(minFieldNameForChimneyHeight), NumberUtils.parseDouble(maxFieldNameForChimneyHeight));
                                        double mul = ArithUtils.mul(sub, ArithUtils.div(subHeightQ1, subHeightQ2));
                                        String propertyValue = ObjectUtils.toString(ArithUtils.add(NumberUtils.parseDouble(maxPropertyValue), mul));
                                        resultList.stream().forEach(r -> {
                                            if (filterItemIdList.stream().anyMatch(b -> ObjectUtils.equals(r.getItemId(), b))) {
                                                r.setIndexHighLimit(propertyValue);
                                            }
                                        });
                                    });
                                }
                            }
                        }
                    }
                }
            }
        }

        orderTaskResultService.getDao().update(resultList, "INDEXLOWLIMIT", "INDEXHIGHLIMIT");
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void updateCupNo(RestJsonWrapperBean wrapper) {
        List<EmcOrderTaskBean> orderTaskList = wrapper.parse(EmcOrderTaskBean.class);
        List<EmcOrderTaskBean> filterList = new ArrayList<>();
        orderTaskList.stream().forEach(e -> {
            EmcOrderTaskBean filter = new EmcOrderTaskBean();
            filter.setRunId(e.getRunId());
            filter.setStatus(EmcBizConstant.ORDERTASK_STATUS_LOGGED);
            filter.setOrderId(e.getOrderId());
            filter.setCupNo(e.getCupNo());
            filterList.add(filter);
        });
        List<EmcOrderTaskBean> updateOrderTaskList = getDao().selectList(filterList, Arrays.asList("RUNID", "STATUS", "ORDERID"), Collections.emptyList());

        for (EmcOrderTaskBean f : filterList) {
            Long orderId = f.getOrderId();
            Long cupNo = f.getCupNo();
            for (EmcOrderTaskBean e : updateOrderTaskList) {
                if (ObjectUtils.equals(e.getOrderId(), orderId)) {
                    e.setCupNo(cupNo);
                }
            }
        }
        getDao().update(updateOrderTaskList, "CUPNO");
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void updateEquiptAcquNo(RestJsonWrapperBean wrapper) {
        List<EmcOrderTaskBean> orderTaskList = wrapper.parse(EmcOrderTaskBean.class);
        List<EmcOrderTaskBean> filterList = new ArrayList<>();
        orderTaskList.stream().forEach(e -> {
            EmcOrderTaskBean filter = new EmcOrderTaskBean();
            filter.setRunId(e.getRunId());
            filter.setStatus(EmcBizConstant.ORDERTASK_STATUS_LOGGED);
            filter.setOrderId(e.getOrderId());
            filter.setEquiptAcquNo(e.getEquiptAcquNo());
            filterList.add(filter);
        });
        List<EmcOrderTaskBean> updateOrderTaskList = getDao().selectList(filterList, Arrays.asList("RUNID", "STATUS", "ORDERID"), Collections.emptyList());

        for (EmcOrderTaskBean f : filterList) {
            Long orderId = f.getOrderId();
            String equiptAcquNo = f.getEquiptAcquNo();
            for (EmcOrderTaskBean e : updateOrderTaskList) {
                if (ObjectUtils.equals(e.getOrderId(), orderId)) {
                    e.setEquiptAcquNo(equiptAcquNo);
                }
            }
        }
        getDao().update(updateOrderTaskList, "EQUIPTACQUNO");
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void updateStockBatchNo(RestJsonWrapperBean wrapper) {
        List<EmcOrderTaskBean> orderTaskList = wrapper.parse(EmcOrderTaskBean.class);
        List<EmcOrderTaskBean> filterList = new ArrayList<>();
        orderTaskList.stream().forEach(e -> {
            EmcOrderTaskBean filter = new EmcOrderTaskBean();
            filter.setRunId(e.getRunId());
            filter.setStatus(EmcBizConstant.ORDERTASK_STATUS_LOGGED);
            filter.setOrderId(e.getOrderId());
            filter.setStockBatchNo(e.getStockBatchNo());
            filterList.add(filter);
        });
        List<EmcOrderTaskBean> updateOrderTaskList = getDao().selectList(filterList, Arrays.asList("RUNID", "STATUS", "ORDERID"), Collections.emptyList());

        for (EmcOrderTaskBean f : filterList) {
            Long orderId = f.getOrderId();
            String stockBatchNo = f.getStockBatchNo();
            for (EmcOrderTaskBean e : updateOrderTaskList) {
                if (ObjectUtils.equals(e.getOrderId(), orderId)) {
                    e.setStockBatchNo(stockBatchNo);
                }
            }
        }
        getDao().update(updateOrderTaskList, "STOCKBATCHNO");
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void updateSampleData(RestJsonWrapperBean wrapper) {
        EmcOrderTaskBean orderTask = wrapper.parseUnique(EmcOrderTaskBean.class);
        EmcOrderTaskBean filter = new EmcOrderTaskBean();
        
        filter.setOrderId(NumberUtils.parseLong(wrapper.getFilterValue("orderId")));
        filter.setContainerCategoryId(orderTask.getContainerCategoryId());
        filter.setContainerCategoryName(orderTask.getContainerCategoryName());
        filter.setContainerGroup(orderTask.getContainerGroup());
        filter.setFixative(orderTask.getFixative());
        List<EmcOrderTaskBean> updateOrderTaskList = getDao().selectList(filter, Arrays.asList("ORDERID", "CONTAINERCATEGORYID", "CONTAINERCATEGORYNAME", "CONTAINERGROUP", "FIXATIVE"), Collections.emptyList());

        for (EmcOrderTaskBean e : updateOrderTaskList) {
            e.setStoreCondition(orderTask.getStoreCondition());
        }
        getDao().updateIfChanged(updateOrderTaskList);
    }

    @Override
    public void syncFolderContainerInfo(RestJsonWrapperBean wrapper) {
        List<EmcFolderBean> folderList = wrapper.parse(EmcFolderBean.class);
        if (folderList.isEmpty()) {
            return;
        }

        List<Long> folderIdList = folderList.stream().map(EmcFolderBean::getId).collect(Collectors.toList());
        List<EmcOrderTaskBean> relatedOrderTaskList = getDao().selectListByOneColumnValues(folderIdList, "FOLDERID", Arrays.asList("ID", "TESTMETHODID", "MONITORTMPLID"));
        syncContainerInfo(relatedOrderTaskList);
        
    }

    @Override
    public Page<EmcOrderTaskBean> selectUnRunnableList(RestJsonWrapperBean wrapper) {
        MapperParameter parameter = wrapper.extractMapFilter();
        PageRowBounds rowBounds = wrapper.extractPageRowBounds();
        return MybatisPageHelper.get(rowBounds, () -> getDao().selectUnRunnableList(parameter));
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void updateAnaOrg(String ids, RestJsonWrapperBean wrapper) {
        List<Map<String, Object>> mapList = wrapper.getBodyList().get(0).stream().map(JSON::parseObject).collect(Collectors.toList());
        Object analysisOrgId = mapList.get(0).get("analysisOrgId");
        Object analysisOrgName = mapList.get(0).get("analysisOrgName");
        List<Long> idList = new ArrayList<>();
        if (!StringUtils.isEmpty(ids)) {
            String [] str = ids.split(",");
            for (int i = 0; i < str.length; i++) {
                if (!StringUtils.isEmpty(str[i])) {
                    Long id = Long.parseLong(str[i]);
                    idList.add(id);
                }
            }
        }
        List<EmcOrderTaskBean> orderTaskList = selectListByIds(idList);
        if (orderTaskList.size() > 0) {
            orderTaskList.forEach(ot ->{
                ot.setAnalysisOrgId(analysisOrgId.toString());
                ot.setAnalysisOrgName(analysisOrgName.toString());
                ot.setInputUserId("");
                ot.setInputUserName("");
            });
        }
        getDao().update(orderTaskList, "ANALYSISORGID", "ANALYSISORGNAME","INPUTUSERID","INPUTUSERNAME");
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void updateAnaUser(String ids, RestJsonWrapperBean wrapper) {
        List<Map<String, Object>> mapList = wrapper.getBodyList().get(0).stream().map(JSON::parseObject).collect(Collectors.toList());
        Object inputUserId = mapList.get(0).get("inputUserId");
        Object inputUserName = mapList.get(0).get("inputUserName");
        List<Long> idList = new ArrayList<Long>();
        if (!StringUtils.isEmpty(ids)) {
            String [] str = ids.split(",");
            for (int i = 0; i < str.length; i++) {
                if (!StringUtils.isEmpty(str[i])) {
                    Long id = Long.parseLong(str[i]);
                    idList.add(id);
                }
            }
        }
        List<EmcOrderTaskBean> orderTaskList = selectListByIds(idList);
        if (orderTaskList.size() > 0) {
            orderTaskList.forEach(ot ->{
                ot.setInputUserId(inputUserId.toString());
                ot.setInputUserName(inputUserName.toString());
            });
        }
        getDao().update(orderTaskList, "INPUTUSERID", "INPUTUSERNAME");
    }

    @Override
    public Page<EmcOrderTaskBean> selectRunnableAnalAssignList(RestJsonWrapperBean wrapper) {
        MapperParameter parameter = wrapper.extractMapFilter();
        PageRowBounds rowBounds = wrapper.extractPageRowBounds();
        return MybatisPageHelper.get(rowBounds, () -> getDao().selectRunnableAnalAssignList(parameter));
    }
}
