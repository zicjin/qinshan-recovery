package com.sunwayworld.emc.module.mdm.bizmdm.test.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcOrderItemGroupBean;
import com.sunwayworld.emc.module.bp.project.project.service.EmcOrderItemGroupService;
import com.sunwayworld.emc.module.bp.project.projscheme.bean.EmcProjOrderBean;
import com.sunwayworld.emc.module.bp.project.projscheme.service.EmcProjOrderService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcGroupItemBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcItemBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcItemGroupBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcItemQcIndexBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcItemResultBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcItemRevisionBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcTestBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcTestMethodBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcTestMethodLogBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcTestMethodOrderCategoryBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.dao.EmcTestMethodDao;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcGroupItemService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcItemGroupService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcItemQcIndexService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcItemResultService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcItemRevisionService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcItemService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcTestMethodLogService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcTestMethodOrderCategoryService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcTestMethodService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcTestService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.validator.EmcTestMethodCopyValidator;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptTestMethodBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.service.EmcEquiptTestMethodService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
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
import com.sunwayworld.module.sys.formula.bean.CoreFormulaRequiredBean;
import com.sunwayworld.module.sys.formula.service.CoreFormulaRequiredService;

/**
 * @author yuanhh@sunwayworld.com
 * @date 2019-09-19
 */

@Repository
@GikamBean
public class EmcTestMethodServiceImpl implements EmcTestMethodService {
    @Autowired
    private EmcItemService itemService;

    @Autowired
    private EmcItemGroupService itemGroupService;

    @Autowired
    private EmcGroupItemService groupItemService;

    @Autowired
    private EmcItemQcIndexService itemQcIndexService;

    @Autowired
    private EmcItemRevisionService itemRevisionService;

    @Autowired
    private EmcItemResultService itemResultService;

    @Autowired
    private EmcEquiptTestMethodService equiptTestMethodService;

    @Autowired
    private EmcTestMethodOrderCategoryService testMethodOrderCategoryService;

    @Autowired
    @Lazy
    private EmcOrderItemGroupService orderItemGroupService;

    @Autowired
    private EmcTestMethodLogService testMethodLogService;

    @Autowired
    @Lazy
    private EmcProjOrderService projOrderService;

    @Autowired
    private CoreFormulaRequiredService coreFormulaRequiredService;

    @Autowired
    private EmcTestService testService;

    @Autowired
    private EmcTestMethodDao testMethodDao;
    
    @Autowired
    private EmcItemQcIndexService emcItemQcindexService;

    @Override
    @SuppressWarnings("unchecked")
    public EmcTestMethodDao getDao() {
        return testMethodDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        EmcTestMethodBean emcTestMethod = jsonWrapper.parseUnique(EmcTestMethodBean.class);
        emcTestMethod.setId(ApplicationContextHelper.getNextIdentity());
        getDao().insert(emcTestMethod);
        return emcTestMethod.getId();
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    @ValidateDataWith(EmcTestMethodCopyValidator.class)
    public void copy(RestJsonWrapperBean jsonWrapper) {
        EmcTestMethodBean testMethod = jsonWrapper.parseUnique(EmcTestMethodBean.class);
        Long id = testMethod.getId();

        // 复制测试方法
        EmcTestMethodBean copyTestMethod = getDao().selectById(id);
        copyTestMethod.setId(ApplicationContextHelper.getNextIdentity());
        copyTestMethod.setTestId(testMethod.getTestId());
        copyTestMethod.setTestCategoryId(testMethod.getTestCategoryId());
        copyTestMethod.setMethodId(testMethod.getMethodId());
        getDao().insert(copyTestMethod);

        // 复制测试样品类型
        EmcTestMethodOrderCategoryBean orderCategoryFilter = new EmcTestMethodOrderCategoryBean();
        orderCategoryFilter.setTestMethodId(id);
        List<EmcTestMethodOrderCategoryBean> copyOrderCategoryList = testMethodOrderCategoryService.getDao().selectList(orderCategoryFilter);
        copyOrderCategoryList.stream().forEach(e -> {
            e.setId(ApplicationContextHelper.getNextIdentity());
            e.setTestMethodId(copyTestMethod.getId());
        });
        testMethodOrderCategoryService.getDao().insert(copyOrderCategoryList);

        // 复制测试关联仪器
        EmcEquiptTestMethodBean equiptFilter = new EmcEquiptTestMethodBean();
        equiptFilter.setTestMethodId(id);
        List<EmcEquiptTestMethodBean> copyequiptList = equiptTestMethodService.getDao().selectList(equiptFilter);
        copyequiptList.stream().forEach(e -> {
            e.setId(ApplicationContextHelper.getNextIdentity());
            e.setTestMethodId(copyTestMethod.getId());
        });
        equiptTestMethodService.getDao().insert(copyequiptList);

        // 复制测试方法分析项
        EmcItemBean itemFilter = new EmcItemBean();
        itemFilter.setTestMethodId(id);
        List<EmcItemBean> copyItemList = itemService.getDao().selectList(itemFilter);
        List<Long> copyItemIdList = copyItemList.stream().map(e -> e.getId()).collect(Collectors.toList());
        // 复制分析项--子表
        List<EmcItemQcIndexBean> copyQcIndexList = itemQcIndexService.getDao().selectListByOneColumnValues(copyItemIdList, "ITEMID");// QC指标
        List<EmcItemRevisionBean> copyRevisionList = itemRevisionService.getDao().selectListByOneColumnValues(copyItemIdList, "ITEMID");// 分段修约
        List<EmcItemResultBean> copyResultList = itemResultService.getDao().selectListByOneColumnValues(copyItemIdList, "ITEMID");// 分析项结果
        List<EmcGroupItemBean> copyGroupItemList = groupItemService.getDao().selectListByOneColumnValues(copyItemIdList, "ITEMID");// 分析项组分析项

        this.copyItemChildTable(copyTestMethod.getId(), copyItemList, copyQcIndexList, copyRevisionList, copyResultList, copyGroupItemList);

        // 复制测试分析项组
        List<EmcGroupItemBean> groupItemList = new ArrayList<>();
        EmcItemGroupBean itemGroupFilter = new EmcItemGroupBean();
        itemGroupFilter.setTestMethodId(id);
        List<EmcItemGroupBean> copyitemGroupList = itemGroupService.getDao().selectList(itemGroupFilter);
        copyitemGroupList.stream().forEach(e -> {
            Long itemGroupId = ApplicationContextHelper.getNextIdentity();
            // 去掉copyGroupItemList中,不在分析项组中存在的分析项
            if (!copyGroupItemList.isEmpty()) {
                copyGroupItemList.stream().forEach(groupItem -> {
                    if (ObjectUtils.equals(e.getId(), groupItem.getItemGroupId())) {
                        groupItem.setId(ApplicationContextHelper.getNextIdentity());
                        groupItem.setItemGroupId(itemGroupId);
                        groupItemList.add(groupItem);
                    }
                });
            }

            e.setId(itemGroupId);
            e.setTestMethodId(copyTestMethod.getId());
            e.setTestId(testMethod.getTestId());
        });

        groupItemService.getDao().insert(groupItemList);
        itemGroupService.getDao().insert(copyitemGroupList);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void move(RestJsonWrapperBean jsonWrapper) {
        EmcTestMethodBean testMethod = jsonWrapper.parseUnique(EmcTestMethodBean.class);
        EmcTestMethodBean moveTestMethod = getDao().selectById(testMethod.getId());
        moveTestMethod.setTestCategoryId(testMethod.getTestCategoryId());
        getDao().update(moveTestMethod, "TESTCATEGORYID");
    }

    /**
     * 复制测试分析项以及其子表
     *
     * @param testMethodId
     * @param itemList
     * @param qcIndexList
     * @param revisionList
     * @param resultList
     * @param groupItemList
     */
    private void copyItemChildTable(Long testMethodId, List<EmcItemBean> itemList, List<EmcItemQcIndexBean> qcIndexList, List<EmcItemRevisionBean> revisionList, List<EmcItemResultBean> resultList,
            List<EmcGroupItemBean> groupItemList) {

        List<Long> itemIdList = itemList.stream().map(EmcItemBean::getId).collect(Collectors.toList());
        Map<String, String> itemIdMap = new HashMap<>(itemIdList.size());
        List<CoreFormulaRequiredBean> requiredList = coreFormulaRequiredService.getDao().selectListByOneColumnValues(itemIdList, "ITEMID");

        itemList.stream().forEach(e -> {
            Long itemId = ApplicationContextHelper.getNextIdentity();
            itemIdMap.put(e.getId() + "", itemId + "");

            if (!qcIndexList.isEmpty()) {
                qcIndexList.stream().forEach(qcIndex -> {
                    if (ObjectUtils.equals(e.getId(), qcIndex.getItemId())) {
                        qcIndex.setId(ApplicationContextHelper.getNextIdentity());
                        qcIndex.setItemId(itemId);
                    }
                });
            }

            if (!revisionList.isEmpty()) {
                revisionList.stream().forEach(revision -> {
                    if (ObjectUtils.equals(e.getId(), revision.getItemId())) {
                        revision.setId(ApplicationContextHelper.getNextIdentity());
                        revision.setItemId(itemId);
                    }
                });
            }

            if (!resultList.isEmpty()) {
                resultList.stream().forEach(result -> {
                    if (ObjectUtils.equals(e.getId(), result.getItemId())) {
                        result.setId(ApplicationContextHelper.getNextIdentity());
                        result.setItemId(itemId);
                    }
                });
            }

            if (!groupItemList.isEmpty()) {
                groupItemList.stream().forEach(groupItem -> {
                    if (ObjectUtils.equals(e.getId(), groupItem.getItemId())) {
                        groupItem.setItemId(itemId);
                    }
                });
            }

            e.setId(itemId);
            e.setTestMethodId(testMethodId);
        });

        // 复制分析项公式依赖
        if (!requiredList.isEmpty()) {
            List<CoreFormulaRequiredBean> newRequiredList = new ArrayList<>();
            requiredList.forEach(r -> {
                String newItemId = itemIdMap.get(r.getItemId());
                if (!StringUtils.isEmpty(newItemId)) {
                    CoreFormulaRequiredBean newRequired = new CoreFormulaRequiredBean();
                    BeanUtils.copyProperties(r, newRequired);
                    Long id = ApplicationContextHelper.getNextIdentity();
                    newRequired.setId(id);
                    newRequired.setItemId(newItemId);
                    String newRequiredItemId = itemIdMap.get(r.getRequiredItemId());
                    if (!StringUtils.isEmpty(newRequiredItemId)) {
                        newRequired.setRequiredItemId(newRequiredItemId);
                    }
                    newRequiredList.add(newRequired);
                }
            });

            coreFormulaRequiredService.getDao().insert(newRequiredList);
        }

        itemService.getDao().insert(itemList);
        itemQcIndexService.getDao().insert(qcIndexList);
        itemRevisionService.getDao().insert(revisionList);
        itemResultService.getDao().insert(resultList);
    }

    @Override
    public Page<EmcTestBean> selectTestRawPagination(RestJsonWrapperBean wrapper) {
        return testService.selectRawPagination(wrapper);
    }

    @Override
    public Long insertTest(RestJsonWrapperBean wrapper) {
        return testService.insert(wrapper);
    }

    @Override
    @AuditTrailEntry(AuditTrailType.DELETE)
    public void deleteTest(RestJsonWrapperBean wrapper) {
        testService.delete(wrapper);
    }

    @Override
    public Page<EmcTestBean> selectTestChoosablePagination(RestJsonWrapperBean wrapper) {
        return testService.selectChoosablePagination(wrapper);
    }

    @Override
    public Page<EmcGroupItemBean> selectGroupItemChoosablePagination(RestJsonWrapperBean wrapper) {
        return groupItemService.selectChoosablePagination(wrapper);
    }

    @Override
    public List<EmcTestBean> selectTestSelectableList() {
        return testService.selectSelectableList();
    }

    @Override
    public List<EmcItemBean> selectTestItemSelectableList() {
        return itemService.selectSelectableList();
    }

    @Override
    public List<Long> testCheckUsed(RestJsonWrapperBean wrapper) {
        return testService.checkUsed(wrapper);
    }

    @Override
    public List<Long> checkUsed(RestJsonWrapperBean wrapper) {
        List<EmcTestMethodBean> testMethodList = wrapper.parse(EmcTestMethodBean.class);

        List<EmcOrderItemGroupBean> filterOrderItemGroupList = new ArrayList<>();
        List<EmcProjOrderBean> filterProjOrderList = new ArrayList<>();
        testMethodList.stream().forEach(e -> {
            EmcOrderItemGroupBean filterOrderItemGroup = new EmcOrderItemGroupBean();
            filterOrderItemGroup.setTestId(e.getTestId());
            filterOrderItemGroup.setMethodId(e.getMethodId());
            filterOrderItemGroupList.add(filterOrderItemGroup);

            EmcProjOrderBean filterProjOrder = new EmcProjOrderBean();
            filterProjOrder.setTestId(e.getTestId());
            filterProjOrder.setMethodId(e.getMethodId());
            filterProjOrderList.add(filterProjOrder);
        });

        List<EmcOrderItemGroupBean> orderItemGroupList = orderItemGroupService.getDao().selectList(filterOrderItemGroupList, Arrays.asList("TESTID", "METHODID"), Collections.emptyList());
        if (orderItemGroupList.isEmpty()) {
            List<EmcProjOrderBean> projOrderList = projOrderService.getDao().selectList(filterProjOrderList, Arrays.asList("TESTID", "METHODID"), Collections.emptyList());
            return projOrderList.stream().map(p -> p.getId()).collect(Collectors.toList());
        }

        return orderItemGroupList.stream().map(g -> g.getId()).collect(Collectors.toList());
    }

    @Override
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void deactivate(RestJsonWrapperBean wrapper) {
        EmcTestMethodService.super.deactivate(wrapper);
        List<EmcTestMethodBean> testMethodList = wrapper.parse(EmcTestMethodBean.class);
        getDao().update(testMethodList, "REMARK");
    }

    @Override
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void geneItemGroup(RestJsonWrapperBean wrapper) {
        List<EmcTestMethodBean> testMethodList = wrapper.parse(EmcTestMethodBean.class);

        List<EmcItemGroupBean> insertGroupList = new ArrayList<>();
        List<EmcGroupItemBean> insertItemList = new ArrayList<>();

        List<Long> testMethodIdList = testMethodList.stream().map(a -> a.getId()).collect(Collectors.toList());
        List<EmcItemGroupBean> existsList = itemGroupService.selectListByFilter(SearchFilter.instance().match("testMethodId", testMethodIdList).filter(MatchPattern.OR));
        List<EmcItemBean> itemList = itemService.getDao().selectListByOneColumnValues(testMethodIdList, "testMethodId");

        for (EmcTestMethodBean tm : testMethodList) {
            // 已有同名分析项组不再添加
            if (existsList.stream().anyMatch(a -> ObjectUtils.equals(a.getTestMethodId(), tm.getId()) && tm.getExt$Item("testname").equals(a.getItemGroup()))) {
                continue;
            }
            List<EmcItemBean> filteList = itemList.stream().filter(a -> ObjectUtils.equals(a.getTestMethodId(), tm.getId())).collect(Collectors.toList());
            if (filteList.isEmpty()) {
                continue;
            }
            // 插入EmcItemGroupBean
            EmcItemGroupBean insertGroup = new EmcItemGroupBean();
            insertGroup.setId(ApplicationContextHelper.getNextIdentity());
            insertGroup.setTestMethodId(tm.getId());
            insertGroup.setItemGroup(tm.getExt$Item("testname"));
            insertGroup.setTestId(tm.getTestId());
            insertGroupList.add(insertGroup);

            // 插入EmcGroupItemBean
            for (EmcItemBean i : filteList) {

                EmcGroupItemBean insertItem = new EmcGroupItemBean();
                insertItem.setId(ApplicationContextHelper.getNextIdentity());
                insertItem.setItemGroupId(insertGroup.getId());
                insertItem.setItemId(i.getId());
                insertItemList.add(insertItem);
            }

        }
        itemGroupService.getDao().insert(insertGroupList);
        groupItemService.getDao().insert(insertItemList);

    }

    @Override
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void lock(RestJsonWrapperBean wrapper) {
        List<EmcTestMethodBean> testMethodList = wrapper.parse(EmcTestMethodBean.class);
        for (EmcTestMethodBean t : testMethodList) {
            t.setIsLocked(EmcConstant.LOCKED);
        }
        getDao().update(testMethodList, "ISLOCKED");
    }

    @Override
    @AuditTrailEntry(AuditTrailType.UPDATE)
    @Transactional
    public void unlock(RestJsonWrapperBean wrapper) {
        List<EmcTestMethodBean> testMethodList = wrapper.parse(EmcTestMethodBean.class);
        List<EmcTestMethodLogBean> testMethodLogList = new ArrayList<>();
        for (EmcTestMethodBean t : testMethodList) {
            if (EmcConstant.UNLOCK.equals(t.getIsLocked())) {
                continue;
            }
            t.setIsLocked(EmcConstant.UNLOCK);
            String versions = t.getVersions();
            Long newVersion = NumberUtils.parseLong(versions, 0L) + 1L;
            t.setVersions(newVersion + "");
            EmcTestMethodLogBean temp = new EmcTestMethodLogBean();
            temp.setTestMethodId(t.getId());
            temp.setId(ApplicationContextHelper.getNextIdentity());
            temp.setVersions(newVersion + "");
            testMethodLogList.add(temp);
        }
        getDao().update(testMethodList, "ISLOCKED", "VERSIONS");
        testMethodLogService.getDao().insert(testMethodLogList);
    }

    @Override
    public Page<EmcTestMethodLogBean> selectLogPagination(RestJsonWrapperBean wrapper) {
        return testMethodLogService.selectPagination(wrapper);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void copyFormulaTo(RestJsonWrapperBean wrapper) {
        List<EmcTestMethodBean> testMethodList = wrapper.parse(EmcTestMethodBean.class); // 测试方法
        List<EmcItemBean> testItemList = wrapper.parse(EmcItemBean.class); // 分析项

        List<Long> testMethodIdList = testMethodList.stream().map(i -> i.getId()).collect(Collectors.toList());

        List<EmcItemBean> updateItemList = new ArrayList<EmcItemBean>();
        // 查询选中测试下所有的分析项
        List<EmcItemBean> totalItemList = itemService.getDao().selectListByOneColumnValues(testMethodIdList, "testMethodId");
        testItemList.forEach(i ->{
            List<EmcItemBean> itemList = totalItemList.stream().filter(t ->ObjectUtils.equals(i.getItemName(), t.getItemName())).collect(Collectors.toList());

            itemList.forEach(item ->{
                item.setScript(i.getScript());
                item.setScriptFlag(i.getScriptFlag());
                updateItemList.add(item);
            });
        });

        itemService.updateIfChanged(updateItemList);

        // 插入FormulaRequire表
        itemService.bacthInsertFormulaRequired(updateItemList);
    }
    
    @Override
    public void batchUpdateQcIndex(RestJsonWrapperBean wrapper) { 
        List<EmcTestMethodBean> testMethodList = wrapper.parse(EmcTestMethodBean.class); // 测试方法多条数据
        List<EmcItemBean> emcItemList = wrapper.parse(EmcItemBean.class); // 
        EmcItemQcIndexBean qcIndexOrg = wrapper.parseUnique(EmcItemQcIndexBean.class); // qc  
        List<EmcItemQcIndexBean> qcIndexOrgList =  new ArrayList<>();
        qcIndexOrgList.add(qcIndexOrg);

/*        1、获取选中的item列表中的所有itemName列表；//itemid去查
        2、获取testMethod数组中所有的testMethodId列表；
        3、以testMethodId列表、itemName列表为条件查询t_emc_item表，找到所有符合条件的分析项列表itemList；  */    
        List<String> itemNameList = emcItemList.stream().map(a -> a.getItemName()).collect(Collectors.toList());//传过来的分析项
		List<Long> testMethodIdList = testMethodList.stream().map(a -> a.getId()).collect(Collectors.toList());//testMethodId列表 
		//所有符合条件的分析项列表itemList
		  List<EmcItemBean> selectTestItemList =  itemService.selectListByFilter
          		((SearchFilter.instance().match("TESTMETHODID", testMethodIdList).filter(MatchPattern.OR).match("ITEMNAME", itemNameList).filter(MatchPattern.OR)));
		//循环itemList列表，逐一创建newItemQcIndex，并把前端传递的itemQcindex的值赋给newItemQcindex；
		  this.insertQcIndex(selectTestItemList,qcIndexOrgList);
    }
    //循环插入itemQcindex表
    protected void insertQcIndex(List<EmcItemBean> selectTestItemList,List<EmcItemQcIndexBean> qcIndexOrgList) {
        EmcItemQcIndexBean qcIndexOrg = qcIndexOrgList.get(0);
        List<EmcItemQcIndexBean> emcItemQcIndexList = new ArrayList<>();
        for (EmcItemBean emcItem : selectTestItemList) {
        	EmcItemQcIndexBean emcItemQcindex = new EmcItemQcIndexBean();
        	emcItemQcindex.setId(ApplicationContextHelper.getNextIdentity());
        	emcItemQcindex.setItemId(emcItem.getId());
        	emcItemQcindex.setQcCategoryId(qcIndexOrg.getQcCategoryId());
        	emcItemQcindex.setResultLowLimit(qcIndexOrg.getResultLowLimit());
        	emcItemQcindex.setResultHighLimit(qcIndexOrg.getResultHighLimit());
        	emcItemQcindex.setResultHighLimit(qcIndexOrg.getResultHighLimit());
        	emcItemQcindex.setDecisionCategory(qcIndexOrg.getDecisionCategory());
        	emcItemQcindex.setStdLowLimit(qcIndexOrg.getStdLowLimit());
        	emcItemQcindex.setStdHighLimit(qcIndexOrg.getStdHighLimit());
        	emcItemQcindex.setExt$Item("monitortmplid",qcIndexOrg.getExt$Item("monitortmplid"));
        	emcItemQcIndexList.add(emcItemQcindex);
        }
        emcItemQcindexService.getDao().insert(emcItemQcIndexList);
    }


}
