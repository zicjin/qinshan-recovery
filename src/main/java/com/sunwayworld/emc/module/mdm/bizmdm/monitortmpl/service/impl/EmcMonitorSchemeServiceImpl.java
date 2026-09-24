package com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.exception.InvalidDataException;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.mybatis.page.PageRowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.bean.EmcMonitorSchemeBean;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.dao.EmcMonitorSchemeDao;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.service.EmcMonitorSchemeService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcItemGroupService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;

/**
 * @author magh@sunwayworld.com
 * @date 2019-09-19
 */

@Repository
@GikamBean
public class EmcMonitorSchemeServiceImpl implements EmcMonitorSchemeService {
    @Autowired
    private EmcMonitorSchemeDao emcMonitorSchemeDao;

    @Autowired
    @Lazy
    private EmcItemGroupService itemGroupService;

    @Override
    @SuppressWarnings("unchecked")
    public EmcMonitorSchemeDao getDao() {
        return emcMonitorSchemeDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(String tmplId, RestJsonWrapperBean jsonWrapper) {
        List<EmcMonitorSchemeBean> monitorSchemeList = jsonWrapper.parse(EmcMonitorSchemeBean.class);

        String schemeNameUnique = jsonWrapper.getParamValue("schemeNameUnique");
        if (!StringUtils.isBlank(schemeNameUnique)) {
            EmcMonitorSchemeBean filterScheme = new EmcMonitorSchemeBean();
            filterScheme.setMonitorTmplId(monitorSchemeList.get(0).getMonitorTmplId());
            filterScheme.setSchemeName(monitorSchemeList.get(0).getSchemeName());
            List<EmcMonitorSchemeBean> repeatSchemeList = getDao().selectList(filterScheme, Arrays.asList("MONITORTMPLID", "SCHEMENAME"), Arrays.asList("ID"));
            if (!repeatSchemeList.isEmpty()) {
                throw new InvalidDataException("监测方案名称重复");
            }
        }

        monitorSchemeList.forEach(e -> {
            e.setId(ApplicationContextHelper.getNextIdentity());
            e.setMonitorTmplId(tmplId);
            if (ObjectUtils.isEmpty(e.getExt$Item("defaultflag"))) {
                e.setExt$Item("defaultflag", "1");
            }
            e.setExt$Item("enableflag", "1");
        });

        getDao().insert(monitorSchemeList);
        return monitorSchemeList.get(0).getId();

    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void edit(String tmplId, String schemeName, RestJsonWrapperBean wrapper) {
        EmcMonitorSchemeBean emcMonitorSchemeBean = new EmcMonitorSchemeBean();
        emcMonitorSchemeBean.setMonitorTmplId(tmplId);
        emcMonitorSchemeBean.setSchemeName(schemeName);

        getDao().deleteBy(emcMonitorSchemeBean, "monitorTmplId", "schemeName"); // 删除已存在的

        insert(tmplId, wrapper);
    }

    @Override
    public void addSchemeTest(String id, String schemeName, RestJsonWrapperBean wrapper) {
        List<EmcMonitorSchemeBean> monitorSchemeList = wrapper.parse(EmcMonitorSchemeBean.class);
        monitorSchemeList.forEach(scheme -> {
            scheme.setId(ApplicationContextHelper.getNextIdentity());
            scheme.setSchemeName(schemeName);
            scheme.setMonitorTmplId(id);
        });

        getDao().insert(monitorSchemeList);
    }

    @Override
    public void deleteSchemeTest(String id, String schemeName, RestJsonWrapperBean wrapper) {
        List<EmcMonitorSchemeBean> monitorSchemeList = wrapper.parse(EmcMonitorSchemeBean.class);
        monitorSchemeList.forEach(scheme -> {
            scheme.setMonitorTmplId(id);
            scheme.setSchemeName(schemeName);
        });
        getDao().deleteBy(monitorSchemeList, "SCHEMENAME", "MONITORTMPLID", "ITEMGROUPID");
    }

    @Override
    public Page<EmcMonitorSchemeBean> selectDetectionChoosablePagination(RestJsonWrapperBean wrapper) {
        MapperParameter parameter = wrapper.extractMapFilter();
        PageRowBounds rowBounds = wrapper.extractPageRowBounds();

        return selectPagination(() -> getDao().selectDetectionChoosablePagination(parameter), rowBounds);
    }

    @Override
    public List<EmcMonitorSchemeBean> selectSelectableList() {
        List<EmcMonitorSchemeBean> monitorSchemeList = EmcMonitorSchemeService.super.selectSelectableList();

        monitorSchemeList = monitorSchemeList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(EmcMonitorSchemeBean::getSchemeName))), ArrayList::new));

        return monitorSchemeList;
    }
    
    @Override
    public Page<EmcMonitorSchemeBean> selectDetectionChoosablePaginationDeWeight(RestJsonWrapperBean wrapper) {
        MapperParameter parameter = wrapper.extractMapFilter();
        PageRowBounds rowBounds = wrapper.extractPageRowBounds();

        return selectPagination(() -> getDao().selectDetectionChoosablePaginationDeWeight(parameter), rowBounds);
    }

    @Override
    public Page<EmcMonitorSchemeBean> selectPaginationGroupByTestMethod(RestJsonWrapperBean wrapper) {
        MapperParameter parameter = wrapper.extractMapFilter();
        PageRowBounds rowBounds = wrapper.extractPageRowBounds();

        return selectPagination(() -> getDao().selectByConditionGroupByTestMethod(parameter), rowBounds);
    }

}
