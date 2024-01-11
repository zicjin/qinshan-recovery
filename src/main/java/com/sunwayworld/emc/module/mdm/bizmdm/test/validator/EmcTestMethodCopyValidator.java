package com.sunwayworld.emc.module.mdm.bizmdm.test.validator;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcTestMethodBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcTestMethodService;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.validator.data.DataValidator;

/**
 * @author magh 2019年12月26日
 */
@Repository
public class EmcTestMethodCopyValidator implements DataValidator {

    @Autowired
    private EmcTestMethodService testMethodService;

    @Override
    public boolean doValid(Object... args) {
        RestJsonWrapperBean wrapper = ArrayUtils.getUniqueTypeValue(args, RestJsonWrapperBean.class);

        EmcTestMethodBean testMethod = wrapper.parseUnique(EmcTestMethodBean.class);

        List<EmcTestMethodBean> testMethodList = testMethodService.selectListByFilter(SearchFilter.instance().match("testCategoryId", testMethod.getTestCategoryId()).filter(MatchPattern.EQ)
                .match("testId", testMethod.getTestId()).filter(MatchPattern.EQ).match("methodId", testMethod.getMethodId()).filter(MatchPattern.EQ));

        if (!testMethodList.isEmpty() && testMethodList.size() > 0) {

            this.addConstraintViolation("EMC.MODULE.MDM.BIZMDM.TESTS.TIP.CANNOT_ADD_REITERATED_METHOD");
            return false;

        }
        return true;
    }

}
