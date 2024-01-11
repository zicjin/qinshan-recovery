package com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.service.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.bean.EmcMonitorSchemeBean;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.bean.EmcMonitorTmplBean;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.dao.EmcMonitorTmplDao;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.service.EmcMonitorSchemeService;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.service.EmcMonitorTmplService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author magh@sunwayworld.com
 * @date 2019-09-19
 */

@Repository
@GikamBean
public class EmcMonitorTmplServiceImpl implements EmcMonitorTmplService {
    @Autowired
    private EmcMonitorTmplDao emcMonitorTmplDao;

    @Autowired
    private EmcMonitorSchemeService monitorSchemeService;

    @Override
    @SuppressWarnings("unchecked")
    public EmcMonitorTmplDao getDao() {
        return emcMonitorTmplDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public String insert(RestJsonWrapperBean jsonWrapper) {
        EmcMonitorTmplBean emcMonitorTmpl = jsonWrapper.parseUnique(EmcMonitorTmplBean.class);
        emcMonitorTmpl.setId(ApplicationContextHelper.getNextSequence(getDao().getTable()));
        CoreUserBean user = LocalContextHelper.getLoginUser();
        emcMonitorTmpl.setExt$Item("ORGID",user.getExt$Item("parentorgid"));
        emcMonitorTmpl.setExt$Item("ORGNAME",user.getExt$Item("parentorgname"));
        getDao().insert(emcMonitorTmpl);
        return emcMonitorTmpl.getId();
    }

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
        EmcMonitorSchemeBean schemeFilter = new EmcMonitorSchemeBean();
        schemeFilter.setMonitorTmplId(oldMonitorTmplId);

        List<EmcMonitorSchemeBean> copyMonitorSchemeList = monitorSchemeService.selectList(schemeFilter);

        List<EmcMonitorSchemeBean> monitorSchemeList = new ArrayList<EmcMonitorSchemeBean>();

        // 复制分析项组
        copyMonitorSchemeList.stream().forEach(e -> {
            e.setId(ApplicationContextHelper.getNextIdentity());
            e.setMonitorTmplId(newMonitorTmplId);
            monitorSchemeList.add(e);
        });

        monitorSchemeService.getDao().insert(monitorSchemeList);

    }

    @Override
    public Page<EmcMonitorSchemeBean> selectSchemeChoosablePagination(RestJsonWrapperBean wrapper) {
        return monitorSchemeService.selectChoosablePagination(wrapper);
    }

    @Override
    public Page<EmcMonitorSchemeBean> selectSchemeDetectionChoosablePagination(RestJsonWrapperBean wrapper) {
        return monitorSchemeService.selectDetectionChoosablePagination(wrapper);
    }

    @Override
    public Page<EmcMonitorSchemeBean> selectSchemeItemGroupChoosablePagination(RestJsonWrapperBean wrapper) {
        MapperParameter parameter = wrapper.extractMapFilter();

        List<EmcMonitorSchemeBean> monitorSchemeList = monitorSchemeService.selectList(parameter).stream()
                .collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(o -> o.getTestId() + ";" + o.getItemGroup()))), ArrayList::new));

        return new Pagination<>(monitorSchemeList);

    }
    
    @Override
    public Page<EmcMonitorSchemeBean> selectDetectionChoosablePaginationDeWeight(RestJsonWrapperBean wrapper) {
        return monitorSchemeService.selectDetectionChoosablePaginationDeWeight(wrapper);
    }
}
