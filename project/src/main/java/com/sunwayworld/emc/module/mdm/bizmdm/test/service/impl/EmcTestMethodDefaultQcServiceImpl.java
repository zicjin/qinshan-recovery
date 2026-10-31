package com.sunwayworld.emc.module.mdm.bizmdm.test.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.bizmdm.capacity.bean.EmcTestCapacityBean;
import com.sunwayworld.emc.module.mdm.bizmdm.capacity.service.EmcTestCapacityService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcTestMethodDefaultQcBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.dao.EmcTestMethodDefaultQcDao;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcTestMethodDefaultQcService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.utils.ObjectUtils;

/**
* @author zhengxy@sunwayworld.com 2021年06月17日
*/
@Repository
public class EmcTestMethodDefaultQcServiceImpl implements EmcTestMethodDefaultQcService {

    @Autowired
    @Lazy
    private EmcTestCapacityService testCapacityService;

    @Autowired
    private EmcTestMethodDefaultQcDao emcTestMethodDefaultQcDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcTestMethodDefaultQcDao getDao() {
        return emcTestMethodDefaultQcDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean wrapperBean) {
        List<EmcTestMethodDefaultQcBean> defaultQcList = wrapperBean.parse(EmcTestMethodDefaultQcBean.class);
        defaultQcList.forEach(item -> item.setId(ApplicationContextHelper.getNextIdentity()));
        getDao().insert(defaultQcList);

        return defaultQcList.get(0).getId();
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void insert(Long id, RestJsonWrapperBean wrapper) {
        List<EmcTestMethodDefaultQcBean> defaultQcList = wrapper.parse(EmcTestMethodDefaultQcBean.class);
        List<EmcTestMethodDefaultQcBean> emptyDefaultQcList = defaultQcList.stream().filter(defaultQc -> ObjectUtils.isEmpty(defaultQc.getTestCapacityId())).collect(Collectors.toList());
        if (!emptyDefaultQcList.isEmpty()) {
            List<Long> testMethodIdList = emptyDefaultQcList.stream().map(EmcTestMethodDefaultQcBean::getTestMethodId).distinct().collect(Collectors.toList());
            String parentOrgId = LocalContextHelper.getLoginUser().getExt$Item("parentOrgId");
            List<EmcTestCapacityBean> testCapacityList = testCapacityService.selectListByFilter(SearchFilter.instance().match("testMethodId", testMethodIdList).filter(MatchPattern.OR).match("orgId", parentOrgId).filter(MatchPattern.SEQ));
            defaultQcList.stream().filter(defaultQc -> ObjectUtils.isEmpty(defaultQc.getTestCapacityId()))
                    .forEach(defaultQc -> testCapacityList.stream().filter(capacity -> ObjectUtils.equals(defaultQc.getTestMethodId(), capacity.getTestMethodId()))
                            .findFirst().ifPresent(capacity -> defaultQc.setTestCapacityId(capacity.getId())));
        }

        defaultQcList.forEach(item -> item.setId(ApplicationContextHelper.getNextIdentity()));
        getDao().insert(defaultQcList);
    }
}
