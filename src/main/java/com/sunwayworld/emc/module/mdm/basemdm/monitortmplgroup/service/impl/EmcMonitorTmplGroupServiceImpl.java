package com.sunwayworld.emc.module.mdm.basemdm.monitortmplgroup.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.basemdm.monitortmplgroup.bean.EmcMonitorTmplGroupBean;
import com.sunwayworld.emc.module.mdm.basemdm.monitortmplgroup.bean.EmcOrderDescBean;
import com.sunwayworld.emc.module.mdm.basemdm.monitortmplgroup.dao.EmcMonitorTmplGroupDao;
import com.sunwayworld.emc.module.mdm.basemdm.monitortmplgroup.service.EmcMonitorTmplGroupService;
import com.sunwayworld.emc.module.mdm.basemdm.monitortmplgroup.service.EmcOrderDescService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

/**
 * @author magh@sunwayworld.com 2019年9月6日
 */

@Repository
public class EmcMonitorTmplGroupServiceImpl implements EmcMonitorTmplGroupService {
    @Autowired
    private EmcMonitorTmplGroupDao emcMonitorTmplGroupDao;
    @Autowired
    private EmcOrderDescService orderDescService;

    @Override
    @SuppressWarnings("unchecked")
    public EmcMonitorTmplGroupDao getDao() {
        return emcMonitorTmplGroupDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        EmcMonitorTmplGroupBean emcMonitorTmplGroup = jsonWrapper.parseUnique(EmcMonitorTmplGroupBean.class);
        long id = ApplicationContextHelper.getNextSequenceNumber(getDao().getTable());
        emcMonitorTmplGroup.setId(id);
        getDao().insert(emcMonitorTmplGroup);
        return emcMonitorTmplGroup.getId();
    }

    @Override
    public Page<EmcOrderDescBean> selectOrderDescPaginationByMonitorTmplGroupId(Long id, RestJsonWrapperBean wrapper) {
        return orderDescService.selectPaginationByFilter(SearchFilter.instance().match("MONITORTMPLGROUPID", id).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    @Transactional
    public Long insertOrderDesc(Long id, RestJsonWrapperBean wrapper) {
        return orderDescService.insert(id, wrapper);
    }

    @Override
    @Transactional
    public void deleteOrderDesc(Long id, RestJsonWrapperBean wrapper) {
        orderDescService.delete(wrapper);
    }

    @Override
    @Transactional
    public void activateOrderDesc(RestJsonWrapperBean wrapper) {
        orderDescService.activate(wrapper);
    }

    @Override
    @Transactional
    public void deactivateOrderDesc(RestJsonWrapperBean wrapper) {
        orderDescService.deactivate(wrapper);
    }

}
