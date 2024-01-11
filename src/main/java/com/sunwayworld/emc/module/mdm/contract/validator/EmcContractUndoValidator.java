package com.sunwayworld.emc.module.mdm.contract.validator;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.project.project.bean.EmcProjectBean;
import com.sunwayworld.emc.module.bp.project.project.service.EmcProjectService;
import com.sunwayworld.emc.module.mdm.contract.bean.EmcContractBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.validator.data.DataValidator;

/**
 * 合同退回校验
 * @author zhengxy@sunwayworld.com 2020年08月26日
 */
@Repository
public class EmcContractUndoValidator implements DataValidator {

    @Autowired
    private EmcProjectService projectService;

    @Override
    public boolean doValid(Object... args) {
        RestJsonWrapperBean wrapper = ArrayUtils.getUniqueTypeValue(args, RestJsonWrapperBean.class);
        EmcContractBean contract = wrapper.parseUnique(EmcContractBean.class);
        List<EmcProjectBean> projectList = projectService.selectProjectByContractId(contract.getId());
        if (!projectList.isEmpty()) {
            this.addConstraintViolation("EMC.MODULE.MDM.CONTRACTS.TIP.HAS_LINKED_PROJECT");
            return false;
        }

        return true;
    }
}
