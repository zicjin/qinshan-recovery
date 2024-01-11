package com.sunwayworld.emc.module.bp.bp.validator;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.framework.constant.EmcBizConstant;
import com.sunwayworld.emc.module.bp.bp.bean.EmcFolderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskService;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.framework.validator.data.DataValidator;

/**
 * @author zhengxy@sunwayworld.com 2020年11月18日
 */
@Repository
@GikamBean
public class EmcFolderSubmitValidator implements DataValidator {

    @Autowired
    private EmcOrderTaskService orderTaskService;

    @Override
    public boolean doValid(Object... args) {
        RestJsonWrapperBean wrapper = ArrayUtils.getUniqueTypeValue(args, RestJsonWrapperBean.class);
        List<EmcFolderBean> folderList = wrapper.parse(EmcFolderBean.class);
        List<Long> folderIdList = folderList.stream().map(EmcFolderBean::getId).collect(Collectors.toList());

        List<EmcOrderTaskBean> orderTaskList = orderTaskService.getDao().selectListByOneColumnValues(folderIdList, "FOLDERID", Arrays.asList("ID", "CONTAINERCATEGORYID", "ANALYSISTYPE"));
        List<EmcOrderTaskBean> containerEmptyTaskList = orderTaskList.stream().filter(task -> StringUtils.equals(EmcBizConstant.ANALYSISCATEGORY_ANALYSIS, task.getAnalysisType()) && ObjectUtils.isEmpty(task.getContainerCategoryId())).collect(Collectors.toList());

        if (!containerEmptyTaskList.isEmpty()) {
            this.addConstraintViolation("EMC.MODULE.BP.FOLDER.FOLDERS.TIP.CONTAINER_IS_EMPTY");
            return false;
        }

        return true;
    }
}
