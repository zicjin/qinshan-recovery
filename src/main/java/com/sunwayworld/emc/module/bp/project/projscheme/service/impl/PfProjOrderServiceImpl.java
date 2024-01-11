package com.sunwayworld.emc.module.bp.project.projscheme.service.impl;


import com.sunwayworld.emc.module.bp.project.projscheme.bean.EmcProjOrderBean;
import com.sunwayworld.emc.module.bp.project.projscheme.bean.EmcProjSchemeBean;
import com.sunwayworld.emc.module.bp.project.projscheme.dao.EmcProjOrderDao;
import com.sunwayworld.emc.module.bp.project.projscheme.service.EmcProjOrderService;
import com.sunwayworld.emc.module.bp.project.projscheme.service.EmcProjSchemeService;
import com.sunwayworld.emc.module.mdm.basemdm.indexitem.bean.EmcIndexTestItemBean;
import com.sunwayworld.emc.module.mdm.basemdm.indexitem.service.EmcIndexTestItemService;
import com.sunwayworld.emc.module.mdm.bizmdm.capacity.bean.EmcCapacityItemGroupBean;
import com.sunwayworld.emc.module.mdm.bizmdm.capacity.service.EmcCapacityItemGroupService;
import com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.bean.EmcIndexSetBean;
import com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.bean.EmcOrderIndexBean;
import com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.service.EmcIndexSetService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcGroupItemBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcGroupItemService;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@GikamBean
public class PfProjOrderServiceImpl extends EmcProjOrderServiceImpl implements EmcProjOrderService {

    @Autowired
    private EmcProjOrderDao projOrderDao;

    @Autowired
    @Lazy
    private EmcProjSchemeService projSchemeService;

    @Autowired
    private EmcIndexSetService indexSetService;

    @Autowired
    private EmcIndexTestItemService indexTestItemService;

    @Autowired
    private EmcGroupItemService groupItemService;

    @Autowired
    private EmcCapacityItemGroupService capacityItemGroupService;


    @Override
    public void updateOrderIndex(RestJsonWrapperBean wrapper) {

        // 方案
        List<EmcProjSchemeBean> projSchemeList = wrapper.parse(EmcProjSchemeBean.class);
        List<Long> projSchemeIdList = projSchemeList.stream().map(e -> e.getId()).collect(Collectors.toList());
        // 方案样品
        List<EmcProjOrderBean> projOrderList = selectListByFilter(SearchFilter.instance().match("projSchemeId", projSchemeIdList).filter(MatchPattern.OR));
        //此为监测能力库分析项组
        List<Long> projOrderItemGroupId = projOrderList.stream().map(EmcProjOrderBean :: getItemGroupId).distinct().collect(Collectors.toList());
        List<EmcCapacityItemGroupBean> capacityItemGroupBeans = capacityItemGroupService.selectListByIds(projOrderItemGroupId);
        //List<Long> itemGroupIdList = capacityItemGroupBeans.stream().map(EmcCapacityItemGroupBean ::getItemGroupId).distinct().collect(Collectors.toList());
        // 样品指标
        EmcOrderIndexBean orderIndex = wrapper.parse(EmcOrderIndexBean.class).get(0);
        // 指标设置
        List<EmcIndexSetBean> indexSetList = indexSetService.selectListByFilter(SearchFilter.instance().match("indexId", orderIndex.getId()).filter(MatchPattern.EQ));
        // 指标项目
        List<Long> indexItemIdList = indexSetList.stream().map(e -> e.getIndexItemId()).collect(Collectors.toList());
        // 指标项目分析项
        List<EmcIndexTestItemBean> indexTestItemList = indexTestItemService.selectListByFilter(SearchFilter.instance().match("indexItemId", indexItemIdList).filter(MatchPattern.OR));
        // 分析项
        List<Long> itemIdOfIndexList = indexTestItemList.stream().map(e -> e.getItemId()).collect(Collectors.toList());
        // 分析项组分析项
        List<EmcGroupItemBean> groupItemList = groupItemService.selectListByFilter(SearchFilter.instance().match("ITEMID", itemIdOfIndexList).filter(MatchPattern.OR));
        // 分析项组
        List<Long> itemGroupIdOfIndexList = groupItemList.stream().map(a -> a.getItemGroupId()).distinct().collect(Collectors.toList());

        // 如果方案关联分析项组下的分析项，只要有在指标项目下的分析项中进行了维护的，就更新分析项组的指标
        ArrayList<EmcProjOrderBean> updateProjOrderList = new ArrayList<EmcProjOrderBean>();
        projOrderList.forEach(projOrder -> {
            Long tcItemGroupId = projOrder.getItemGroupId();
            List<EmcCapacityItemGroupBean> capacityItemGroupTargetBeans = capacityItemGroupBeans.stream().filter(cig -> ObjectUtils.equals(tcItemGroupId,cig.getId())).collect(Collectors.toList());
            Long itemGroupId = capacityItemGroupTargetBeans.get(0).getItemGroupId();
            if (itemGroupIdOfIndexList.contains(itemGroupId)) {
                EmcProjOrderBean updateProjOrder = new EmcProjOrderBean();
                updateProjOrder.setId(projOrder.getId());
                updateProjOrder.setIndexCategoryId(orderIndex.getIndexCategoryId());
                updateProjOrder.setIndexId(orderIndex.getId());
                updateProjOrderList.add(updateProjOrder);
            }
        });

        getDao().update(updateProjOrderList, "indexCategoryId", "indexId");

    }

}

