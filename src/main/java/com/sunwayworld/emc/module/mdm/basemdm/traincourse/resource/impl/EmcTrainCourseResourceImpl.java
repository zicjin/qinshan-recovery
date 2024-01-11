package com.sunwayworld.emc.module.mdm.basemdm.traincourse.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.mdm.basemdm.traincourse.bean.EmcTcTestMethodBean;
import com.sunwayworld.emc.module.mdm.basemdm.traincourse.resource.EmcTrainCourseResource;
import com.sunwayworld.emc.module.mdm.basemdm.traincourse.service.EmcTrainCourseService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author magh@sunwayworld.com
 * @date 2019-10-28
 */

@LogModule("培训课程")
@RestController
@GikamBean
public class EmcTrainCourseResourceImpl implements EmcTrainCourseResource {
    @Autowired
    private EmcTrainCourseService emcTrainCourseService;

    @Override
    public EmcTrainCourseService getService() {
        return emcTrainCourseService;
    }

    @Override
    @Log(value = "新增涉及标准", type = LogType.INSERT)
    @RequestMapping(value = "/{id}/test-methods", method = RequestMethod.POST)
    public Long insertTestMethod(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return getService().insertTestMethod(id, wrapper);
    }

    @Override
    @Log(value = "查询涉及标准", type = LogType.SELECT)
    @RequestMapping(value = "/{id}/test-methods/queries/raw", method = RequestMethod.POST)
    public Page<EmcTcTestMethodBean> selectTcTestMethodRawPagination(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return getService().selectTcTestMethodRawPagination(id, wrapper);
    }

    @Override
    @Log(value = "删除涉及标准", type = LogType.DELETE)
    @RequestMapping(value = "/{id}/test-methods", method = RequestMethod.DELETE)
    public void deleteTestMethod(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        getService().deleteTestMethod(id, wrapper);
    }
}
