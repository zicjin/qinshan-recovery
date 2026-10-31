package com.sunwayworld.emc.module.mdm.basemdm.traincourse.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.basemdm.traincourse.bean.EmcTcTestMethodBean;
import com.sunwayworld.emc.module.mdm.basemdm.traincourse.bean.EmcTrainCourseBean;
import com.sunwayworld.emc.module.mdm.basemdm.traincourse.dao.EmcTrainCourseDao;
import com.sunwayworld.emc.module.mdm.basemdm.traincourse.service.EmcTcTestMethodService;
import com.sunwayworld.emc.module.mdm.basemdm.traincourse.service.EmcTrainCourseService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author magh@sunwayworld.com
 * @date 2019-10-28
 */
@GikamBean
@Repository
public class EmcTrainCourseServiceImpl implements EmcTrainCourseService {
    @Autowired
    private EmcTrainCourseDao emcTrainCourseDao;

    @Autowired
    private EmcTcTestMethodService emcTcTestMethodService;

    @Override
    @SuppressWarnings("unchecked")
    public EmcTrainCourseDao getDao() {
        return emcTrainCourseDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        EmcTrainCourseBean emcTrainCourse = jsonWrapper.parseUnique(EmcTrainCourseBean.class);
        emcTrainCourse.setId(ApplicationContextHelper.getNextIdentity());
        emcTrainCourse.setCourseCode(ApplicationContextHelper.getNextSequence(getDao().getTable()));
        getDao().insert(emcTrainCourse);
        return emcTrainCourse.getId();
    }

    @Override
    public Long insertTestMethod(Long id, RestJsonWrapperBean wrapper) {
        return emcTcTestMethodService.insert(id, wrapper);
    }

    @Override
    public Page<EmcTcTestMethodBean> selectTcTestMethodRawPagination(Long id, RestJsonWrapperBean wrapper) {
        return emcTcTestMethodService.selectPaginationByFilter(SearchFilter.instance().match("COURSEID", id).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    public void deleteTestMethod(Long id, RestJsonWrapperBean wrapper) {
        emcTcTestMethodService.delete(wrapper);
    }

}
