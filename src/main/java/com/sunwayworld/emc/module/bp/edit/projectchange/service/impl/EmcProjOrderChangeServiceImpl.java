package com.sunwayworld.emc.module.bp.edit.projectchange.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.emc.module.bp.edit.projectchange.bean.EmcProjOrderChangeBean;
import com.sunwayworld.emc.module.bp.edit.projectchange.bean.EmcProjSchemeChangeBean;
import com.sunwayworld.emc.module.bp.edit.projectchange.dao.EmcProjOrderChangeDao;
import com.sunwayworld.emc.module.bp.edit.projectchange.service.EmcProjOrderChangeService;
import com.sunwayworld.emc.module.bp.edit.projectchange.service.EmcProjSchemeChangeService;
import com.sunwayworld.emc.module.bp.project.projscheme.bean.EmcProjDTO;
import com.sunwayworld.emc.module.mdm.basemdm.indexitem.bean.EmcIndexTestItemBean;
import com.sunwayworld.emc.module.mdm.basemdm.indexitem.service.EmcIndexTestItemService;
import com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.bean.EmcIndexSetBean;
import com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.bean.EmcOrderIndexBean;
import com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.service.EmcIndexSetService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcGroupItemBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcGroupItemService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.ObjectUtils;

/**
 * @author yuanhh@sunwayworld.com
 * @date 2019-10-08
 */

@Repository
@GikamBean
public class EmcProjOrderChangeServiceImpl implements EmcProjOrderChangeService {

    @Autowired
    private EmcProjOrderChangeDao projOrderDao;

    @Autowired
    @Lazy
    private EmcProjSchemeChangeService projSchemeService;

    @Autowired
    private EmcIndexSetService indexSetService;

    @Autowired
    private EmcIndexTestItemService indexTestItemService;

    @Autowired
    private EmcGroupItemService groupItemService;

    @Override
    @SuppressWarnings("unchecked")
    public EmcProjOrderChangeDao getDao() {
        return projOrderDao;
    }

    @Override
    public Page<EmcProjDTO> selectByProjId(Long projId) {
        List<EmcProjDTO> projList = projOrderDao.selectByProjId(projId);
        projList.stream().forEach(e -> {
            String monitorPointCategoryName = ObjectUtils.isEmpty(e.getMonitorPointCategoryName()) ? EmcConstant.N_A : e.getMonitorPointCategoryName();
            String monitorPointNo = ObjectUtils.isEmpty(e.getMonitorPointNo()) ? EmcConstant.N_A : e.getMonitorPointNo();
            String monitorPointName = ObjectUtils.isEmpty(e.getMonitorPointName()) ? EmcConstant.N_A : e.getMonitorPointName();
            String orgName = ObjectUtils.isEmpty(e.getOrgName()) ? EmcConstant.N_A : e.getOrgName();

            e.setMonitorPointCategoryName(monitorPointCategoryName);
            e.setMonitorPointNo(monitorPointNo);
            e.setMonitorPointName(monitorPointName);
            e.setOrgName(orgName);
        });

        // 根据监测点类型,监测点编码,监测点名称,周期,频次,采样科室分组
        List<EmcProjDTO> filterProjList = projList.stream()
                .collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(
                        o -> o.getMonitorPointCategoryName() + ";" + o.getMonitorPointNo() + ";" + o.getMonitorPointName() + ";" + o.getCycle() + ";" + o.getFrequency() + ";" + o.getOrgName()))),
                        ArrayList::new));

        for (EmcProjDTO proj : filterProjList) {
            StringBuilder itemGroups = new StringBuilder("");
            projList.stream().forEach(e -> {
                if (ObjectUtils.equals(e.getMonitorPointCategoryName(), proj.getMonitorPointCategoryName()) && ObjectUtils.equals(e.getMonitorPointNo(), proj.getMonitorPointNo())
                        && ObjectUtils.equals(e.getMonitorPointName(), proj.getMonitorPointName()) && ObjectUtils.equals(e.getCycle(), proj.getCycle())
                        && ObjectUtils.equals(e.getFrequency(), proj.getFrequency()) && ObjectUtils.equals(e.getOrgName(), proj.getOrgName())) {
                    if (itemGroups.indexOf(e.getItemGroups() + ",") < 0) {
                        itemGroups.append(e.getItemGroups() + ",");
                    }
                }
            });
            if (itemGroups.length() > 0) {
                proj.setItemGroups(itemGroups.substring(0, itemGroups.length() - 1));
            }
        }

        return new Pagination<>(filterProjList);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void editSubcontractor(RestJsonWrapperBean wrapper) {
        List<EmcProjOrderChangeBean> projOrderList = wrapper.parse(EmcProjOrderChangeBean.class);
        List<Long> schemeIdList = projSchemeService.getDao().selectListByOneColumnValue(projSchemeService.selectById(projOrderList.get(0).getProjSchemeId()).getProjId(), "PROJID", Arrays.asList("ID"))
                .stream().map(e -> e.getId()).collect(Collectors.toList());
        List<EmcProjOrderChangeBean> subcontractorProjOrderList = getDao().selectListByOneColumnValues(schemeIdList, "PROJSCHEMEID", Arrays.asList("ID", "SUBCONTRACTORID", "SUBPACKAGENO")).stream()
                .filter(e -> !ObjectUtils.isEmpty(e.getSubcontractorId())).collect(Collectors.toList());

        EmcProjOrderChangeBean updateProjOrder = new EmcProjOrderChangeBean();
        updateProjOrder.setSubcontractorId(projOrderList.get(0).getSubcontractorId());
        // 更新分包单号
        if (!subcontractorProjOrderList.isEmpty()) {
            updateProjOrder.setSubPackageNo(subcontractorProjOrderList.get(0).getSubPackageNo());
        } else {
            updateProjOrder.setSubPackageNo(ApplicationContextHelper.getNextSequence(getDao().getTable()));
        }

        getDao().updateByIds(updateProjOrder, projOrderList.stream().map(e -> e.getId()).collect(Collectors.toList()), "subPackageNo", "subcontractorId");
    }

    @Override
    public void updateOrderIndex(RestJsonWrapperBean wrapper) {

        // 方案
        List<EmcProjSchemeChangeBean> projSchemeList = wrapper.parse(EmcProjSchemeChangeBean.class);
        List<Long> projSchemeIdList = projSchemeList.stream().map(e -> e.getId()).collect(Collectors.toList());
        // 方案样品
        List<EmcProjOrderChangeBean> projOrderList = selectListByFilter(SearchFilter.instance().match("projSchemeId", projSchemeIdList).filter(MatchPattern.OR));

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
        ArrayList<EmcProjOrderChangeBean> updateProjOrderList = new ArrayList<EmcProjOrderChangeBean>();
        projOrderList.forEach(projOrder -> {
            Long itemGroupId = projOrder.getItemGroupId();

            if (itemGroupIdOfIndexList.contains(itemGroupId)) {
                EmcProjOrderChangeBean updateProjOrder = new EmcProjOrderChangeBean();
                updateProjOrder.setId(projOrder.getId());
                updateProjOrder.setIndexCategoryId(orderIndex.getIndexCategoryId());
                updateProjOrder.setIndexId(orderIndex.getId());
                updateProjOrderList.add(updateProjOrder);
            }
        });

        getDao().update(updateProjOrderList, "indexCategoryId", "indexId");

    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void relevanceUpdateSubcontractor(RestJsonWrapperBean wrapper) {
        List<EmcProjOrderChangeBean> projOrderList = wrapper.parse(EmcProjOrderChangeBean.class);
        List<EmcProjSchemeChangeBean> projSchemeList = wrapper.parse(EmcProjSchemeChangeBean.class);

        // 获取该项目的分包单号,分包商ID
        List<Long> schemeIdList = projSchemeService.getDao().selectListByOneColumnValue(projSchemeService.selectById(projOrderList.get(0).getProjSchemeId()).getProjId(), "PROJID", Arrays.asList("ID"))
                .stream().map(e -> e.getId()).collect(Collectors.toList());
        List<EmcProjOrderChangeBean> subcontractorProjOrderList = getDao().selectListByOneColumnValues(schemeIdList, "PROJSCHEMEID", Arrays.asList("ID", "SUBCONTRACTORID", "SUBPACKAGENO")).stream()
                .filter(e -> !ObjectUtils.isEmpty(e.getSubcontractorId())).collect(Collectors.toList());
        String subPackageNo = "";
        if (!subcontractorProjOrderList.isEmpty()) {
            subPackageNo = subcontractorProjOrderList.get(0).getSubPackageNo();
        } else {
            subPackageNo = ApplicationContextHelper.getNextSequence(getDao().getTable());
        }
        Long subcontractorId = projOrderList.get(0).getSubcontractorId();

        // 通过分析项组名称来关联查询需要更新的测试
        List<String> itemGroupNameList = projOrderList.stream().map(e -> e.getItemGroup()).distinct().collect(Collectors.toList());
        List<Long> projSchemeIdList = projSchemeList.stream().map(e -> e.getId()).collect(Collectors.toList());
        List<EmcProjOrderChangeBean> projOrderOfSchemeList = getDao().selectListByOneColumnValues(projSchemeIdList, "projSchemeId");

        List<EmcProjOrderChangeBean> updateProjOrderList = new ArrayList<>();
        for (int i = 0; i < projOrderOfSchemeList.size(); i++) {
            EmcProjOrderChangeBean e = projOrderOfSchemeList.get(i);
            if (itemGroupNameList.contains(e.getItemGroup())) {
                EmcProjOrderChangeBean updateProjOrder = new EmcProjOrderChangeBean();
                updateProjOrder.setId(e.getId());
                updateProjOrder.setSubcontractorId(subcontractorId);
                updateProjOrder.setSubPackageNo(subPackageNo);
                updateProjOrderList.add(updateProjOrder);
            }
        }
        getDao().update(updateProjOrderList, "subPackageNo", "subcontractorId");
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void relevanceDeleteSubcontractor(RestJsonWrapperBean wrapper) {
        List<EmcProjOrderChangeBean> projOrderList = wrapper.parse(EmcProjOrderChangeBean.class);
        List<EmcProjSchemeChangeBean> projSchemeList = wrapper.parse(EmcProjSchemeChangeBean.class);

        // 通过分析项组名称来关联查询需要更新的测试
        List<String> itemGroupNameList = projOrderList.stream().map(e -> e.getItemGroup()).distinct().collect(Collectors.toList());
        List<Long> projSchemeIdList = projSchemeList.stream().map(e -> e.getId()).collect(Collectors.toList());
        List<EmcProjOrderChangeBean> projOrderOfSchemeList = getDao().selectListByOneColumnValues(projSchemeIdList, "projSchemeId");

        List<EmcProjOrderChangeBean> updateProjOrderList = new ArrayList<>();
        for (int i = 0; i < projOrderOfSchemeList.size(); i++) {
            EmcProjOrderChangeBean e = projOrderOfSchemeList.get(i);
            if (itemGroupNameList.contains(e.getItemGroup())) {
                EmcProjOrderChangeBean updateProjOrder = new EmcProjOrderChangeBean();
                updateProjOrder.setId(e.getId());
                updateProjOrder.setSubcontractorId(null);
                updateProjOrder.setSubPackageNo(null);
                updateProjOrderList.add(updateProjOrder);
            }
        }
        getDao().update(updateProjOrderList, "subPackageNo", "subcontractorId");
    }
}
