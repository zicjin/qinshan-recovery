package com.sunwayworld.emc.module.bp.project.project.validator;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.framework.constant.EmcBizConstant;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcProjectBean;
import com.sunwayworld.emc.module.bp.sample.sample.bean.EmcSampleBean;
import com.sunwayworld.emc.module.bp.sample.sample.service.EmcSampleService;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.framework.validator.data.DataValidator;

/**
 * @author zhengxy@sunwayworld.com 2021年11月16日
 */
@Repository
public class PfUndoDeliveryProjectValidator implements DataValidator {

    @Autowired
    private EmcSampleService sampleService;

    @Override
    public boolean doValid(Object... args) {
        RestJsonWrapperBean wrapper = ArrayUtils.getUniqueTypeValue(args, RestJsonWrapperBean.class);
        List<EmcProjectBean> projectList = wrapper.parse(EmcProjectBean.class);
        List<Long> projectIdList = projectList.stream().map(EmcProjectBean::getId).collect(Collectors.toList());

        List<EmcSampleBean> sampleList = sampleService.getDao().selectListByOneColumnValues(projectIdList, "projId", Arrays.asList("id", "status"));
        boolean allDraftStatus = sampleList.stream().allMatch(sample -> StringUtils.equalsIgnoreCase(sample.getStatus(), EmcBizConstant.SAMPLE_STATUS_DRAFT));

        if (!allDraftStatus) {
            this.addConstraintViolation("EMC.MODULE.BP.DELIVERIES.TIP.CANNOT_UNDO_PROJECT");
            return false;
        }

        return true;
    }
}
