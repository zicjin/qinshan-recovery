package com.sunwayworld.emc.module.bp.result.run.validator;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.result.run.bean.EmcRunBean;
import com.sunwayworld.emc.module.bp.result.run.service.EmcRunService;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.framework.validator.data.DataValidator;
import com.sunwayworld.module.sys.bpmn.diagram.ProcessStatus;

/**
 * @author zhengxy@sunwayworld.com 2020年10月13日
 */
@Repository
public class EmcRunDeleteValidator implements DataValidator {

    @Autowired
    private EmcRunService runService;

    @Override
    public boolean doValid(Object... args) {
        RestJsonWrapperBean wrapper = ArrayUtils.getUniqueTypeValue(args, RestJsonWrapperBean.class);
        List<EmcRunBean> runList = wrapper.parse(EmcRunBean.class);

        List<String> runIdList = runList.stream().map(EmcRunBean::getId).collect(Collectors.toList());
        List<EmcRunBean> realRunList = runService.getDao().selectListByOneColumnValues(runIdList, "ID", Arrays.asList("ID", "PROCESSSTATUS"));

        // 只能删除draft状态的批
        boolean allMatch = realRunList.stream().allMatch(run -> StringUtils.startsWith(run.getProcessStatus(), ProcessStatus.DRAFT.name().toLowerCase()));
        if (!allMatch) {
            this.addConstraintViolation("EMC.MODULE.BP.RESULT.RUNS.TIP.EXIST_SUBMITTED_RUN");
            return false;
        }

        return true;
    }
}
