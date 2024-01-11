package com.sunwayworld.emc.module.bp.project.projscheme.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.sunwayworld.emc.module.bp.project.project.bean.EmcProjectBean;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.bean.EmcMonitorTmplBean;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.service.EmcMonitorTmplService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.framework.constant.EmcBizConstant;
import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.emc.module.bp.bp.bean.EmcFolderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskResultBean;
import com.sunwayworld.emc.module.bp.bp.service.EmcFolderService;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderService;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskResultService;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskService;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcOrderItemGroupBean;
import com.sunwayworld.emc.module.bp.project.project.service.EmcOrderItemGroupService;
import com.sunwayworld.emc.module.bp.project.project.service.EmcProjectService;
import com.sunwayworld.emc.module.bp.project.projscheme.bean.EmcProjDTO;
import com.sunwayworld.emc.module.bp.project.projscheme.bean.EmcProjOrderBean;
import com.sunwayworld.emc.module.bp.project.projscheme.bean.EmcProjSchemeBean;
import com.sunwayworld.emc.module.bp.project.projscheme.dao.EmcProjSchemeDao;
import com.sunwayworld.emc.module.bp.project.projscheme.service.EmcProjOrderService;
import com.sunwayworld.emc.module.bp.project.projscheme.service.EmcProjSchemeService;
import com.sunwayworld.emc.module.bp.project.projscheme.validator.EmcInitSchemeValidator;
import com.sunwayworld.emc.module.mdm.basemdm.bizcategory.bean.EmcBizCategoryBean;
import com.sunwayworld.emc.module.mdm.basemdm.bizcategory.service.EmcBizCategoryService;
import com.sunwayworld.emc.module.mdm.basemdm.indexitem.bean.EmcIndexTestItemBean;
import com.sunwayworld.emc.module.mdm.basemdm.indexitem.service.EmcIndexTestItemService;
import com.sunwayworld.emc.module.mdm.bizmdm.client.bean.EmcMonitorPointBean;
import com.sunwayworld.emc.module.mdm.bizmdm.client.bean.EmcMonitorPointSchemeBean;
import com.sunwayworld.emc.module.mdm.bizmdm.client.service.EmcMonitorPointSchemeService;
import com.sunwayworld.emc.module.mdm.bizmdm.client.service.EmcMonitorPointService;
import com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.bean.EmcIndexSetBean;
import com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.service.EmcIndexSetService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcGroupItemBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcTestMethodBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcTestMethodOrderCategoryBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcGroupItemService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcTestMethodOrderCategoryService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcTestMethodService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.framework.validator.data.annotation.ValidateDataWith;
import com.sunwayworld.module.mdm.org.bean.CoreOrgBean;
import com.sunwayworld.module.mdm.org.service.CoreOrgService;

/**
 * @author yuanhh@sunwayworld.com
 * @date 2019-10-08
 */

@Repository
@GikamBean
public class EmcProjSchemeServiceImpl implements EmcProjSchemeService {

    @Autowired
    @Lazy
    private EmcProjectService projectService;

    @Autowired
    @Lazy
    private EmcMonitorTmplService monitorTmplService;

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
    private EmcMonitorPointSchemeService monitorPointSchemeService;

    @Autowired
    private EmcIndexSetService indexSetService;

    @Autowired
    private EmcIndexTestItemService indexTestItemService;

    @Autowired
    private EmcProjSchemeDao projSchemeDao;

    @Autowired
    private CoreOrgService orgService;

    @Autowired
    private EmcBizCategoryService emcBizCategoryService;

    @Override
    @SuppressWarnings("unchecked")
    public EmcProjSchemeDao getDao() {
        return projSchemeDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        List<EmcProjSchemeBean> schemeList = jsonWrapper.parse(EmcProjSchemeBean.class);
        List<EmcProjOrderBean> orderList = jsonWrapper.parse(EmcProjOrderBean.class);

        int qty = schemeList.size();
        Long projId = schemeList.get(0).getProjId();
        EmcProjectBean emcProject = projectService.selectById(projId);
        List<String> folderNoList = generateFolderNosByProjId(projId, projectService.selectDetail(projId).getProjNo(), qty);

        // 将监测点管理下维护的样品指标带过了
        List<Long> monitorPointIdList = schemeList.stream().filter(e -> !ObjectUtils.isEmpty(e.getMonitorPointId())).map(e -> e.getMonitorPointId()).distinct().collect(Collectors.toList());
        List<EmcMonitorPointBean> monitorPointList = Collections.emptyList();
        if (!monitorPointIdList.isEmpty()) {
            monitorPointList = monitorPointService.selectListByFilter(SearchFilter.instance().match("ID", monitorPointIdList).filter(MatchPattern.OR)).stream()
                    .filter(e -> !ObjectUtils.isEmpty(e.getIndexId())).collect(Collectors.toList());
        }


        String monitorTemplId = schemeList.get(0).getMonitorTmplId();
        EmcMonitorTmplBean emplBean = monitorTmplService.selectById(monitorTemplId);
        Long cycle= EmcConstant.ONE;
        Long frequency= EmcConstant.ONE;
        if(!ObjectUtils.isEmpty(emplBean)){
            cycle = ObjectUtils.isEmpty(emplBean.getCycle()) ? EmcConstant.ONE : emplBean.getCycle();
            frequency = ObjectUtils.isEmpty(emplBean.getFrequency()) ? EmcConstant.ONE : emplBean.getFrequency();
        }

        List<EmcProjOrderBean> insertOrderList = new ArrayList<>();
        for (int i = 0; i < qty; i++) {
            EmcProjSchemeBean scheme = schemeList.get(i);

            Long monitorPointId = scheme.getMonitorPointId();

            Long id = ApplicationContextHelper.getNextIdentity();
            scheme.setId(id);
            scheme.setFolderNo(folderNoList.get(i));
            scheme.setCycle(cycle);
            scheme.setFrequency(frequency);
            //如果采样科室为空则赋值任务表的默认采样科室
            if(StringUtils.isEmpty(scheme.getSampleOrgId())) {
            	scheme.setSampleOrgId(emcProject.getProjectSampleOrgId());
            }


            List<EmcProjOrderBean> copyOrderList = ObjectUtils.clone(orderList);
            for (EmcProjOrderBean order : copyOrderList) {
                order.setId(ApplicationContextHelper.getNextIdentity());
                order.setProjSchemeId(id);
                order.setSampleNum(EmcConstant.ONE);
                if (monitorPointId != null) {
                    EmcMonitorPointBean monitorPoint = monitorPointList.stream().filter(e -> ObjectUtils.equals(monitorPointId, e.getId())).findFirst().orElse(null);
                    if (monitorPoint != null) {
                        order.setIndexCategoryId(NumberUtils.parseLong(monitorPoint.getExt$Item("indexcategoryid")));
                        order.setIndexId(monitorPoint.getIndexId());
                    }
                }
            }
            insertOrderList.addAll(copyOrderList);
        }

        getDao().insert(schemeList);
        projOrderService.getDao().insert(insertOrderList);
        return schemeList.get(0).getId();
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    @ValidateDataWith(EmcInitSchemeValidator.class)
    public void initInsert(RestJsonWrapperBean wrapper) {
        List<EmcProjSchemeBean> schemeList = wrapper.parse(EmcProjSchemeBean.class);
        // 先删除项目已存在的监测方案
        Long projId = schemeList.get(0).getProjId();
        List<EmcProjSchemeBean> deleteSchemeList = getDao().selectListByOneColumnValue(projId, "PROJID");
        if (!deleteSchemeList.isEmpty()) {
            List<Long> projSchemeIdList = deleteSchemeList.stream().map(e -> e.getId()).collect(Collectors.toList());
            List<EmcProjOrderBean> deleteOrderList = projOrderService.getDao().selectListByOneColumnValues(projSchemeIdList, "PROJSCHEMEID");
            getDao().deleteBy(deleteSchemeList);
            projOrderService.getDao().deleteBy(deleteOrderList);
        }

        // 初始化新增方案
        List<Long> monitorPointIdList = schemeList.stream().map(e -> e.getMonitorPointId()).collect(Collectors.toList());
        List<EmcMonitorPointSchemeBean> pointSchemeList = monitorPointSchemeService.selectListByFilter(SearchFilter.instance().match("MONITORPOINTID", monitorPointIdList).filter(MatchPattern.OR));

        int qty = schemeList.size();
        List<String> folderNoList = generateFolderNosByProjId(projId, projectService.selectDetail(projId).getProjNo(), qty);

        //首先获取业务类型中维护的默认采样科室，如果为空则是本单位下第一个采样科室
        EmcProjectBean emcProject = projectService.selectById(projId);
        Long bizCategoryId = emcProject.getBizCategoryId();
        EmcBizCategoryBean bizCategory= emcBizCategoryService.selectById(bizCategoryId);
        if(StringUtils.isEmpty(bizCategory.getSampleOrgId())){
        	 CoreOrgBean filterOrg = new CoreOrgBean();
             filterOrg.setExt$Item("SAMPLEDEPARTMENT", "1");
             filterOrg.setParentId(LocalContextHelper.getLoginUser().getExt$Item("parentorgid"));
             List<CoreOrgBean> sampleOrgList = orgService.getDao().selectList(filterOrg);
             if (!sampleOrgList.isEmpty()) {
                 schemeList.forEach(scheme -> scheme.setSampleOrgId(sampleOrgList.get(0).getId()));
             }
        }else {
        	schemeList.forEach(scheme -> scheme.setSampleOrgId(bizCategory.getSampleOrgId()));
        }



        List<EmcProjOrderBean> insertOrderList = new ArrayList<>();
        for (int i = 0; i < qty; i++) {
            EmcProjSchemeBean scheme = schemeList.get(i);

            Long id = ApplicationContextHelper.getNextIdentity();
            scheme.setId(id);
            scheme.setFolderNo(folderNoList.get(i));
            scheme.setCycle(ObjectUtils.isEmpty(scheme.getCycle()) ? EmcConstant.ONE : scheme.getCycle());
            scheme.setFrequency(ObjectUtils.isEmpty(scheme.getFrequency()) ? EmcConstant.ONE : scheme.getFrequency());

            List<EmcMonitorPointSchemeBean> filterPointSchemeList = pointSchemeList.stream().filter(f -> ObjectUtils.equals(scheme.getMonitorPointId(), f.getMonitorPointId()))
                    .collect(Collectors.toList());

            String monitorTmplId = filterPointSchemeList.isEmpty() ? null : filterPointSchemeList.get(0).getMonitorTmplId();
            scheme.setMonitorTmplId(monitorTmplId);
            for (EmcMonitorPointSchemeBean pointScheme : filterPointSchemeList) {
                EmcProjOrderBean order = new EmcProjOrderBean();
                order.setId(ApplicationContextHelper.getNextIdentity());
                order.setProjSchemeId(id);
                order.setSampleNum(ObjectUtils.isEmpty(pointScheme.getSampleNum()) ? EmcConstant.ONE : pointScheme.getSampleNum());

                order.setTestId(NumberUtils.parseLong(pointScheme.getExt$Item("testid")));
                order.setSchemeName(pointScheme.getSchemeName());
                order.setMethodId(pointScheme.getMethodId());
                order.setItemGroupId(pointScheme.getItemGroupId());
                order.setItemGroup(pointScheme.getItemGroup());
                order.setIndexId(scheme.getExt$Item("indexid"));
                order.setIndexCategoryId(NumberUtils.parseLong(scheme.getExt$Item("indexcategoryid")));
                insertOrderList.add(order);
            }
        }

        getDao().insert(schemeList);
        projOrderService.getDao().insert(insertOrderList);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void copy(RestJsonWrapperBean wrapper) {
        String copies = wrapper.getParamValue("copies");// 复制份数
        List<EmcProjSchemeBean> copySchemeList = wrapper.parse(EmcProjSchemeBean.class);
        List<Long> projSchemeIdList = copySchemeList.stream().map(e -> e.getId()).collect(Collectors.toList());
        List<EmcProjOrderBean> copyOrderList = projOrderService.getDao().selectListByOneColumnValues(projSchemeIdList, "PROJSCHEMEID");

        int qty = copySchemeList.size() * NumberUtils.parseInt(copies);
        Long projId = copySchemeList.get(0).getProjId();
        List<String> folderNoList = generateFolderNosByProjId(projId, projectService.selectDetail(projId).getProjNo(), qty);

        int index = 0;// 下标

        List<EmcProjSchemeBean> insertSchemeList = new ArrayList<>();
        List<EmcProjOrderBean> insertOrderList = new ArrayList<>();
        for (int i = 0; i < NumberUtils.parseInt(copies); i++) {
            List<EmcProjSchemeBean> projSchemeList = ObjectUtils.clone(copySchemeList);
            List<EmcProjOrderBean> projOrderList = ObjectUtils.clone(copyOrderList);
            for (EmcProjSchemeBean projScheme : projSchemeList) {
                Long projSchemeId = ApplicationContextHelper.getNextIdentity();
                if (!projOrderList.isEmpty()) {
                    projOrderList.stream().forEach(projOrder -> {
                        if (ObjectUtils.equals(projScheme.getId(), projOrder.getProjSchemeId())) {
                            projOrder.setId(ApplicationContextHelper.getNextIdentity());
                            projOrder.setProjSchemeId(projSchemeId);
                        }
                    });
                }
                projScheme.setId(projSchemeId);
                projScheme.setFolderNo(folderNoList.get(index));
                index++;
            }

            insertSchemeList.addAll(projSchemeList);
            insertOrderList.addAll(projOrderList);
        }

        getDao().insert(insertSchemeList);
        projOrderService.getDao().insert(insertOrderList);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void copyPoint(RestJsonWrapperBean wrapper) {
        List<EmcProjSchemeBean> copySchemeList = wrapper.parse(EmcProjSchemeBean.class, 0);
        List<EmcProjSchemeBean> paramSchemeList = wrapper.parse(EmcProjSchemeBean.class, 1);
        EmcProjSchemeBean copyScheme = copySchemeList.get(0);

        EmcProjOrderBean orderFilter = new EmcProjOrderBean();
        orderFilter.setProjSchemeId(copyScheme.getId());
        List<EmcProjOrderBean> copyOrderList = projOrderService.getDao().selectList(orderFilter);

        int qty = paramSchemeList.size();
        Long projId = copyScheme.getProjId();
        List<String> folderNoList = generateFolderNosByProjId(projId, projectService.selectDetail(projId).getProjNo(), qty);

        int index = 0;// 下标

        List<EmcProjSchemeBean> insertSchemeList = new ArrayList<>();
        List<EmcProjOrderBean> insertOrderList = new ArrayList<>();
        for (EmcProjSchemeBean paramScheme : paramSchemeList) {
            EmcProjSchemeBean scheme = new EmcProjSchemeBean();
            BeanUtils.copyProperties(copyScheme, scheme);
            List<EmcProjOrderBean> orderList = ObjectUtils.clone(copyOrderList);

            Long projSchemeId = ApplicationContextHelper.getNextIdentity();
            orderList.stream().forEach(e -> {
                e.setId(ApplicationContextHelper.getNextIdentity());
                e.setProjSchemeId(projSchemeId);
            });
            scheme.setId(projSchemeId);
            scheme.setMonitorPointCategoryId(paramScheme.getMonitorPointCategoryId());
            scheme.setMonitorPointCategoryName(paramScheme.getMonitorPointCategoryName());
            scheme.setMonitorPointId(paramScheme.getMonitorPointId());
            scheme.setMonitorPointNo(paramScheme.getMonitorPointNo());
            scheme.setMonitorPointName(paramScheme.getMonitorPointName());
            scheme.setFolderNo(folderNoList.get(index));
            index++;

            insertSchemeList.add(scheme);
            insertOrderList.addAll(orderList);
        }
        getDao().insert(insertSchemeList);
        projOrderService.getDao().insert(insertOrderList);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void updateFrequency(RestJsonWrapperBean wrapper) {
        String cycleString = wrapper.getParamValue("cycle"); // 周期
        String frequencyString = wrapper.getParamValue("frequency"); // 频次

        Long cycle = StringUtils.isEmpty(cycleString) ? null : NumberUtils.parseLong(wrapper.getParamValue("cycle")); // 周期
        Long frequency = StringUtils.isEmpty(frequencyString) ? null : NumberUtils.parseLong(wrapper.getParamValue("frequency")); // 频次

        List<EmcProjSchemeBean> schemeList = wrapper.parse(EmcProjSchemeBean.class);
        schemeList.forEach(s -> {
            s.setCycle(cycle);
            s.setFrequency(frequency);
        });

        getDao().update(schemeList, "CYCLE", "FREQUENCY");
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

        // 需要新增的测试
        List<Long> originalItemGroupIdList = originalOrderList.stream().map(EmcProjOrderBean::getItemGroupId).collect(Collectors.toList());
        List<EmcProjOrderBean> newOrderList = orderList.stream().filter(o -> !originalItemGroupIdList.contains(o.getItemGroupId())).collect(Collectors.toList());

        projOrderService.getDao().deleteBy(deleteOrderList);
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
        projOrderService.getDao().insert(insertOrderList);
    }

    @Override
    public Page<EmcProjOrderBean> selectProjOrderPagationByProjScheme(Long id, RestJsonWrapperBean wrapper) {
        return projOrderService.selectPaginationByFilter(SearchFilter.instance().match("PROJSCHEMEID", id).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    public Page<EmcProjOrderBean> selectOrderPagation(RestJsonWrapperBean wrapper) {
        return projOrderService.selectPagination(wrapper);
    }

    @Override
    public Page<EmcProjDTO> selectByProjId(Long id) {
        return projOrderService.selectByProjId(id);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void insertBpData(List<Long> projectIdList) {
        List<EmcProjSchemeBean> schemeList = this.selectListByFilter(SearchFilter.instance().match("PROJID", projectIdList).filter(MatchPattern.OR));
        if (!schemeList.isEmpty()) {
            // 监测方案样品
            List<Long> schemeIdList = schemeList.stream().map(e -> e.getId()).collect(Collectors.toList());
            List<EmcProjOrderBean> orderList = projOrderService.selectListByFilter(SearchFilter.instance().match("PROJSCHEMEID", schemeIdList).filter(MatchPattern.OR));
            // 分析项组分析项
            List<Long> itemGroupIdList = orderList.stream().map(e -> e.getItemGroupId()).collect(Collectors.toList());
            List<EmcGroupItemBean> itemList = groupItemService.selectListByFilter(SearchFilter.instance().match("ITEMGROUPID", itemGroupIdList).filter(MatchPattern.OR));
            // 监测点
            List<Long> monitorPointIdList = schemeList.stream().map(e -> e.getMonitorPointId()).filter(e -> !ObjectUtils.isEmpty(e)).collect(Collectors.toList());
            List<EmcMonitorPointBean> pointList = monitorPointService.selectListByIds(monitorPointIdList);
            // 测试方法
            List<Long> testIdList = orderList.stream().map(e -> e.getTestId()).collect(Collectors.toList());
            List<EmcTestMethodBean> testMethodList = testMethodService.selectListByFilter(SearchFilter.instance().match("TESTID", testIdList).filter(MatchPattern.OR));
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

            for (EmcProjSchemeBean projScheme : schemeList) {
                List<EmcProjOrderBean> projOrderList = orderList.stream().filter(order -> ObjectUtils.equals(order.getProjSchemeId(), projScheme.getId())).collect(Collectors.toList());
                this.generateBpData(projScheme, projOrderList, itemList, pointList, testMethodList, testOrderList, indexSetList, indexTestItemList, insertFolderList, insertOrderList,
                        insertOrderTaskList, insertOrderTaskResultList, insertOrderItemGroupList);
            }

            folderService.getDao().insert(insertFolderList);
            orderService.getDao().insert(insertOrderList);
            orderTaskService.getDao().insert(insertOrderTaskList);
            orderTaskResultService.getDao().insert(insertOrderTaskResultList);
            orderItemGroupService.getDao().insert(insertOrderItemGroupList);
        }
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void editSubcontractor(RestJsonWrapperBean wrapper) {
        projOrderService.editSubcontractor(wrapper);
    }

    @Override
    public void updateOrderIndex(RestJsonWrapperBean wrapper) {
        projOrderService.updateOrderIndex(wrapper);
    }

    @Override
    public List<String> generateFolderNosByProjId(Long projId, String projectNo, int num) {

        List<EmcProjSchemeBean> schemeOfProjectList = getDao().selectListByOneColumnValue(projId, "PROJID");
        List<String> newFolderNoList = new ArrayList<>();
        String firstFolderNo = "";
        if (schemeOfProjectList.isEmpty()) {
            firstFolderNo = projectNo + "001";
            newFolderNoList.add(firstFolderNo);

        } else {

            String maxFolderNo = schemeOfProjectList.stream().max(Comparator.comparing(EmcProjSchemeBean::getFolderNo)).get().getFolderNo();
            firstFolderNo = maxFolderNo.substring(0, maxFolderNo.length() - 3) + String.format("%03d", Long.parseLong(maxFolderNo.substring(maxFolderNo.length() - 3)) + 1);
            newFolderNoList.add(firstFolderNo);
        }

        if (num > 1) {
            for (int i = 0; i < num - 1; i++) {
                String beforeFolderNo = newFolderNoList.get(i);
                String nextFolderNo = beforeFolderNo.substring(0, beforeFolderNo.length() - 3) + String.format("%03d", Long.parseLong(beforeFolderNo.substring(beforeFolderNo.length() - 3)) + 1);
                newFolderNoList.add(nextFolderNo);
            }
        }

        return newFolderNoList;
    }

    // ---------------------------------------------------------------------
    // 私有方法
    // ---------------------------------------------------------------------
    /**
     * 通过监测方案和监测方案样品生成主业务流程数据
     */
    private void generateBpData(EmcProjSchemeBean projScheme, List<EmcProjOrderBean> projOrderList, List<EmcGroupItemBean> itemList, List<EmcMonitorPointBean> pointList,
            List<EmcTestMethodBean> testMethodList, List<EmcTestMethodOrderCategoryBean> testOrderList, List<EmcIndexSetBean> indexSetList, List<EmcIndexTestItemBean> indexTestItemList,
            List<EmcFolderBean> insertFolderList, List<EmcOrderBean> insertOrderList, List<EmcOrderTaskBean> insertOrderTaskList, List<EmcOrderTaskResultBean> insertOrderTaskResultList,
            List<EmcOrderItemGroupBean> insertOrderItemGroupList) {
        // 生成点位
        EmcFolderBean folder = this.generateFolder(projScheme, pointList);
        insertFolderList.add(folder);
        // 生成样品
        List<EmcOrderBean> orderList = this.generateOrderList(folder, projScheme, projOrderList);
        insertOrderList.addAll(orderList);
        // 生成样品测试和生成样品结果
        List<EmcOrderTaskResultBean> orderTaskResultList = new ArrayList<>();
        List<EmcOrderTaskBean> orderTaskList = this.generateOrderTaskList(projScheme, orderList, projOrderList, itemList, testMethodList, testOrderList, orderTaskResultList, indexSetList,
                indexTestItemList);
        insertOrderTaskList.addAll(orderTaskList);
        insertOrderTaskResultList.addAll(orderTaskResultList);
        // 生成样品分析项组
        List<EmcOrderItemGroupBean> orderItemGroupList = this.generateOrderItemGroup(orderList, projOrderList, orderTaskList);
        insertOrderItemGroupList.addAll(orderItemGroupList);
    }

    /**
     * 生成样品分析项组数据
     */
    protected List<EmcOrderItemGroupBean> generateOrderItemGroup(List<EmcOrderBean> orderList, List<EmcProjOrderBean> projOrderList, List<EmcOrderTaskBean> orderTaskList) {
        List<EmcOrderItemGroupBean> list = new ArrayList<>();
        for (EmcOrderBean order : orderList) {
            for (EmcProjOrderBean projOrder : projOrderList) {
                Long orderId = order.getId();
                Long testMethodId = NumberUtils.parseLong(projOrder.getExt$Item("testmethodid"));
                EmcOrderTaskBean orderTask = orderTaskList.stream().filter(f -> ObjectUtils.equals(orderId, f.getOrderId()) && ObjectUtils.equals(testMethodId, f.getTestMethodId())).findFirst()
                        .orElse(null);

                EmcOrderItemGroupBean orderItemGroup = new EmcOrderItemGroupBean();
                orderItemGroup.setId(ApplicationContextHelper.getNextIdentity());
                orderItemGroup.setOrderId(orderId);
                orderItemGroup.setSchemeName(projOrder.getSchemeName());
                orderItemGroup.setTestId(projOrder.getTestId());
                orderItemGroup.setTestName(projOrder.getExt$Item("testname"));
                orderItemGroup.setTestMethodId(testMethodId);
                orderItemGroup.setMethodId(projOrder.getMethodId());
                orderItemGroup.setMethodName(projOrder.getExt$Item("methodname"));
                orderItemGroup.setMethodVersionId(NumberUtils.parseLong(projOrder.getExt$Item("methodversionid")));
                orderItemGroup.setTestMethodBasis(projOrder.getExt$Item("testmethodbasis"));
                orderItemGroup.setItemGroupId(projOrder.getItemGroupId());
                orderItemGroup.setItemGroup(projOrder.getItemGroup());
                orderItemGroup.setQcCategoryId(order.getQcCategoryId());
                orderItemGroup.setQcCategoryCode(order.getQcCategoryCode());
                orderItemGroup.setQcName(order.getQcName());
                if (orderTask != null) {
                    orderItemGroup.setAnalysisOrgId(orderTask.getAnalysisOrgId());
                    orderItemGroup.setAnalysisOrgName(orderTask.getAnalysisOrgName());
                    orderItemGroup.setContainerCategoryId(orderTask.getContainerCategoryId());
                    orderItemGroup.setContainerCategoryCode(orderTask.getContainerCategoryCode());
                    orderItemGroup.setContainerCategoryName(orderTask.getContainerCategoryName());
                    orderItemGroup.setContainerGroup(orderTask.getContainerGroup());
                    orderItemGroup.setFixative(orderTask.getFixative());
                    orderItemGroup.setInputUserId(orderTask.getInputUserId());
                    orderItemGroup.setInputUserName(orderTask.getInputUserName());
                }
                orderItemGroup.setSubcontractorId(projOrder.getSubcontractorId());
                orderItemGroup.setSubPackageNo(projOrder.getSubPackageNo());
                orderItemGroup.setDataSharing(projOrder.getDataSharing());
                orderItemGroup.setMonitorTmplId(order.getMonitorTmplId());
                orderItemGroup.setOrderCategory(order.getOrderCategory());
                list.add(orderItemGroup);
            }
        }
        return list;
    }

    /**
     * 生成样品测试数据
     */
    protected List<EmcOrderTaskBean> generateOrderTaskList(EmcProjSchemeBean projScheme, List<EmcOrderBean> orderList, List<EmcProjOrderBean> projOrderList, List<EmcGroupItemBean> itemList,
            List<EmcTestMethodBean> testMethodList, List<EmcTestMethodOrderCategoryBean> testOrderList, List<EmcOrderTaskResultBean> orderTaskResultList, List<EmcIndexSetBean> indexSetList,
            List<EmcIndexTestItemBean> indexTestItemList) {

        // 方案样品按测试和方法分组
        List<EmcProjOrderBean> groupProjOrderList = projOrderList.stream().collect(
                Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(o -> o.getTestId() + "#" + o.getMethodId()))), ArrayList<EmcProjOrderBean>::new));

        List<EmcOrderTaskBean> list = new ArrayList<>();
        for (EmcOrderBean order : orderList) {
            for (EmcProjOrderBean projOrder : groupProjOrderList) {
                List<EmcGroupItemBean> filterItemList = this.filterItem(projOrder, projOrderList, itemList);

                // 查询该样品测试所维护的指标设置的指标项目
                List<EmcIndexSetBean> indexSetOfGroupList = null;
                List<EmcIndexTestItemBean> indexTestItemOfGroupList = null;
                if (!indexTestItemList.isEmpty() && !indexSetList.isEmpty()) {
                    indexSetOfGroupList = indexSetList.stream().filter(e -> ObjectUtils.equals(e.getIndexId(), projOrder.getIndexId())).collect(Collectors.toList());
                    List<Long> indexItemIdOfGroupList = indexSetOfGroupList.stream().map(e -> e.getIndexItemId()).distinct().collect(Collectors.toList());
                    indexTestItemOfGroupList = indexTestItemList.stream().filter(e -> indexItemIdOfGroupList.contains(e.getIndexItemId())).collect(Collectors.toList());
                }

                // 取当前监测方案样品的分析类型List
                List<EmcGroupItemBean> analysisCategoryList = getDistinctItemList(filterItemList);

                for (EmcGroupItemBean analysisItem : analysisCategoryList) {
                    EmcTestMethodBean testMethod = testMethodList.stream().filter(f -> ObjectUtils.equals(f.getId(), NumberUtils.parseLong(analysisItem.getExt$Item("testmethodid")))).findFirst()
                            .orElse(null);
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
                        orderTask.setEquiptAcquNo(order.getLabelNo());
                        orderTask.setIndexId(projOrder.getIndexId());
                        orderTask.setIndexCategoryId(projOrder.getIndexCategoryId());
                        orderTask.setSubcontractorId(projOrder.getSubcontractorId());
                        orderTask.setSubPackageNo(projOrder.getSubPackageNo());
                        orderTask.setDataSharing(projOrder.getDataSharing());
                        // 如果样品测试的分析类型为采样，样品测试的分析科室为监测方案的采样科室
                        String analysisCategory = analysisItem.getExt$Item("analysiscategory");
                        if (EmcBizConstant.ANALYSISCATEGORY_SAMPLING.equals(analysisCategory)) {
                            orderTask.setAnalysisOrgId(projScheme.getSampleOrgId());
                            orderTask.setAnalysisOrgName(projScheme.getExt$Item("orgname"));
                        } else {
                            if (testMethod != null) {
                                orderTask.setAnalysisOrgId(testMethod.getAnalysisOrgId());
                                orderTask.setAnalysisOrgName(testMethod.getExt$Item("orgname"));
                            }
                        }

                        if (testMethod != null && !StringUtils.isEmpty(testMethod.getInputUserId())) {
                            orderTask.setInputUserId(testMethod.getInputUserId());
                            orderTask.setInputUserName(testMethod.getInputUserName());
                        }
                        if (testOrder != null) {
                            orderTask.setContainerCategoryId(testOrder.getContainerCategoryId());
                            orderTask.setContainerCategoryCode(testOrder.getExt$Item("containercategorycode"));
                            orderTask.setContainerCategoryName(testOrder.getExt$Item("containercategoryname"));
                            orderTask.setContainerGroup(testOrder.getContainerGroup());
                            orderTask.setFixative(testOrder.getFixative());
                            orderTask.setStoreCondition(testOrder.getStoreCondition());
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

    /**
     * 根据方案样品过滤重复的分析项
     */
    protected List<EmcGroupItemBean> filterItem(EmcProjOrderBean projOrder, List<EmcProjOrderBean> projOrderList, List<EmcGroupItemBean> itemList) {
        List<EmcProjOrderBean> filterProjOrderList = projOrderList.stream()
                .filter(f -> ObjectUtils.equals(projOrder.getTestId(), f.getTestId()) && ObjectUtils.equals(projOrder.getMethodId(), f.getMethodId())).collect(Collectors.toList());
        List<Long> itemGroupIdList = filterProjOrderList.stream().map(e -> e.getItemGroupId()).collect(Collectors.toList());

        List<EmcGroupItemBean> filterItemList = new ArrayList<>();
        for (EmcGroupItemBean groupItem : itemList) {
            if (itemGroupIdList.stream().anyMatch(b -> ObjectUtils.equals(b, groupItem.getItemGroupId()))) {
                filterItemList.add(groupItem);
            }
        }

        // 分析项组分析项按分析项分组
        List<EmcGroupItemBean> list = filterItemList.stream()
                .collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(o -> o.getItemId()))), ArrayList<EmcGroupItemBean>::new));
        return list;
    }

    /**
     * 生成样品结果数据
     * @param orderTask
     * @param itemList
     * @param order
     * @param indexSetList
     * @param indexTestItemList
     * @return
     */
    protected List<EmcOrderTaskResultBean> generateOrderTaskResult(EmcOrderTaskBean orderTask, List<EmcGroupItemBean> itemList, EmcOrderBean order, List<EmcIndexSetBean> indexSetList,
            List<EmcIndexTestItemBean> indexTestItemList) {
        List<EmcOrderTaskResultBean> list = new ArrayList<>();
        String analysisType = orderTask.getAnalysisType();
        List<EmcGroupItemBean> filterItemList = itemList.stream().filter(item -> analysisType.equals(item.getExt$Item("analysiscategory"))).collect(Collectors.toList());

        for (EmcGroupItemBean item : filterItemList) {
            String pnFunc = item.getExt$Item("pnfunc");
            // 如果测试分析项的PN函数为日均值，则只会出现在同一周期下的第一个样品上
            if (EmcConstant.PN_DAYAVERAGE.equals(pnFunc)) {
                if (!ObjectUtils.equals(EmcConstant.ONE, order.getSampleNum()) || !ObjectUtils.equals(EmcConstant.ONE, order.getFrequency())) {
                    continue;
                }
                // 如果测试分析项的PN函数为次均值，则只会出现在同一周期频次下的第一个样品上
            } else if (EmcConstant.PN_PERAVERAGE.equals(pnFunc)) {
                if (!ObjectUtils.equals(EmcConstant.ONE, order.getSampleNum())) {
                    continue;
                }
            }
            // 未启用的分析项跳过
            if ("0".equals(item.getExt$Item("activatedflag"))) {
                continue;
            }

            Long repeatItem = NumberUtils.parseLong(item.getExt$Item("repeatitem"));// 重复数
            for (int i = 0; i < repeatItem; i++) {
                EmcOrderTaskResultBean result = new EmcOrderTaskResultBean();
                result.setId(ApplicationContextHelper.getNextIdentity());
                result.setFolderId(orderTask.getFolderId());
                result.setOrderId(orderTask.getOrderId());
                result.setOrderNo(order.getOrderNo());
                result.setParentOrderNo(order.getParentOrderNo());
                result.setCycle(order.getCycle());
                result.setFrequency(order.getFrequency());
                result.setTestId(orderTask.getTestId());
                result.setProjId(orderTask.getProjId());
                result.setOrderTaskId(orderTask.getId());
                result.setTestMethodId(orderTask.getTestMethodId());
                result.setMethodId(orderTask.getMethodId());
                result.setMethodVersionId(orderTask.getMethodVersionId());
                result.setItemId(item.getItemId());
                result.setItemOrderNo(NumberUtils.parseLong(item.getExt$Item("orderno")));
                result.setItemName(item.getExt$Item("itemname"));
                result.setItemAliasName(item.getExt$Item("itemaliasname"));
                result.setScriptFlag(item.getExt$Item("scriptflag"));
                result.setResultCategory(item.getExt$Item("resultcategory"));
                result.setRevisionRule(item.getExt$Item("revisionrule"));
                result.setUnitName(item.getExt$Item("unitname"));
                result.setRepeatNo(i + 1l);
                result.setRepeatQty(NumberUtils.parseLong(repeatItem));
                result.setReportDisplay(item.getExt$Item("reportdisplay"));
                result.setAnalysisType(item.getExt$Item("analysiscategory"));
                result.setLowLimit(StringUtils.isEmpty(item.getExt$Item("lowlimit")) ? null : item.getExt$Item("lowlimit"));
                result.setHighLimit(StringUtils.isEmpty(item.getExt$Item("highlimit")) ? null : item.getExt$Item("highlimit"));

                // 添加指标高低限
                if (indexTestItemList != null && indexSetList != null) {
                    EmcIndexTestItemBean indexTestItem = indexTestItemList.stream().filter(e -> ObjectUtils.equals(item.getItemId(), e.getItemId())).findFirst().orElse(null);
                    if (indexTestItem != null) {
                        EmcIndexSetBean indexSet = indexSetList.stream().filter(e -> ObjectUtils.equals(indexTestItem.getIndexItemId(), e.getIndexItemId())).findFirst().orElse(null);
                        if (indexSet != null) {
                            result.setIndexLowLimit(indexSet.getIndexLowLimit());
                            result.setIndexHighLimit(indexSet.getIndexHighLimit());
                            result.setIndexLimitUnit(indexSet.getUnit());
                        }
                    }
                }
                list.add(result);
            }
        }
        return list;
    }

    /**
     * 生成样品数据
     */
    protected List<EmcOrderBean> generateOrderList(EmcFolderBean folder, EmcProjSchemeBean projScheme, List<EmcProjOrderBean> projOrderList) {
        List<EmcOrderBean> list = new ArrayList<>();
        Long cycle = projScheme.getCycle();// 周期
        Long frequency = projScheme.getFrequency();// 频次
        Long sampleNum = projOrderList.stream().mapToLong(EmcProjOrderBean::getSampleNum).max().getAsLong();// 最大采样数

        for (int i = 1; i <= cycle; i++) {
            for (int j = 1; j <= frequency; j++) {
                for (int k = 1; k <= sampleNum; k++) {
                    EmcOrderBean order = new EmcOrderBean();
                    order.setId(ApplicationContextHelper.getNextIdentity());
                    order.setOrderNo(folder.getFolderNo() + StringUtils.leftPad("" + (list.size() + 1), 3));// 生成样品编码
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
                }
            }
        }

        return list;
    }

    /**
     * 生成点位数据
     */
    protected EmcFolderBean generateFolder(EmcProjSchemeBean projScheme, List<EmcMonitorPointBean> pointList) {
        EmcMonitorPointBean point = pointList.stream().filter(f -> ObjectUtils.equals(f.getMonitorPointNo(), projScheme.getMonitorPointNo())).findFirst().orElse(null);
        EmcFolderBean folder = new EmcFolderBean();
        folder.setId(ApplicationContextHelper.getNextIdentity());
        folder.setFolderNo(projScheme.getFolderNo());
        folder.setProjId(projScheme.getProjId());
        folder.setSampleOrgId(projScheme.getSampleOrgId());
        folder.setSampleOrgName(projScheme.getExt$Item("orgname"));
        folder.setOrderCategory(projScheme.getExt$Item("monitortmplname"));
        folder.setSource(EmcBizConstant.BP_SOURCE_MONITORSCHEME);
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
        return folder;
    }

    /**
     * 根据分析类型去重
     */
    protected List<EmcGroupItemBean> getDistinctItemList(List<EmcGroupItemBean> itemList) {
        return new ArrayList<>(itemList.stream().collect(Collectors.groupingBy(v -> v.getExt$Item("analysiscategory"), Collectors.collectingAndThen(Collectors.toList(), v -> v.get(0)))).values());
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void relevanceUpdateSubcontractor(RestJsonWrapperBean wrapper) {
        projOrderService.relevanceUpdateSubcontractor(wrapper);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void relevanceDeleteSubcontractor(RestJsonWrapperBean wrapper) {
        projOrderService.relevanceDeleteSubcontractor(wrapper);

    }

}
