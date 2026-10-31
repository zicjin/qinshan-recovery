package com.sunwayworld.emc.module.mdm.bizmdm.method.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.bizmdm.method.bean.EmcMethodBean;
import com.sunwayworld.emc.module.mdm.bizmdm.method.service.EmcMethodService;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.validator.data.DataValidator;

@Repository
public class EmcMethodUniqueValidator implements DataValidator {
    @Autowired
    private EmcMethodService methodService;

    @Override
    public boolean doValid(Object... args) {
        RestJsonWrapperBean wrapper = ArrayUtils.getUniqueTypeValue(args, RestJsonWrapperBean.class);
        EmcMethodBean emcMethod = wrapper.parseUnique(EmcMethodBean.class);

        EmcMethodBean filter = new EmcMethodBean();
        filter.setMethodNo(emcMethod.getMethodNo());
        filter.setMethodName(emcMethod.getMethodName());
        filter.setSource(emcMethod.getSource());
        filter.setMonitorCategory(emcMethod.getMonitorCategory());

        EmcMethodBean method = methodService.getDao().selectFirstIfPresent(filter);
        if (method != null) {
            this.addConstraintViolation("EMC.MODULE.MDM.BIZMDM.METHODS.TIP.METHOD_EXIST");
            return false;
        }

        return true;
    }

}
