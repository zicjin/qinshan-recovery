package com.sunwayworld.emc.module.mdm.bizmdm.test.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.*;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcItemQcIndexService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcItemResultService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcItemRevisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;

/**
 * @author zhengxy@sunwayworld.com 2021年10月27日
 */
@GikamBean
@Repository
public class PfItemServiceImpl extends EmcItemServiceImpl {
    @Autowired
    private EmcItemQcIndexService itemQcIndexService;

    @Autowired
    private EmcItemRevisionService itemRevisionService;

    @Autowired
    private EmcItemResultService itemResultService;
    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void copyTo(RestJsonWrapperBean jsonWrapper) {
        List<EmcTestMethodBean> targetTestMethodList = jsonWrapper.parse(EmcTestMethodBean.class);
        List<EmcItemBean> testItemList = jsonWrapper.parse(EmcItemBean.class);

        List<Long> testItemIdList = testItemList.stream().map(EmcItemBean::getId).collect(Collectors.toList());
        List<EmcItemBean> sourceItemList = getDao().selectListByIds(testItemIdList);

        if (sourceItemList.isEmpty()) {
            return;
        }

        List<Long> targetTestMethodIdList = targetTestMethodList.stream().map(EmcTestMethodBean::getId).collect(Collectors.toList());
        List<EmcItemBean> existedItemList = getDao().selectListByOneColumnValues(targetTestMethodIdList, "testMethodId");

        List<EmcItemQcIndexBean> copyItemQcIndexList = itemQcIndexService.getDao().selectListByOneColumnValues(testItemIdList,"itemId");
        List<EmcItemRevisionBean> copyItemRevisionList = itemRevisionService.getDao().selectListByOneColumnValues(testItemIdList,"itemId");
        List<EmcItemResultBean> copyItemResultList = itemResultService.getDao().selectListByOneColumnValues(testItemIdList,"itemId");

        List<EmcItemBean> insertItemList = new ArrayList<>();
        List<EmcItemQcIndexBean> insertItemQcIndexList = new ArrayList<>();
        List<EmcItemRevisionBean> insertItemRevisionList = new ArrayList<>();
        List<EmcItemResultBean> insertItemResultList = new ArrayList<>();

        targetTestMethodIdList.forEach(testMethodId -> {
            List<String> existedItemNameList = existedItemList.stream().filter(item -> ObjectUtils.equals(testMethodId, item.getTestMethodId())).map(EmcItemBean::getItemName).collect(Collectors.toList());
            sourceItemList.stream().filter(item -> !existedItemNameList.contains(item.getItemName())).forEach(item -> {
                EmcItemBean newItem = new EmcItemBean();
                // 不复制制单字段
                BeanUtils.copyProperties(item, newItem, EmcConstant.CREATED_COLUMNS);
                newItem.setId(ApplicationContextHelper.getNextIdentity());
                newItem.setTestMethodId(testMethodId);

                //复制qc指标
                List<EmcItemQcIndexBean> curItemQcIndexList = copyItemQcIndexList.stream().filter(e -> ObjectUtils.equals(e.getItemId(),item.getId()))
                        .collect(Collectors.toList());
                curItemQcIndexList.forEach(e -> {
                    EmcItemQcIndexBean newBean = new EmcItemQcIndexBean();
                    BeanUtils.copyProperties(e, newBean, EmcConstant.CREATED_COLUMNS);
                    newBean.setId(ApplicationContextHelper.getNextIdentity());
                    newBean.setItemId(newItem.getId());
                    insertItemQcIndexList.add(newBean);
                });

                //复制分段修约
                List<EmcItemRevisionBean> curItemRevisionList = copyItemRevisionList.stream().filter(e -> ObjectUtils.equals(e.getItemId(),item.getId()))
                        .collect(Collectors.toList());
                curItemRevisionList.forEach(e -> {
                    EmcItemRevisionBean newBean = new EmcItemRevisionBean();
                    BeanUtils.copyProperties(e, newBean, EmcConstant.CREATED_COLUMNS);
                    newBean.setId(ApplicationContextHelper.getNextIdentity());
                    newBean.setItemId(newItem.getId());
                    insertItemRevisionList.add(newBean);
                });

                //复制可选结果
                List<EmcItemResultBean> curItemResultList = copyItemResultList.stream().filter(e -> ObjectUtils.equals(e.getItemId(),item.getId()))
                        .collect(Collectors.toList());
                curItemResultList.forEach(e -> {
                    EmcItemResultBean newBean = new EmcItemResultBean();
                    BeanUtils.copyProperties(e, newBean, EmcConstant.CREATED_COLUMNS);
                    newBean.setId(ApplicationContextHelper.getNextIdentity());
                    newBean.setItemId(newItem.getId());
                    insertItemResultList.add(newBean);
                });

                insertItemList.add(newItem);
            });
        });

        getDao().insert(insertItemList);
        itemQcIndexService.getDao().fastInsert(insertItemQcIndexList);
        itemRevisionService.getDao().fastInsert(insertItemRevisionList);
        itemResultService.getDao().fastInsert(insertItemResultList);
        // 添加分析项公式依赖关系
        insertItemList.stream().filter(item -> !StringUtils.isEmpty(item.getScript())).forEach(this::insertFormulaRequired);
    }
}
