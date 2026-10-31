/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.emc.module.bp.project.projscheme.service.impl;

import com.sunwayworld.emc.module.bp.project.projscheme.bean.EmcProjOrderBean;
import com.sunwayworld.emc.module.bp.project.projscheme.bean.EmcProjSchemeBean;
import com.sunwayworld.emc.module.bp.project.projscheme.dao.EmcProjOrderDao;
import com.sunwayworld.emc.module.bp.project.projscheme.service.EmcProjOrderService;
import com.sunwayworld.emc.module.bp.project.projscheme.service.EmcProjSchemeService;
import com.sunwayworld.emc.module.bp.project.projscheme.service.impl.EmcProjOrderServiceImpl;
import com.sunwayworld.emc.module.mdm.basemdm.indexitem.bean.EmcIndexTestItemBean;
import com.sunwayworld.emc.module.mdm.basemdm.indexitem.service.EmcIndexTestItemService;
import com.sunwayworld.emc.module.mdm.bizmdm.capacity.bean.EmcCapacityItemGroupBean;
import com.sunwayworld.emc.module.mdm.bizmdm.capacity.service.EmcCapacityItemGroupService;
import com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.bean.EmcOrderIndexBean;
import com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.bean.EmcIndexSetBean;
import com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.service.EmcIndexSetService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcGroupItemBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcGroupItemService;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.ObjectUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class PfProjOrderServiceImpl
extends EmcProjOrderServiceImpl
implements EmcProjOrderService {
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
        List<EmcProjSchemeBean> projSchemeList = wrapper.parse(EmcProjSchemeBean.class);
        List<EmcProjOrderBean> wrapperProjOrderList = wrapper.parse(EmcProjOrderBean.class);
        List<Long> projSchemeIdList = projSchemeList.stream().map(e -> e.getId()).collect(Collectors.toList());
        List<Long> itemGroupIdList = wrapperProjOrderList.stream().map(e -> e.getItemGroupId()).distinct().collect(Collectors.toList());
        List<EmcProjOrderBean> projOrderList = this.selectListByFilter(SearchFilter.instance().match("projSchemeId", projSchemeIdList).filter(MatchPattern.OR).match("itemGroupId", itemGroupIdList).filter(MatchPattern.OR), new Order[0]);
        List<Long> projOrderItemGroupId = projOrderList.stream().map(EmcProjOrderBean::getItemGroupId).distinct().collect(Collectors.toList());
        List<EmcCapacityItemGroupBean> capacityItemGroupBeans = this.capacityItemGroupService.selectListByIds(projOrderItemGroupId);
        EmcOrderIndexBean orderIndex = (EmcOrderIndexBean)((Object)wrapper.parse(EmcOrderIndexBean.class).get(0));
        List<EmcIndexSetBean> indexSetList = this.indexSetService.selectListByFilter(SearchFilter.instance().match("indexId", (Object)orderIndex.getId()).filter(MatchPattern.EQ), new Order[0]);
        List<Long> indexItemIdList = indexSetList.stream().map(e -> e.getIndexItemId()).collect(Collectors.toList());
        List<EmcIndexTestItemBean> indexTestItemList = this.indexTestItemService.selectListByFilter(SearchFilter.instance().match("indexItemId", indexItemIdList).filter(MatchPattern.OR), new Order[0]);
        List<Long> itemIdOfIndexList = indexTestItemList.stream().map(e -> e.getItemId()).collect(Collectors.toList());
        List<EmcGroupItemBean> groupItemList = this.groupItemService.selectListByFilter(SearchFilter.instance().match("ITEMID", itemIdOfIndexList).filter(MatchPattern.OR), new Order[0]);
        List<Long> itemGroupIdOfIndexList = groupItemList.stream().map(a -> a.getItemGroupId()).distinct().collect(Collectors.toList());
        ArrayList<EmcProjOrderBean> updateProjOrderList = new ArrayList<>();
        projOrderList.forEach(projOrder -> {
            Long tcItemGroupId = projOrder.getItemGroupId();
            List<EmcCapacityItemGroupBean> capacityItemGroupTargetBeans = capacityItemGroupBeans.stream().filter(cig -> ObjectUtils.equals((Object)tcItemGroupId, (Object)cig.getId())).collect(Collectors.toList());
            Long itemGroupId = ((EmcCapacityItemGroupBean)((Object)((Object)capacityItemGroupTargetBeans.get(0)))).getItemGroupId();
            if (itemGroupIdOfIndexList.contains(itemGroupId)) {
                EmcProjOrderBean updateProjOrder = new EmcProjOrderBean();
                updateProjOrder.setId(projOrder.getId());
                updateProjOrder.setIndexCategoryId(orderIndex.getIndexCategoryId());
                updateProjOrder.setIndexId(orderIndex.getId());
                updateProjOrderList.add(updateProjOrder);
            }
        });
        this.getDao().update(updateProjOrderList, new String[]{"indexCategoryId", "indexId"});
    }
}
