package com.sunwayworld.emc.module.mdm.basemdm.traincourse.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.basemdm.traincourse.bean.EmcTrainCourseBean;
import com.sunwayworld.emc.module.mdm.basemdm.traincourse.dao.EmcTrainCourseDao;
import com.sunwayworld.emc.module.mdm.basemdm.traincourse.mapper.EmcTrainCourseMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;

/**
 * @author magh@sunwayworld.com
 * @date 2019-10-28
 */
@Repository
public class EmcTrainCourseDaoImpl extends MybatisDaoSupport<EmcTrainCourseBean, Long> implements EmcTrainCourseDao {

    @Autowired
    private EmcTrainCourseMapper emcTrainCourseMapper;

    @Override
    public EmcTrainCourseMapper getMapper() {
        return emcTrainCourseMapper;
    }

}
