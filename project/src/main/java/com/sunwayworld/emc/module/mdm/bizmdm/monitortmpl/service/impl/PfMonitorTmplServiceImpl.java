package com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.sunwayworld.framework.utils.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.emc.module.mdm.bizmdm.capacity.bean.EmcCapacityItemGroupBean;
import com.sunwayworld.emc.module.mdm.bizmdm.capacity.bean.EmcTestCapacityBean;
import com.sunwayworld.emc.module.mdm.bizmdm.capacity.service.EmcCapacityItemGroupService;
import com.sunwayworld.emc.module.mdm.bizmdm.capacity.service.EmcTestCapacityService;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.bean.EmcMonitorSchemeBean;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.bean.EmcMonitorSchemeCostBean;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.bean.EmcMonitorSchemeSamplingCostBean;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.bean.EmcMonitorTmplBean;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.service.EmcMonitorSchemeCostService;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.service.EmcMonitorSchemeSamplingCostService;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.service.EmcMonitorSchemeService;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.service.PfMonitorTmplService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcTestMethodOrderCategoryBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcTestMethodOrderCategoryService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;

/**
 * @author zhengxy@sunwayworld.com 2021年10月19日
 */
@GikamBean
@Repository
public class PfMonitorTmplServiceImpl extends EmcMonitorTmplServiceImpl implements PfMonitorTmplService {

    @Autowired
    private EmcMonitorSchemeService monitorSchemeService;

    @Autowired
    private EmcMonitorSchemeCostService monitorSchemeCostService;

    @Autowired
    @Lazy
    private EmcCapacityItemGroupService capacityItemGroupService;

    @Autowired
    @Lazy
    private EmcTestCapacityService testCapacityService;

    @Autowired
    @Lazy
    private EmcTestMethodOrderCategoryService testMethodOrderCategoryService;

    @Autowired
    @Lazy
    private EmcMonitorSchemeSamplingCostService schemeSamplingCostService;

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void copy(RestJsonWrapperBean wrapper) {
        EmcMonitorTmplBean monitorTmpl = wrapper.parseUnique(EmcMonitorTmplBean.class);
        String oldMonitorTmplId = monitorTmpl.getId();
        String newMonitorTmplId = ApplicationContextHelper.getNextSequence(getDao().getTable());

        // 复制监测模板
        monitorTmpl.setId(newMonitorTmplId);
        getDao().insert(monitorTmpl);

        // 复制方案
        List<EmcMonitorSchemeBean> copyMonitorSchemeList = monitorSchemeService.getDao().selectListByOneColumnValue(oldMonitorTmplId, "monitorTmplId");
        copyMonitorSchemeList.forEach(e -> {
            e.setId(ApplicationContextHelper.getNextIdentity());
            e.setMonitorTmplId(newMonitorTmplId);
        });

        monitorSchemeService.getDao().insert(copyMonitorSchemeList);

        // 复制监测费用
        List<EmcMonitorSchemeCostBean> copiedCostList = monitorSchemeCostService.getDao().selectListByOneColumnValue(oldMonitorTmplId, "monitorTmplId");
        copiedCostList.forEach(cost -> {
            cost.setId(ApplicationContextHelper.getNextIdentity());
            cost.setMonitorTmplId(newMonitorTmplId);
        });

        monitorSchemeCostService.getDao().insert(copiedCostList);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void copySchemeTo(RestJsonWrapperBean wrapper) {
        List<EmcMonitorTmplBean> targetMonitorTmplList = wrapper.parse(EmcMonitorTmplBean.class);
        List<EmcMonitorSchemeBean> copiedMonitorSchemeList = wrapper.parse(EmcMonitorSchemeBean.class);

        List<String> monitorTmplIdList = targetMonitorTmplList.stream().map(EmcMonitorTmplBean::getId).collect(Collectors.toList());
        List<EmcMonitorSchemeCostBean> costList = monitorSchemeCostService.getDao().selectListByOneColumnValues(monitorTmplIdList, "monitorTmplId");
        //被复制的监测方案费用
        List<String> schemeNameList = copiedMonitorSchemeList.stream().map(EmcMonitorSchemeBean::getSchemeName).distinct().collect(Collectors.toList());
        String copiedMonitorTmplId = copiedMonitorSchemeList.get(0).getMonitorTmplId();

        List<EmcMonitorSchemeCostBean> copiedSchemeCostList = monitorSchemeCostService.getDao().selectListByOneColumnValue(copiedMonitorTmplId, "monitorTmplId");
        List<EmcMonitorSchemeCostBean> validCopiedSchemeCostList = copiedSchemeCostList.stream().filter(cost -> !StringUtils.isEmpty(cost.getSchemeName()) && !ObjectUtils.isEmpty(cost.getCost())).collect(Collectors.toList());
        Map<String, Double> schemeNameCostMap = validCopiedSchemeCostList.stream().collect(Collectors.toMap(EmcMonitorSchemeCostBean::getSchemeName, EmcMonitorSchemeCostBean::getCost, (v1, v2) -> v1));

        List<EmcMonitorSchemeBean> newSchemeList = new ArrayList<>();
        List<EmcMonitorSchemeCostBean> newSchemeCostList = new ArrayList<>();
        targetMonitorTmplList.forEach(tmpl -> {
            //复制到的监测费用
            List<EmcMonitorSchemeCostBean> tmplCostList = costList.stream().filter(cost -> StringUtils.equals(cost.getMonitorTmplId(), tmpl.getId())).collect(Collectors.toList());
            List<String> tmplSchemeNameList = tmplCostList.stream().map(EmcMonitorSchemeCostBean::getSchemeName).collect(Collectors.toList());
            //复制多的监测费用
            List<String> newSchemeNameList = schemeNameList.stream().filter(schemeName -> !tmplSchemeNameList.contains(schemeName)).collect(Collectors.toList());

            copiedMonitorSchemeList.stream().filter(scheme -> newSchemeNameList.contains(scheme.getSchemeName())).forEach(scheme -> {
                EmcMonitorSchemeBean newScheme = new EmcMonitorSchemeBean();
                BeanUtils.copyProperties(scheme, newScheme, EmcConstant.CREATED_COLUMNS);
                newScheme.setId(ApplicationContextHelper.getNextIdentity());
                newScheme.setMonitorTmplId(tmpl.getId());
                newSchemeList.add(newScheme);
            });

            newSchemeNameList.forEach(schemeName -> {
                if (schemeName.equals("default")) {
                    return;
                }
                EmcMonitorSchemeCostBean newCost = new EmcMonitorSchemeCostBean();
                newCost.setId(ApplicationContextHelper.getNextIdentity());
                newCost.setSchemeName(schemeName);
                newCost.setMonitorTmplId(tmpl.getId());
                newCost.setCost(schemeNameCostMap.get(schemeName));
                newSchemeCostList.add(newCost);
            });
        });

        monitorSchemeService.getDao().insert(newSchemeList);
        monitorSchemeCostService.getDao().insert(newSchemeCostList);
    }

    @Override
    public void enableScheme(RestJsonWrapperBean wrapper) {
        //考虑到用户可能未全选监测方案的情况
        List<EmcMonitorSchemeBean> selectSchemeList = wrapper.parse(EmcMonitorSchemeBean.class);
        List<String> monitorTmplIdList = selectSchemeList.stream().map(EmcMonitorSchemeBean::getMonitorTmplId).distinct().collect(Collectors.toList());
        List<String> schemeNameList = selectSchemeList.stream().map(EmcMonitorSchemeBean::getSchemeName).distinct().collect(Collectors.toList());
        List<EmcMonitorSchemeBean> schemeBeforeList = monitorSchemeService.getDao().selectListByOneColumnValues(monitorTmplIdList, "monitorTmplId");
        List<EmcMonitorSchemeBean> schemeAfterList = schemeBeforeList.stream().filter(sc -> schemeNameList.contains(sc.getSchemeName())).collect(Collectors.toList());
        schemeAfterList.forEach(sc -> {
            sc.setExt$Item("enableflag", "1");
        });
        List<EmcMonitorSchemeCostBean> schemeCostBeforeList = monitorSchemeCostService.getDao().selectListByOneColumnValues(monitorTmplIdList, "monitorTmplId");
        List<EmcMonitorSchemeCostBean> schemeCostAfterList = schemeCostBeforeList.stream().filter(sc -> schemeNameList.contains(sc.getSchemeName())).collect(Collectors.toList());
        schemeCostAfterList.forEach(sc -> {
            sc.setExt$Item("isEnable", "1");
        });
        monitorSchemeCostService.getDao().update(schemeCostAfterList, "isEnable");
        monitorSchemeService.getDao().update(schemeAfterList, "enableflag");
    }

    @Override
    public void disableScheme(RestJsonWrapperBean wrapper) {
        //考虑到用户可能未全选监测方案的情况
        List<EmcMonitorSchemeBean> selectSchemeList = wrapper.parse(EmcMonitorSchemeBean.class);
        List<String> monitorTmplIdList = selectSchemeList.stream().map(EmcMonitorSchemeBean::getMonitorTmplId).distinct().collect(Collectors.toList());
        List<String> schemeNameList = selectSchemeList.stream().map(EmcMonitorSchemeBean::getSchemeName).distinct().collect(Collectors.toList());
        List<EmcMonitorSchemeBean> schemeBeforeList = monitorSchemeService.getDao().selectListByOneColumnValues(monitorTmplIdList, "monitorTmplId");
        List<EmcMonitorSchemeBean> schemeAfterList = schemeBeforeList.stream().filter(sc -> schemeNameList.contains(sc.getSchemeName())).collect(Collectors.toList());
        schemeAfterList.forEach(sc -> {
            sc.setExt$Item("enableflag", "0");
        });
        List<EmcMonitorSchemeCostBean> schemeCostBeforeList = monitorSchemeCostService.getDao().selectListByOneColumnValues(monitorTmplIdList, "monitorTmplId");
        List<EmcMonitorSchemeCostBean> schemeCostAfterList = schemeCostBeforeList.stream().filter(sc -> schemeNameList.contains(sc.getSchemeName())).collect(Collectors.toList());
        schemeCostAfterList.forEach(sc -> {
            sc.setExt$Item("isEnable", "0");
        });
        monitorSchemeCostService.getDao().update(schemeCostAfterList, "isEnable");

        monitorSchemeService.getDao().update(schemeAfterList, "enableflag");
    }

    @Override
    public void copyAllMonitorToOrg(RestJsonWrapperBean wrapper) {
        //选择的要复制到数据的公司
        String orgId = wrapper.getFilterValue("orgId");
        CoreUserBean user = LocalContextHelper.getLoginUser();
        String parentOrgId = user.getExt$Item("parentorgid");
        //用户当前登录的公司的监测模板作为母数据
        List<EmcMonitorTmplBean> copyMonitorTempList = selectListByFilter(SearchFilter.instance().match("orgId", parentOrgId).filter(MatchPattern.OR));
        List<EmcMonitorTmplBean> isExistMonitorTempList = selectListByFilter(SearchFilter.instance().match("orgId", orgId).filter(MatchPattern.OR));
        if (!isExistMonitorTempList.isEmpty()) {
            return;
        }
        //监测方案及监测方案费用表都是用MONITORTMPLID关联
        List<String> monitorTmplIdList = copyMonitorTempList.stream().map(EmcMonitorTmplBean::getId).collect(Collectors.toList());
        List<EmcMonitorSchemeBean> monitorSchemeList = monitorSchemeService.getDao().selectListByOneColumnValues(monitorTmplIdList, "monitorTmplId");
        List<EmcMonitorSchemeCostBean> monitorSchemeCostList = monitorSchemeCostService.getDao().selectListByOneColumnValues(monitorTmplIdList, "monitorTmplId");
        //监测方案的分析项组id为监测能力库的监测分析项组id
        //复制到得公司必须先复制监测能力库
        List<EmcTestCapacityBean> copyTestCapacityList = testCapacityService.getDao().selectListByOneColumnValue(orgId, "orgId");
        if (copyTestCapacityList.isEmpty()) {
            return;
        }
        //监测能力库
        List<Long> testCapacityIdList = copyTestCapacityList.stream().map(EmcTestCapacityBean::getId).distinct().collect(Collectors.toList());
        //监测分析项组
        List<EmcCapacityItemGroupBean> capacityItemGroupList = capacityItemGroupService.getDao().selectListByOneColumnValues(testCapacityIdList, "testCapacityId");
        //测试样品类型表需要复制新的模板id
        List<EmcTestMethodOrderCategoryBean> beforeTestMethodOrderCategoryList = testMethodOrderCategoryService.getDao().selectListByOneColumnValues(monitorTmplIdList, "monitorTmplId");
        List<EmcTestMethodOrderCategoryBean> testMethodOrderCategoryList = beforeTestMethodOrderCategoryList.stream().filter(oc -> StringUtils.equals(parentOrgId, oc.getExt$Item("orgId"))).collect(Collectors.toList());

        List<EmcMonitorTmplBean> addMonitorTempList = new ArrayList<>();
        List<EmcMonitorSchemeBean> addMonitorSchemeList = new ArrayList<>();
        List<EmcMonitorSchemeCostBean> addMonitorSchemeCostList = new ArrayList<>();
        List<EmcTestMethodOrderCategoryBean> addTestMethodOrderCategoryList = new ArrayList<>();
        copyMonitorTempList.forEach(temp -> {
            //未更新前id
            //String oldId = temp.getId();
            temp.setExt$Item("orgId", orgId);
            temp.setCreatedById(LocalContextHelper.getLoginUserId());
            temp.setCreatedByName(LocalContextHelper.getLoginUserName());
            temp.setCreatedByOrgId(LocalContextHelper.getLoginOrgId());
            temp.setCreatedByOrgName(LocalContextHelper.getLoginOrgName());
            temp.setCreatedTime(LocalDateTime.now());
            addMonitorTempList.add(temp);
            //先取老的id
            List<EmcMonitorSchemeBean> monitorSchemeForSingleTmplList = monitorSchemeList.stream().filter(sc -> StringUtils.equals(temp.getId(), sc.getMonitorTmplId())).collect(Collectors.toList());
            List<EmcMonitorSchemeCostBean> monitorSchemeCostForSingleTmplList = monitorSchemeCostList.stream().filter(msc -> StringUtils.equals(temp.getId(), msc.getMonitorTmplId())).collect(Collectors.toList());
            List<EmcTestMethodOrderCategoryBean> updateTestMethodOrderCategoryList = testMethodOrderCategoryList.stream().filter(oc -> StringUtils.equals(temp.getId(), oc.getMonitorTmplId())).collect(Collectors.toList());
            //再更新id
            temp.setId(ApplicationContextHelper.getNextSequence(getDao().getTable()));

            if (!monitorSchemeForSingleTmplList.isEmpty()) {
                monitorSchemeForSingleTmplList.forEach(sch -> {
                    sch.setId(ApplicationContextHelper.getNextIdentity());
                    sch.setMonitorTmplId(temp.getId());
                    sch.setCreatedById(LocalContextHelper.getLoginUserId());
                    sch.setCreatedByName(LocalContextHelper.getLoginUserName());
                    sch.setCreatedByOrgId(LocalContextHelper.getLoginOrgId());
                    sch.setCreatedByOrgName(LocalContextHelper.getLoginOrgName());
                    sch.setCreatedTime(LocalDateTime.now());
                    //复制到得公司得监测能力库相同测试相同方法相同分析项组的数据
                    List<EmcCapacityItemGroupBean> capacityItemGroupForSchemeList = capacityItemGroupList.stream().filter(ig -> ObjectUtils.equals(sch.getTestMethodId(), ig.getTestMethodId()))
                            .filter(ig -> ObjectUtils.equals(sch.getTestId(), ig.getTestId())).filter(ig -> StringUtils.equals(sch.getItemGroup(), ig.getItemGroup())).collect(Collectors.toList());
                    if (!capacityItemGroupForSchemeList.isEmpty()) {
                        sch.setItemGroupId(capacityItemGroupForSchemeList.get(0).getId());
                    }
                });
            }
            addMonitorSchemeList.addAll(monitorSchemeForSingleTmplList);
            if (!monitorSchemeCostForSingleTmplList.isEmpty()) {
                monitorSchemeCostForSingleTmplList.forEach(cost -> {
                    cost.setId(ApplicationContextHelper.getNextIdentity());
                    cost.setMonitorTmplId(temp.getId());
                    cost.setCreatedById(LocalContextHelper.getLoginUserId());
                    cost.setCreatedByName(LocalContextHelper.getLoginUserName());
                    cost.setCreatedByOrgId(LocalContextHelper.getLoginOrgId());
                    cost.setCreatedByOrgName(LocalContextHelper.getLoginOrgName());
                    cost.setCreatedTime(LocalDateTime.now());
                });
            }
            addMonitorSchemeCostList.addAll(monitorSchemeCostForSingleTmplList);
            //根据老的id找到样品类型表的数据查出来list，更改新的模板id，插进样品类型表里面，可以获得新机构的
            if (!updateTestMethodOrderCategoryList.isEmpty()) {
                updateTestMethodOrderCategoryList.forEach(oc -> {
                    oc.setId(ApplicationContextHelper.getNextIdentity());
                    //给新模板id
                    oc.setMonitorTmplId(temp.getId());
                    oc.setCreatedById(LocalContextHelper.getLoginUserId());
                    oc.setCreatedByName(LocalContextHelper.getLoginUserName());
                    oc.setCreatedByOrgId(LocalContextHelper.getLoginOrgId());
                    oc.setCreatedByOrgName(LocalContextHelper.getLoginOrgName());
                    oc.setCreatedTime(LocalDateTime.now());
                    oc.setExt$Item("orgId", orgId);
                });
            }
            addTestMethodOrderCategoryList.addAll(updateTestMethodOrderCategoryList);

        });
        getDao().insert(addMonitorTempList);
        monitorSchemeService.getDao().insert(addMonitorSchemeList);
        monitorSchemeCostService.getDao().insert(addMonitorSchemeCostList);
        testMethodOrderCategoryService.getDao().insert(addTestMethodOrderCategoryList);
    }

    @Override
    public void editSchemesName(RestJsonWrapperBean wrapper) {
        //修改监测方案表和监测方案费用表
        String newSchemeName = wrapper.getFilterValue("schemeName");
        //监测能力库就更新名字
        List<EmcMonitorSchemeCostBean> schemeCostList = wrapper.parse(EmcMonitorSchemeCostBean.class);
        List<Long> schemeCostIdList = schemeCostList.stream().map(EmcMonitorSchemeCostBean::getId).collect(Collectors.toList());
        List<EmcMonitorSchemeCostBean> updateSchemeCostList = monitorSchemeCostService.getDao().selectListByIds(schemeCostIdList);
        String monitorTmplId = updateSchemeCostList.get(0).getMonitorTmplId();
        if (monitorTmplId != null && monitorTmplId != "") {
            //根据监测方案费用表找到所有含有这个名称的
            String schemeName = updateSchemeCostList.get(0).getSchemeName();
            List<EmcMonitorSchemeBean> schemeList = monitorSchemeService.getDao().selectListByOneColumnValue(schemeName, "schemeName");
            List<EmcMonitorSchemeBean> updateSchemeList = schemeList.stream().filter(sch -> StringUtils.equals(monitorTmplId, sch.getMonitorTmplId())).collect(Collectors.toList());
            updateSchemeCostList.forEach(schc -> {
                schc.setSchemeName(newSchemeName);
            });
            //这里根据id更新没问题
            updateSchemeList.forEach(sch -> {
                sch.setSchemeName(newSchemeName);
            });
            monitorSchemeCostService.getDao().update(updateSchemeCostList, "schemeName");
            monitorSchemeService.getDao().update(updateSchemeList, "schemeName");

        }

    }

    @Override
    public Page<EmcMonitorSchemeSamplingCostBean> selectSchemeSamplingCostPagination(RestJsonWrapperBean wrapper) {
        return schemeSamplingCostService.selectPagination(wrapper);
    }

    @Override
    public void insertSamplingCost(RestJsonWrapperBean wrapper) {
        schemeSamplingCostService.insert(wrapper);
    }

    @Override
    public void deleteSamplingCost(RestJsonWrapperBean wrapper) {
        schemeSamplingCostService.delete(wrapper);
    }

    @Override
    public void syncPriceInfoTo(RestJsonWrapperBean wrapper) {
        List<EmcMonitorSchemeBean> selectedSchemeList = wrapper.parse(EmcMonitorSchemeBean.class);
        List<EmcMonitorTmplBean> targetMonitorTmplList = wrapper.parse(EmcMonitorTmplBean.class);

        List<Long> selectedItemGroupIdList = selectedSchemeList.stream().filter(scheme -> StringUtils.equals(scheme.getSchemeName(), "default")).map(EmcMonitorSchemeBean::getItemGroupId).distinct().collect(Collectors.toList());
        List<String> targetMonitorTmplIdList = targetMonitorTmplList.stream().map(EmcMonitorTmplBean::getId).collect(Collectors.toList());
        List<EmcMonitorSchemeBean> targetSchemeList = monitorSchemeService.selectListByFilter(SearchFilter.instance().match("schemeName", "default").filter(MatchPattern.SEQ).match("monitorTmplId", targetMonitorTmplIdList).filter(MatchPattern.OR).match("itemGroupId", selectedItemGroupIdList).filter(MatchPattern.OR));

        List<Long> selectedSchemeIdList = selectedSchemeList.stream().map(EmcMonitorSchemeBean::getId).collect(Collectors.toList());
        List<EmcMonitorSchemeSamplingCostBean> samplingCostList = schemeSamplingCostService.getDao().selectListByOneColumnValues(selectedSchemeIdList, "monitorSchemeId");

        List<EmcMonitorSchemeSamplingCostBean> newSamplingCostList = new ArrayList<>();
        List<EmcMonitorSchemeBean> updateSchemeList = new ArrayList<>();

        selectedSchemeList.forEach(selectedScheme -> {
            List<EmcMonitorSchemeBean> curTargetSchemeList = targetSchemeList.stream().filter(ts -> ObjectUtils.equals(ts.getItemGroupId(), selectedScheme.getItemGroupId())).collect(Collectors.toList());
            List<EmcMonitorSchemeSamplingCostBean> curSamplingCostList = samplingCostList.stream().filter(sc -> ObjectUtils.equals(sc.getMonitorSchemeId(), selectedScheme.getId())).collect(Collectors.toList());

            curTargetSchemeList.forEach(cts -> {
                cts.setPeriod(selectedScheme.getPeriod());
                cts.setSalePrice(selectedScheme.getSalePrice());
                cts.setTechPrice(selectedScheme.getTechPrice());

                curSamplingCostList.forEach(sc -> {
                    EmcMonitorSchemeSamplingCostBean newSamplingCost = new EmcMonitorSchemeSamplingCostBean();
                    BeanUtils.copyProperties(sc, newSamplingCost, EmcConstant.CREATED_COLUMNS);
                    newSamplingCost.setId(ApplicationContextHelper.getNextIdentity());
                    newSamplingCost.setMonitorSchemeId(cts.getId());
                    newSamplingCost.setMonitorTmplId(cts.getMonitorTmplId());
                    newSamplingCostList.add(newSamplingCost);
                });
            });
            updateSchemeList.addAll(curTargetSchemeList);
        });

        monitorSchemeService.getDao().update(updateSchemeList, "period", "salePrice", "techPrice");
        schemeSamplingCostService.getDao().insert(newSamplingCostList);
    }

    @Override
    public Long copyToSamplingCost(RestJsonWrapperBean wrapper) {
        List<EmcMonitorSchemeBean> selectedSchemeList = wrapper.parse(EmcMonitorSchemeBean.class,0);
        List<EmcMonitorSchemeSamplingCostBean> monitorSchemeSamplingCostList = wrapper.parse(EmcMonitorSchemeSamplingCostBean.class,1);

        List<EmcMonitorSchemeSamplingCostBean> newSamplingCostList = new ArrayList<>();

        if (!CollectionUtils.isEmpty(selectedSchemeList) && !CollectionUtils.isEmpty(monitorSchemeSamplingCostList)) {
            selectedSchemeList.forEach(cts -> monitorSchemeSamplingCostList.forEach(sc -> {
                EmcMonitorSchemeSamplingCostBean newSamplingCost = new EmcMonitorSchemeSamplingCostBean();
                BeanUtils.copyProperties(sc, newSamplingCost, EmcConstant.CREATED_COLUMNS);
                newSamplingCost.setId(ApplicationContextHelper.getNextIdentity());
                newSamplingCost.setMonitorSchemeId(cts.getId());
                newSamplingCost.setMonitorTmplId(cts.getMonitorTmplId());
                newSamplingCostList.add(newSamplingCost);
            }));
        }
        schemeSamplingCostService.getDao().insert(newSamplingCostList);
        return 1L;
    }
}
