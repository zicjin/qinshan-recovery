package com.sunwayworld.emc.module.bp.project.project.validator;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.project.project.bean.EmcProjectBean;
import com.sunwayworld.emc.module.bp.project.project.service.EmcProjectService;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.framework.validator.data.DataValidator;

/**
 * @author zhengxy@sunwayworld.com 2020年06月04日
 */
@Repository
public class EmcProjectDeleteValidator implements DataValidator {

    @Autowired
    private EmcProjectService projectService;

    @Override
    public boolean doValid(Object... args) {
        RestJsonWrapperBean wrapper = ArrayUtils.getUniqueTypeValue(args, RestJsonWrapperBean.class);

        List<Long> projectIdList = wrapper.parse(EmcProjectBean.class).stream().map(EmcProjectBean::getId).collect(Collectors.toList());
        List<EmcProjectBean> projectList = projectService.getDao().selectListByOneColumnValues(projectIdList, "ID", Arrays.asList("ID", "STATUS", "BEFORESTATUS"));
        List<EmcProjectBean> canceledProjectList = projectList.stream().filter(project -> StringUtils.equals(project.getStatus(), "cancel")).collect(Collectors.toList());
        List<EmcProjectBean> normalProjectList = projectList.stream().filter(project -> !StringUtils.equals(project.getStatus(), "cancel")).collect(Collectors.toList());
        boolean isCreatedProject = canceledProjectList.stream().allMatch(project -> StringUtils.equals(project.getBeforeStatus(), "create"))
                && normalProjectList.stream().allMatch(project -> StringUtils.equals(project.getStatus(), "create"));
        if (!isCreatedProject) {
            this.addConstraintViolation("EMC.MODULE.BP.PROJECT.PROJECTS.TIP.CAN_NOT_DELETE");
            return false;
        }

        return true;
    }
}
