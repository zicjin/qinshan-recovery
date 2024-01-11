package com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.bean.EmcPropertyDataBean;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.service.EmcPropertyDataService;
import com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.bean.EmcIndexSetBean;
import com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.bean.EmcOrderIndexBean;
import com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.dao.EmcOrderIndexDao;
import com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.service.EmcIndexSetService;
import com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.service.EmcOrderIndexService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.ObjectUtils;

/**
 * @author zhangsy@sunwayworld.com 2019年9月17日
 */

@Repository
@GikamBean
public class EmcOrderIndexServiceImpl implements EmcOrderIndexService {
    @Autowired
    private EmcOrderIndexDao emcOrderIndexDao;

    @Autowired
    private EmcIndexSetService emcIndexSetSerivce;

    @Autowired
    private EmcPropertyDataService propertyDataService;

    @Override
    @SuppressWarnings("unchecked")
    public EmcOrderIndexDao getDao() {
        return emcOrderIndexDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public String insert(Long id, RestJsonWrapperBean jsonWrapper) {
        EmcOrderIndexBean emcOrderIndex = jsonWrapper.parseUnique(EmcOrderIndexBean.class);
        emcOrderIndex.setId(ApplicationContextHelper.getNextSequence(getDao().getTable()));
        emcOrderIndex.setIndexCategoryId(id);
        getDao().insert(emcOrderIndex);
        return emcOrderIndex.getId();
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void copy(RestJsonWrapperBean jsonWrapper) {
        List<EmcOrderIndexBean> wrapperOrderIndexList = jsonWrapper.parse(EmcOrderIndexBean.class);
        List<String> oldOrderIndexIdList = wrapperOrderIndexList.stream().map(e -> e.getId()).distinct().collect(Collectors.toList());
        int oldOrderIndexIdSize = oldOrderIndexIdList.size();
        List<String> newOrderIndexIdList = ApplicationContextHelper.getNextSequenceList(getDao().getTable(), oldOrderIndexIdSize);

        //旧数据
        List<EmcOrderIndexBean> oldOrderIndexList = selectListByIds(oldOrderIndexIdList);
        List<EmcIndexSetBean> oldIndexSetList = emcIndexSetSerivce.getDao().selectListByOneColumnValues(oldOrderIndexIdList, "INDEXID");
        List<Long> oldIndexSetIdList = oldIndexSetList.stream().map(e -> e.getId()).collect(Collectors.toList());
        List<EmcPropertyDataBean> oldPropertyDataList = propertyDataService.getDao().selectListByOneColumnValues(oldIndexSetIdList, "BIZID");

        //新增数据
        List<EmcOrderIndexBean> insertOrderIndexList = new ArrayList<>();
        List<EmcIndexSetBean> insertIndexSetList = new ArrayList<>();
        List<EmcPropertyDataBean> insertPropertyDataList = new ArrayList<>();
        for (int i = 0; i < oldOrderIndexIdSize; i++) {
            // 复制样品指标
            EmcOrderIndexBean insertOrderIndex = new EmcOrderIndexBean();
            EmcOrderIndexBean oldOrderIndex = oldOrderIndexList.get(i);
            BeanUtils.copyProperties(oldOrderIndex, insertOrderIndex);
            String newOrderIndexId = newOrderIndexIdList.get(i);
            insertOrderIndex.setId(newOrderIndexId);
            insertOrderIndexList.add(insertOrderIndex);

            // 复制指标设置
            List<EmcIndexSetBean> oldOrderIndexSetList = oldIndexSetList.stream().filter(e -> ObjectUtils.equals(e.getIndexId(), oldOrderIndex.getId())).collect(Collectors.toList());
            oldOrderIndexSetList.forEach(oldOrderIndexSet -> {
                EmcIndexSetBean insertIndexSet = new EmcIndexSetBean();
                BeanUtils.copyProperties(oldOrderIndexSet, insertIndexSet);
                Long insertIndexSetId = ApplicationContextHelper.getNextIdentity();
                insertIndexSet.setId(insertIndexSetId);
                insertIndexSet.setIndexId(newOrderIndexId);
                insertIndexSetList.add(insertIndexSet);

                // 复制指标设置属性值
                EmcPropertyDataBean oldPropertyData = oldPropertyDataList.stream().filter(e -> ObjectUtils.equals(e.getBizId(), oldOrderIndexSet.getId())).findFirst().orElse(null);
                if (oldPropertyData != null) {
                    EmcPropertyDataBean insertPropertyData = new EmcPropertyDataBean();
                    BeanUtils.copyProperties(oldPropertyData, insertPropertyData);
                    insertPropertyData.setId(ApplicationContextHelper.getNextIdentity());
                    insertPropertyData.setBizId(insertIndexSetId);
                    insertPropertyDataList.add(insertPropertyData);
                }
            });
        }

        getDao().insert(insertOrderIndexList);
        emcIndexSetSerivce.getDao().insert(insertIndexSetList);
        propertyDataService.getDao().insert(insertPropertyDataList);

    }
}
