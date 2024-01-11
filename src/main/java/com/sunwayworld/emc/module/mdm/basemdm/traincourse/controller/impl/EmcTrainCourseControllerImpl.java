package com.sunwayworld.emc.module.mdm.basemdm.traincourse.controller.impl;

import org.springframework.stereotype.Controller;

import com.sunwayworld.emc.module.mdm.basemdm.traincourse.controller.EmcTrainCourseController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author magh@sunwayworld.com
 * @date 2019-10-28
 */

@Controller
@GikamBean
public class EmcTrainCourseControllerImpl implements EmcTrainCourseController {

    @Log(value = "培训课程编制列表页", type = LogType.CONTROLLER)
    @Override
    public String editListPage() {
        return "emc/module/mdm/basemdm/train-courses/emc-train-course-edit-list";
    }

}
