package com.sunwayworld.emc.module.mdm.basemdm.traincourse.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.mdm.basemdm.traincourse.bean.EmcTcTestMethodBean;
import com.sunwayworld.emc.module.mdm.basemdm.traincourse.bean.EmcTrainCourseBean;
import com.sunwayworld.emc.module.mdm.basemdm.traincourse.service.EmcTrainCourseService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericResource;

/**
 * @author magh@sunwayworld.com
 * @date 2019-10-28
 */
@RequestMapping("/secure/emc/module/mdm/basemdm/train-courses")
public interface EmcTrainCourseResource extends GenericResource<EmcTrainCourseService, EmcTrainCourseBean, Long> {

    Long insertTestMethod(Long id, RestJsonWrapperBean wrapper);

    Page<EmcTcTestMethodBean> selectTcTestMethodRawPagination(Long id, RestJsonWrapperBean wrapper);

    void deleteTestMethod(Long id, RestJsonWrapperBean wrapper);
}
