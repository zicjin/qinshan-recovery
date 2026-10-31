package com.sunwayworld.emc.module.mdm.basemdm.traincourse.service;

import com.sunwayworld.emc.module.mdm.basemdm.traincourse.bean.EmcTcTestMethodBean;
import com.sunwayworld.emc.module.mdm.basemdm.traincourse.bean.EmcTrainCourseBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

/**
 * @author magh@sunwayworld.com
 * @date 2019-10-28
 */

public interface EmcTrainCourseService extends GenericService<EmcTrainCourseBean, Long> {
    Long insertTestMethod(Long id, RestJsonWrapperBean wrapper);

    Page<EmcTcTestMethodBean> selectTcTestMethodRawPagination(Long id, RestJsonWrapperBean wrapper);

    void deleteTestMethod(Long id, RestJsonWrapperBean wrapper);
}
