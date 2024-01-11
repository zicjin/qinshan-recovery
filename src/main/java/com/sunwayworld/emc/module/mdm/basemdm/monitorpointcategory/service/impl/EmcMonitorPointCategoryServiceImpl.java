package com.sunwayworld.emc.module.mdm.basemdm.monitorpointcategory.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.basemdm.monitorpointcategory.bean.EmcMonitorPointCategoryBean;
import com.sunwayworld.emc.module.mdm.basemdm.monitorpointcategory.bean.EmcMpcTmplBean;
import com.sunwayworld.emc.module.mdm.basemdm.monitorpointcategory.dao.EmcMonitorPointCategoryDao;
import com.sunwayworld.emc.module.mdm.basemdm.monitorpointcategory.service.EmcMonitorPointCategoryService;
import com.sunwayworld.emc.module.mdm.basemdm.monitorpointcategory.service.EmcMpcTmplService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.utils.ObjectUtils;

/**
 * @author magh@sunwayworld.com
 * @date 2019-09-10
 */

@Repository
public class EmcMonitorPointCategoryServiceImpl implements EmcMonitorPointCategoryService {
    @Autowired
    private EmcMonitorPointCategoryDao emcMonitorPointCategoryDao;
    @Autowired
    private EmcMpcTmplService mpcTmplService;

    @Override
    @SuppressWarnings("unchecked")
    public EmcMonitorPointCategoryDao getDao() {
        return emcMonitorPointCategoryDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public String insert(RestJsonWrapperBean jsonWrapper) {
        EmcMonitorPointCategoryBean emcMonitorPointCategory = jsonWrapper.parseUnique(EmcMonitorPointCategoryBean.class);
        emcMonitorPointCategory.setId(ApplicationContextHelper.getNextSequence(getDao().getTable()));
        emcMonitorPointCategory.setOrgId(LocalContextHelper.getLoginUser().getExt$Item("parentOrgId"));
        getDao().insert(emcMonitorPointCategory);
        return emcMonitorPointCategory.getId();
    }

    @Override
    public Page<EmcMpcTmplBean> selectTmplPaginationByMpcId(String id, RestJsonWrapperBean wrapper) {
        return mpcTmplService.selectPaginationByFilter(SearchFilter.instance().match("MONITORPOINTCATEGORYID", id).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    public Long insertTmpl(String id, RestJsonWrapperBean wrapper) {
        return mpcTmplService.insert(id, wrapper);
    }

    @Override
    public void deleteTmpl(String id, RestJsonWrapperBean wrapper) {
        mpcTmplService.delete(wrapper);
    }

    @Override
    public List<EmcMonitorPointCategoryBean> selectFilterMonitorTmplGroupSelectableList() {
        List<EmcMonitorPointCategoryBean> pointCategoryList = this.selectSelectableList();
        List<EmcMonitorPointCategoryBean> pointCategoryNotEmptyList = pointCategoryList.stream().filter(f->!ObjectUtils.isEmpty(f.getMonitorTmplGroupId())).collect(Collectors.toList());
        return new ArrayList<>(pointCategoryNotEmptyList.stream().collect(Collectors.groupingBy(v -> v.getMonitorTmplGroupId(), Collectors.collectingAndThen(Collectors.toList(), v -> v.get(0)))).values());
    }

    @Override
    public List<EmcMpcTmplBean> selectTmplSelectableList() {
        List<EmcMpcTmplBean> mpcTmplList = mpcTmplService.selectSelectableList();
        return new ArrayList<>(mpcTmplList.stream().collect(Collectors.groupingBy(v -> v.getMonitorTmplId(), Collectors.collectingAndThen(Collectors.toList(), v -> v.get(0)))).values());
    }
}
