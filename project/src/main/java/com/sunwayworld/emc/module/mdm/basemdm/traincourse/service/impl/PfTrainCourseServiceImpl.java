package com.sunwayworld.emc.module.mdm.basemdm.traincourse.service.impl;


import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.basemdm.traincourse.bean.EmcTrainCourseBean;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;

/**
 * @author licm@sunwayworld.com 2021年10月08日
 */
@Repository
@GikamBean
public class PfTrainCourseServiceImpl extends EmcTrainCourseServiceImpl {

	
    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        EmcTrainCourseBean emcTrainCourse = jsonWrapper.parseUnique(EmcTrainCourseBean.class);
        emcTrainCourse.setId(ApplicationContextHelper.getNextIdentity());
        emcTrainCourse.setCourseCode(ApplicationContextHelper.getNextSequence(getDao().getTable()));
        //赋值所属单位信息
        CoreUserBean user = new CoreUserBean();
        user = LocalContextHelper.getLoginUser();
        String parentOrgId = user.getExt$Item("parentorgid");
        emcTrainCourse.setExt$Item("orgid", parentOrgId);
        getDao().insert(emcTrainCourse);
        return emcTrainCourse.getId();
    }
	
}