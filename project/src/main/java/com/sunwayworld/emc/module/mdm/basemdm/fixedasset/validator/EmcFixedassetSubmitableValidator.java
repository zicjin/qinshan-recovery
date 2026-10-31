package com.sunwayworld.emc.module.mdm.basemdm.fixedasset.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.basemdm.fixedasset.bean.EmcFixedassetBean;
import com.sunwayworld.emc.module.mdm.basemdm.fixedasset.service.EmcFixedassetService;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.validator.data.DataValidator;

/**
 * @author zhangxiantao
 * @date 2019-11-25
 */

@Repository
public class EmcFixedassetSubmitableValidator implements DataValidator {
    @Autowired
    private EmcFixedassetService fixedassetService;
    
    @Override
    public boolean doValid(Object ... args) {
        Long id = (Long) args[0];
        EmcFixedassetBean emcFixedasset = fixedassetService.selectDetail(id);
        Boolean passed = true;
        if (!passed) {
            addConstraintViolation(emcFixedasset.getExt$().get(Constant.VALIDATION_SUBMIT_MSG));
        }
        return passed;
    }
}
