package com.sunwayworld.emc.module.mdm.bizmdm.test.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.sunwayworld.framework.spring.annotation.GikamBean;
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
import com.sunwayworld.emc.module.mdm.bizmdm.test.dao.EmcItemGroupDao;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcGroupItemService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcItemGroupService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcItemQcIndexService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcItemResultService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcItemRevisionService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcItemService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;

/**
 * @author yuanhh@sunwayworld.com
 * @date 2019-09-19
 */

@Repository
@GikamBean
public class EmcItemGroupServiceImpl implements EmcItemGroupService {

    /** 有机分析项名称前缀匹配正则 */
    private static final Pattern ORGANIC_ITEM_NAME_PREFIX_REGEX = Pattern.compile("^(\\d+)");

    @Autowired
    private EmcItemGroupDao itemGroupDao;

    @Autowired
    private EmcItemService itemService;

    @Autowired
    @Lazy
    private EmcGroupItemService groupItemService;

    @Autowired
    @Lazy
    private EmcOrderItemGroupService orderItemGroupService;

    @Autowired
    @Lazy
    private EmcProjOrderService projOrderService;

    @Autowired
    private EmcItemRevisionService itemRevisionService;

    @Autowired
    private EmcItemQcIndexService itemQcIndexService;

    @Autowired
    private EmcItemResultService itemResultService;

    @Override
    @SuppressWarnings("unchecked")
    public EmcItemGroupDao getDao() {
        return itemGroupDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(Long testMethodId, RestJsonWrapperBean jsonWrapper) {
        EmcItemGroupBean emcItemGroup = jsonWrapper.parseUnique(EmcItemGroupBean.class);
        emcItemGroup.setId(ApplicationContextHelper.getNextIdentity());
        emcItemGroup.setTestMethodId(testMethodId);
        getDao().insert(emcItemGroup);

        List<EmcGroupItemBean> groupItemList = new ArrayList<EmcGroupItemBean>();

        EmcItemBean filter = new EmcItemBean();
        filter.setTestMethodId(testMethodId);
        filter.setParam(Constant.YES);
        List<EmcItemBean> itemList = itemService.selectList(filter);
        for (EmcItemBean itemBean : itemList) {
            EmcGroupItemBean groupItem = new EmcGroupItemBean();
            groupItem.setId(ApplicationContextHelper.getNextIdentity());
            groupItem.setItemGroupId(emcItemGroup.getId());
            groupItem.setItemId(itemBean.getId());
            groupItemList.add(groupItem);
        }

        groupItemService.getDao().insert(groupItemList);
        return emcItemGroup.getId();
    }

    @Override
    public void copy(Long id, RestJsonWrapperBean wrapper) {
        EmcItemGroupBean emcItemGroup = wrapper.parseUnique(EmcItemGroupBean.class);
        Long oldItemGroupId = emcItemGroup.getId();
        Long newItemGroupId = ApplicationContextHelper.getNextIdentity();
        emcItemGroup.setId(newItemGroupId);
        getDao().insert(emcItemGroup);

        List<Long> groupItemIdList = groupItemService.getDao().selectListByOneColumnValue(oldItemGroupId, "itemGroupId").stream().map(e -> e.getItemId()).collect(Collectors.toList());

        List<EmcGroupItemBean> groupItemList = new ArrayList<EmcGroupItemBean>();
        groupItemIdList.stream().forEach(i -> {
            EmcGroupItemBean groupItem = new EmcGroupItemBean();
            groupItem.setId(ApplicationContextHelper.getNextIdentity());
            groupItem.setItemGroupId(newItemGroupId);
            groupItem.setItemId(i);
            groupItemList.add(groupItem);
        });

        groupItemService.getDao().insert(groupItemList);
    }

    @Override
    public List<Long> checkUsed(RestJsonWrapperBean wrapper) {
        List<Long> itemGroupIdList = wrapper.parseId(Long.class);

        List<EmcOrderItemGroupBean> orderItemGroupList = orderItemGroupService.getDao().selectListByOneColumnValues(itemGroupIdList, "ITEMGROUPID", Collections.singletonList("ID"));

        if (orderItemGroupList.isEmpty()) {
            List<EmcProjOrderBean> projOrderList = projOrderService.getDao().selectListByOneColumnValues(itemGroupIdList, "ITEMGROUPID", Collections.singletonList("ID"));

            return projOrderList.stream().map(EmcProjOrderBean::getId).collect(Collectors.toList());
        }

        return orderItemGroupList.stream().map(EmcOrderItemGroupBean::getId).collect(Collectors.toList());
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long copyOrganicItemGroup(RestJsonWrapperBean wrapper) {
        EmcItemGroupBean itemGroup = wrapper.parseUnique(EmcItemGroupBean.class);
        List<EmcGroupItemBean> groupItemList = groupItemService.getDao().selectListByOneColumnValue(itemGroup.getId(), "ITEMGROUPID", Arrays.asList("ITEMID"));
        List<Long> itemIdList = groupItemList.stream().map(EmcGroupItemBean::getItemId).collect(Collectors.toList());
        List<EmcItemBean> itemList = itemService.getDao().selectListByOneColumnValues(itemIdList, "ID", Arrays.asList("ID", "ITEMNAME"));

        // 判断有机分析项名是否都是以数字开头，若不是直接返回，不进行复制
        boolean validItemName = itemList.stream().allMatch(i -> ORGANIC_ITEM_NAME_PREFIX_REGEX.matcher(i.getItemName()).find());
        if (!validItemName) {
            return 0L;
        }

        Long testMethodId = itemGroup.getTestMethodId();
        List<EmcItemBean> testMethodItemList = itemService.getDao().selectListByOneColumnValue(testMethodId, "TESTMETHODID", Arrays.asList("ID", "ITEMNAME"));
        int maxPrefixNum = 0;
        for (EmcItemBean i : testMethodItemList) {
            Matcher matcher = ORGANIC_ITEM_NAME_PREFIX_REGEX.matcher(i.getItemName());
            if (matcher.find()) {
                int num = NumberUtils.parseInt(matcher.group(0));
                if (num > maxPrefixNum) {
                    maxPrefixNum = num;
                }
            }
        }

        String groupName = itemGroup.getItemGroup();
        EmcItemGroupBean newItemGroup = new EmcItemGroupBean();
        newItemGroup.setId(ApplicationContextHelper.getNextIdentity());
        newItemGroup.setTestMethodId(itemGroup.getTestMethodId());
        newItemGroup.setTestId(itemGroup.getTestId());
        newItemGroup.setItemGroup(groupName);

        List<EmcItemBean> copyItemList = itemService.getDao().selectListByIds(itemIdList);
        List<EmcItemRevisionBean> itemRevisionList = itemRevisionService.getDao().selectListByOneColumnValues(itemIdList, "ITEMID");
        List<EmcItemQcIndexBean> itemQcIndexList = itemQcIndexService.getDao().selectListByOneColumnValues(itemIdList, "ITEMID");
        List<EmcItemResultBean> itemResultList = itemResultService.getDao().selectListByOneColumnValues(itemIdList, "ITEMID");

        List<EmcItemBean> newItemList = new ArrayList<>();
        List<EmcGroupItemBean> newGroupItemList = new ArrayList<>();
        List<EmcItemRevisionBean> newItemRevisionList = new ArrayList<>();
        List<EmcItemQcIndexBean> newItemQcIndexList = new ArrayList<>();
        List<EmcItemResultBean> newItemResultList = new ArrayList<>();

        EmcItemBean maxOrderNoItem = itemService.selectFirstByFilter(SearchFilter.instance().match("TESTMETHODID", testMethodId).filter(MatchPattern.EQ), Order.desc("orderNo"));
        long maxOrderNo = maxOrderNoItem.getOrderNo();

        Map<String, String> itemNameMap = new HashMap<>();
        String newMaxPrefixNum = String.format("%02d", (maxPrefixNum + 1));
        copyItemList.stream().forEach(i -> {
            EmcItemBean newItem = new EmcItemBean();

            BeanUtils.copyProperties(i, newItem, EmcConstant.CREATED_COLUMNS);

            newItem.setId(ApplicationContextHelper.getNextIdentity());
            String withoutNumName = ORGANIC_ITEM_NAME_PREFIX_REGEX.matcher(i.getItemName()).replaceAll("").trim();
            newItem.setItemName(newMaxPrefixNum + withoutNumName);
            newItem.setItemAliasName(groupName + withoutNumName);
            newItem.setOrderNo(maxOrderNo + copyItemList.indexOf(i) + 1);
            newItemList.add(newItem);
            itemNameMap.put(i.getItemName(), newItem.getItemName());

            // 复制分段修约
            itemRevisionList.stream().filter(r -> ObjectUtils.equals(r.getItemId(), i.getId())).forEach(r -> {
                EmcItemRevisionBean newRevision = new EmcItemRevisionBean();
                BeanUtils.copyProperties(r, newRevision, EmcConstant.CREATED_COLUMNS);
                newRevision.setId(ApplicationContextHelper.getNextIdentity());
                newRevision.setItemId(newItem.getId());
                newItemRevisionList.add(newRevision);
            });

            // 复制QC指标
            itemQcIndexList.stream().filter(r -> ObjectUtils.equals(r.getItemId(), i.getId())).forEach(r -> {
                EmcItemQcIndexBean newQcIndex = new EmcItemQcIndexBean();
                BeanUtils.copyProperties(r, newQcIndex, EmcConstant.CREATED_COLUMNS);
                newQcIndex.setId(ApplicationContextHelper.getNextIdentity());
                newQcIndex.setItemId(newItem.getId());
                newItemQcIndexList.add(newQcIndex);
            });

            // 复制分析项结果
            itemResultList.stream().filter(r -> ObjectUtils.equals(r.getItemId(), i.getId())).forEach(r -> {
                EmcItemResultBean newResult = new EmcItemResultBean();
                BeanUtils.copyProperties(r, newResult, EmcConstant.CREATED_COLUMNS);
                newResult.setId(ApplicationContextHelper.getNextIdentity());
                newResult.setItemId(newItem.getId());
                newItemResultList.add(newResult);
            });

            // 分析项组与分析项关系
            EmcGroupItemBean newGroupItem = new EmcGroupItemBean();
            newGroupItem.setId(ApplicationContextHelper.getNextIdentity());
            newGroupItem.setItemGroupId(newItemGroup.getId());
            newGroupItem.setItemId(newItem.getId() == null ? i.getId() : newItem.getId());
            newGroupItemList.add(newGroupItem);

        });

        if (newItemList.size() == 0) {
            return 0L;
        }

        // 替换公式中的分析项名
        newItemList.stream().forEach(i -> {
            String script = i.getScript();
            for (@SuppressWarnings("rawtypes")
            Map.Entry entry : itemNameMap.entrySet()) {
                script = StringUtils.replace(script, entry.getKey().toString(), entry.getValue().toString());
            }
            i.setScript(script);
        });

        getDao().insert(newItemGroup);
        itemService.getDao().insert(newItemList);
        groupItemService.getDao().insert(newGroupItemList);
        itemRevisionService.getDao().insert(newItemRevisionList);
        itemQcIndexService.getDao().insert(newItemQcIndexList);
        itemResultService.getDao().insert(newItemResultList);

        return (long) newItemList.size();
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void editOrganicItemGroup(RestJsonWrapperBean wrapper) {
        EmcItemGroupBean itemGroup = wrapper.parseUnique(EmcItemGroupBean.class);
        String newGroupName = itemGroup.getItemGroup();
        List<EmcGroupItemBean> groupItemList = groupItemService.getDao().selectListByOneColumnValue(itemGroup.getId(), "ITEMGROUPID", Arrays.asList("ITEMID"));
        List<Long> itemIdList = groupItemList.stream().map(EmcGroupItemBean::getItemId).collect(Collectors.toList());
        List<EmcItemBean> itemList = itemService.getDao().selectListByOneColumnValues(itemIdList, "ID", Arrays.asList("ID", "ITEMNAME"));

        itemList.stream().forEach(i -> {
            String withoutNumName = ORGANIC_ITEM_NAME_PREFIX_REGEX.matcher(i.getItemName()).replaceAll("").trim();
            i.setItemAliasName(newGroupName + withoutNumName);
        });

        getDao().update(itemGroup, "ITEMGROUP");
        itemService.getDao().update(itemList, "ITEMALIASNAME");
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void copyOrganicFormulaTo(Long itemGroupId, RestJsonWrapperBean wrapper) {
        EmcItemGroupBean itemGroup = wrapper.parseUnique(EmcItemGroupBean.class);
        List<EmcGroupItemBean> targetGroupItemList = groupItemService.getDao().selectListByOneColumnValue(itemGroup.getId(), "ITEMGROUPID", Arrays.asList("ITEMID"));
        List<Long> targetItemIdList = targetGroupItemList.stream().map(EmcGroupItemBean::getItemId).collect(Collectors.toList());
        List<EmcItemBean> targetItemList = itemService.getDao().selectListByOneColumnValues(targetItemIdList, "ID", Arrays.asList("ID", "ITEMNAME"));

        List<EmcGroupItemBean> sourceGroupItemList = groupItemService.getDao().selectListByOneColumnValue(itemGroupId, "ITEMGROUPID", Arrays.asList("ITEMID"));
        List<Long> sourceItemIdList = sourceGroupItemList.stream().map(EmcGroupItemBean::getItemId).collect(Collectors.toList());
        List<EmcItemBean> sourceItemList = itemService.getDao().selectListByOneColumnValues(sourceItemIdList, "ID", Arrays.asList("ID", "ITEMNAME", "SCRIPT", "SCRIPTFLAG"));

        Map<String, String> itemNameMap = new HashMap<>();
        targetItemList.stream().forEach(t -> {
            String targetItemName = ORGANIC_ITEM_NAME_PREFIX_REGEX.matcher(t.getItemName()).replaceAll("").trim();
            sourceItemList.stream().filter(s -> StringUtils.equals(targetItemName, ORGANIC_ITEM_NAME_PREFIX_REGEX.matcher(s.getItemName()).replaceAll("").trim())).findFirst().ifPresent(s -> {
                t.setScript(s.getScript());
                t.setScriptFlag(s.getScriptFlag());
                itemNameMap.put(s.getItemName(), t.getItemName());
            });
        });

        // 替换公式中的分析项名
        targetItemList.stream().forEach(i -> {
            String script = i.getScript();
            for (@SuppressWarnings("rawtypes")
            Map.Entry entry : itemNameMap.entrySet()) {
                script = StringUtils.replace(script, entry.getKey().toString(), entry.getValue().toString());
            }
            i.setScript(script);
        });

        itemService.getDao().update(targetItemList, "SCRIPT", "SCRIPTFLAG");
    }
}
