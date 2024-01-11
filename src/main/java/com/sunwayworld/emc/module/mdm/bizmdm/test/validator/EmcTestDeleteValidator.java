package com.sunwayworld.emc.module.mdm.bizmdm.test.validator;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcTestBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcTestMethodBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcTestMethodService;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.validator.data.DataValidator;

/**
 * @author magt@sunwayworld.com 2021年03月22日
 */
@Repository
public class EmcTestDeleteValidator implements DataValidator {

    @Autowired
    private EmcTestMethodService testMethodService;
    @Override
    public boolean doValid(Object... args) {
        RestJsonWrapperBean wrapper = ArrayUtils.getUniqueTypeValue(args, RestJsonWrapperBean.class);
        List<EmcTestBean> testList = wrapper.parse(EmcTestBean.class);
        List<Long> testIdList = testList.stream().map(a -> a.getId()).collect(Collectors.toList());
        List<EmcTestMethodBean> testMethodList = testMethodService.getDao().selectListByOneColumnValues(testIdList, "TESTID");
        if(!testMethodList.isEmpty()) {
            this.addConstraintViolation("EMC.MODULE.MDM.BIZMDM.TESTS.TIP.CAN_NOT_DEL");
            return false;
        }
        return true;
    }

}
