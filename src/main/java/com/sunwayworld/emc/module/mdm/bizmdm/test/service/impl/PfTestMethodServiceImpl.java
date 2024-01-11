package com.sunwayworld.emc.module.mdm.bizmdm.test.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.*;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.*;
import com.sunwayworld.emc.module.mdm.bizmdm.test.validator.EmcTestMethodCopyValidator;
import com.sunwayworld.framework.validator.data.annotation.ValidateDataWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.bizmdm.capacity.bean.EmcCapacityItemGroupBean;
import com.sunwayworld.emc.module.mdm.bizmdm.capacity.service.EmcCapacityItemGroupService;
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
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.sys.formula.bean.CoreFormulaRequiredBean;
import com.sunwayworld.module.sys.formula.service.CoreFormulaRequiredService;

/**
 * @author zhengxy@sunwayworld.com 2021年06月17日
 */
@Repository
@GikamBean
public class PfTestMethodServiceImpl extends EmcTestMethodServiceImpl implements PfTestMethodService {

    @Autowired
    private EmcTestMethodDefaultQcService testMethodDefaultQcService;

    @Autowired
    private EmcRelatedTestMethodService relatedTestMethodService;

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
    private EmcCapacityItemGroupService capacityItemGroupService;

    @Autowired
    @Lazy
    private EmcTestmethodBeforehandleService testMethodBeforeHandleService;

    @Autowired
    private CoreFormulaRequiredService coreFormulaRequiredService;

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

        // 复制前处理
        EmcTestmethodBeforehandleBean testMethodBeforeHandleFilter = new EmcTestmethodBeforehandleBean();
        testMethodBeforeHandleFilter.setTestMethodId(id);
        List<EmcTestmethodBeforehandleBean> testMethodBeforeHandleList = testMethodBeforeHandleService.getDao().selectList(testMethodBeforeHandleFilter);
        testMethodBeforeHandleList.stream().forEach(e -> {
            e.setId(ApplicationContextHelper.getNextIdentity());
            e.setTestMethodId(copyTestMethod.getId());
        });
        testMethodBeforeHandleService.getDao().insert(testMethodBeforeHandleList);

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

        // 复制默认QC
        EmcTestMethodDefaultQcBean qcFilter = new EmcTestMethodDefaultQcBean();
        qcFilter.setTestMethodId(id);
        List<EmcTestMethodDefaultQcBean> copyQcList = testMethodDefaultQcService.getDao().selectList(qcFilter);
        copyQcList.stream().forEach(e -> {
            e.setId(ApplicationContextHelper.getNextIdentity());
            e.setTestMethodId(copyTestMethod.getId());
        });
        testMethodDefaultQcService.getDao().insert(copyQcList);

        // 复制关联测试
        EmcRelatedTestMethodBean relatedFilter = new EmcRelatedTestMethodBean();
        relatedFilter.setTestMethodId(id);
        List<EmcRelatedTestMethodBean> copyRelatedList = relatedTestMethodService.getDao().selectList(relatedFilter);
        copyRelatedList.stream().forEach(e -> {
            e.setId(ApplicationContextHelper.getNextIdentity());
            e.setTestMethodId(copyTestMethod.getId());
        });
        relatedTestMethodService.getDao().insert(copyRelatedList);

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
    public Page<EmcTestMethodDefaultQcBean> selectDefaultQcPagination(Long id, RestJsonWrapperBean wrapper) {
        return testMethodDefaultQcService.selectPaginationByFilter(SearchFilter.instance().match("testMethodId", id).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    public void insertDefaultQc(Long id, RestJsonWrapperBean wrapper) {
        testMethodDefaultQcService.insert(id, wrapper);
    }

    @Override
    public void deleteDefaultQc(Long id, RestJsonWrapperBean wrapper) {
        testMethodDefaultQcService.delete(wrapper);
    }

    @Override
    public Page<EmcRelatedTestMethodBean> selectRelatedTestMethodPagination(Long id, RestJsonWrapperBean wrapper) {
        return relatedTestMethodService.selectPaginationByFilter(SearchFilter.instance().match("testMethodId", id).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    public void insertRelatedTestMethod(Long id, RestJsonWrapperBean wrapper) {
        relatedTestMethodService.insert(id, wrapper);
    }

    @Override
    public void deleteRelatedTestMethod(Long id, RestJsonWrapperBean wrapper) {
        relatedTestMethodService.delete(wrapper);
    }

    @Override
    public void batchInsertRelatedTestMethod(RestJsonWrapperBean wrapper) {
        List<EmcTestMethodBean> targetTestMethodList = wrapper.parse(EmcTestMethodBean.class);
        List<EmcRelatedTestMethodBean> relatedTestMethodList = wrapper.parse(EmcRelatedTestMethodBean.class);

        List<Long> testMethodIdList = targetTestMethodList.stream().map(EmcTestMethodBean::getId).collect(Collectors.toList());
        List<EmcRelatedTestMethodBean> existedRelatedMethodList = relatedTestMethodService.getDao().selectListByOneColumnValues(testMethodIdList, "testMethodId");

        List<EmcRelatedTestMethodBean> insertRelatedTestMethodList = new ArrayList<>();
        targetTestMethodList.forEach(testMethod -> {
            List<Long> existedRelatedIdList = existedRelatedMethodList.stream().filter(existed -> ObjectUtils.equals(existed.getTestMethodId(), testMethod.getId())).map(EmcRelatedTestMethodBean::getRelatedTestMethodId).distinct().collect(Collectors.toList());

            relatedTestMethodList.stream().filter(related -> !existedRelatedIdList.contains(related.getRelatedTestMethodId())).forEach(related -> {
                EmcRelatedTestMethodBean newRelatedMethod = new EmcRelatedTestMethodBean();
                newRelatedMethod.setId(ApplicationContextHelper.getNextIdentity());
                newRelatedMethod.setRelatedTestMethodId(related.getRelatedTestMethodId());
                newRelatedMethod.setTestMethodId(testMethod.getId());
                insertRelatedTestMethodList.add(newRelatedMethod);
            });
        });

        relatedTestMethodService.getDao().insert(insertRelatedTestMethodList);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void copyByCategory(RestJsonWrapperBean jsonWrapper) {
        List<EmcTestMethodBean> testMethodList = jsonWrapper.parse(EmcTestMethodBean.class);
        Long testCategoryId = Long.parseLong(jsonWrapper.getFilterValue("testCategoryId"));

        List<Long> testMethodIdList = testMethodList.stream().map(EmcTestMethodBean::getId).distinct().collect(Collectors.toList());
        List<EmcTestMethodOrderCategoryBean> copyOrderAllCategoryList = testMethodOrderCategoryService.selectListByFilter(SearchFilter.instance().match("testMethodId", testMethodIdList).filter(MatchPattern.OR));
        List<EmcEquiptTestMethodBean> copyAllequiptList = equiptTestMethodService.selectListByFilter(SearchFilter.instance().match("testMethodId", testMethodIdList).filter(MatchPattern.OR));

        List<EmcItemBean> copyAllItemList = itemService.selectListByFilter(SearchFilter.instance().match("testMethodId", testMethodIdList).filter(MatchPattern.OR));
        List<Long> itemAllIdList = copyAllItemList.stream().map(EmcItemBean :: getId).distinct().collect(Collectors.toList());
        // 分析项--子表
        List<EmcItemQcIndexBean> copyQcIndexAllList = itemQcIndexService.getDao().selectListByOneColumnValues(itemAllIdList, "ITEMID");// QC指标
        List<EmcItemRevisionBean> copyRevisionAllList = itemRevisionService.getDao().selectListByOneColumnValues(itemAllIdList, "ITEMID");// 分段修约
        List<EmcItemResultBean> copyResultAllList = itemResultService.getDao().selectListByOneColumnValues(itemAllIdList, "ITEMID");// 分析项结果
        List<EmcGroupItemBean> copyGroupItemAllList = groupItemService.getDao().selectListByOneColumnValues(itemAllIdList, "ITEMID");// 分析项组分析项

        List<EmcItemGroupBean> copyAllItemGroupList = itemGroupService.selectListByFilter(SearchFilter.instance().match("testMethodId", testMethodIdList).filter(MatchPattern.OR));

        List<EmcTestMethodOrderCategoryBean> copyAllUpdateOrderCategoryList = new ArrayList<>();
        List<EmcEquiptTestMethodBean> copyAllUpdateEquiptList = new ArrayList<>();
        List<EmcItemGroupBean> copyitemUpdateGroupList = new ArrayList<>();
        List<EmcGroupItemBean> groupItemList = new ArrayList<>();

        testMethodList.forEach(testMethod ->{
        	 Long id = testMethod.getId();

             // 复制测试方法
             EmcTestMethodBean copyTestMethod = getDao().selectById(id);
             copyTestMethod.setId(ApplicationContextHelper.getNextIdentity());
             copyTestMethod.setTestId(testMethod.getTestId());
             copyTestMethod.setTestCategoryId(testCategoryId);
             copyTestMethod.setMethodId(testMethod.getMethodId());
             getDao().insert(copyTestMethod);

             // 复制测试样品类型
             EmcTestMethodOrderCategoryBean orderCategoryFilter = new EmcTestMethodOrderCategoryBean();
             orderCategoryFilter.setTestMethodId(id);
             List<EmcTestMethodOrderCategoryBean> copyOrderCategoryList = copyOrderAllCategoryList.stream().filter(co ->ObjectUtils.equals(id, co.getTestMethodId())).collect(Collectors.toList());;
             copyOrderCategoryList.stream().forEach(e -> {
                 e.setId(ApplicationContextHelper.getNextIdentity());
                 e.setTestMethodId(copyTestMethod.getId());
             });
             copyAllUpdateOrderCategoryList.addAll(copyOrderCategoryList);
             // 复制测试关联仪器
             EmcEquiptTestMethodBean equiptFilter = new EmcEquiptTestMethodBean();
             equiptFilter.setTestMethodId(id);
             List<EmcEquiptTestMethodBean> copyequiptList = copyAllequiptList.stream().filter(et ->ObjectUtils.equals(id, et.getTestMethodId())).collect(Collectors.toList());

             copyequiptList.stream().forEach(e -> {
                 e.setId(ApplicationContextHelper.getNextIdentity());
                 e.setTestMethodId(copyTestMethod.getId());
             });
             copyAllUpdateEquiptList.addAll(copyequiptList);
             // 复制测试方法分析项
             EmcItemBean itemFilter = new EmcItemBean();
             itemFilter.setTestMethodId(id);

             List<EmcItemBean> copyItemList = copyAllItemList.stream().filter(i ->ObjectUtils.equals(id, i.getTestMethodId())).collect(Collectors.toList());
             List<Long> copyItemIdList = copyItemList.stream().map(e -> e.getId()).distinct().collect(Collectors.toList());

             List<EmcItemQcIndexBean> copyQcIndexList = copyQcIndexAllList.stream().filter(qci ->copyItemIdList.contains(qci.getItemId())).collect(Collectors.toList());// QC指标
             List<EmcItemRevisionBean> copyRevisionList = copyRevisionAllList.stream().filter(ir ->copyItemIdList.contains(ir.getItemId())).collect(Collectors.toList());// 分段修约
             List<EmcItemResultBean> copyResultList = copyResultAllList.stream().filter(ir ->copyItemIdList.contains(ir.getItemId())).collect(Collectors.toList());// 分析项结果
             List<EmcGroupItemBean> copyGroupItemList = copyGroupItemAllList.stream().filter(gi ->copyItemIdList.contains(gi.getItemId())).collect(Collectors.toList());// 分析项组分析项

             this.copyItemChildTable(copyTestMethod.getId(), copyItemList, copyQcIndexList, copyRevisionList, copyResultList, copyGroupItemList);

             // 复制测试分析项组

             List<EmcItemGroupBean> copyitemGroupList = copyAllItemGroupList.stream().filter(ig ->ObjectUtils.equals(id,ig.getTestMethodId())).collect(Collectors.toList());;

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
             copyitemUpdateGroupList.addAll(copyitemGroupList);
        });
        testMethodOrderCategoryService.getDao().insert(copyAllUpdateOrderCategoryList);
        equiptTestMethodService.getDao().insert(copyAllUpdateEquiptList);
        itemGroupService.getDao().insert(copyitemUpdateGroupList);
        groupItemService.getDao().insert(groupItemList);

    }

    @Override
    public void copyDefaultQcTo(RestJsonWrapperBean wrapper) {
        List<EmcTestMethodBean> testMethodList = wrapper.parse(EmcTestMethodBean.class);
        List<EmcTestMethodDefaultQcBean> defaultQcList = wrapper.parse(EmcTestMethodDefaultQcBean.class);

        List<EmcTestMethodDefaultQcBean> newDefaultQcList = new ArrayList<>();
        testMethodList.forEach(testMethod -> defaultQcList.forEach(qc -> {
            EmcTestMethodDefaultQcBean newDefaultQc = new EmcTestMethodDefaultQcBean();
            BeanUtils.copyProperties(qc, newDefaultQc);
            newDefaultQc.setTestMethodId(testMethod.getId());
            newDefaultQc.setId(ApplicationContextHelper.getNextIdentity());

            newDefaultQcList.add(newDefaultQc);
        }));

        testMethodDefaultQcService.getDao().insert(newDefaultQcList);
    }

    @Override
    @Transactional
    public void syncTestId(RestJsonWrapperBean wrapper) {
        List<EmcTestMethodBean> testMethodList = wrapper.parse(EmcTestMethodBean.class);
        List<Long> testMethodIdList = testMethodList.stream().map(EmcTestMethodBean::getId).collect(Collectors.toList());
        List<EmcItemGroupBean> itemGroupList = itemGroupService.getDao().selectListByOneColumnValues(testMethodIdList, "testMethodId");
        itemGroupList.forEach(group -> testMethodList.stream().filter(tm -> ObjectUtils.equals(tm.getId(), group.getTestMethodId())).findFirst().ifPresent(tm -> group.setTestId(tm.getTestId())));

        List<Long> itemGroupIdList = itemGroupList.stream().map(EmcItemGroupBean::getId).collect(Collectors.toList());
        List<EmcCapacityItemGroupBean> capacityItemGroupList = capacityItemGroupService.getDao().selectListByOneColumnValues(itemGroupIdList, "itemGroupId");
        capacityItemGroupList.forEach(cig -> itemGroupList.stream().filter(ig -> ObjectUtils.equals(ig.getId(), cig.getItemGroupId())).findFirst().ifPresent(ig -> cig.setTestId(ig.getTestId())));

        itemGroupService.getDao().update(itemGroupList, "testId");
        capacityItemGroupService.getDao().update(capacityItemGroupList, "testId");
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

}
