package com.sunwayworld.emc.module.mdm.bizmdm.capacity.validator;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.bizmdm.capacity.bean.EmcTestCapacityBean;
import com.sunwayworld.emc.module.mdm.bizmdm.capacity.service.EmcTestCapacityService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcTestMethodBean;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.validator.data.DataValidator;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;

/**
 * @author zhengxy@sunwayworld.com 2021年06月16日
 */
@Repository
public class PfAddTestCapacityValidator implements DataValidator {

    @Autowired
    private EmcTestCapacityService testCapacityService;

    @Override
    public boolean doValid(Object... args) {
        RestJsonWrapperBean wrapper = ArrayUtils.getUniqueTypeValue(args, RestJsonWrapperBean.class);
        List<EmcTestMethodBean> testMethodList = wrapper.parse(EmcTestMethodBean.class);
        List<Long> testMethodIdList = testMethodList.stream().map(EmcTestMethodBean::getId).collect(Collectors.toList());

        CoreUserBean user = LocalContextHelper.getLoginUser();
        String parentOrgId = user.getExt$Item("parentorgid");

        List<EmcTestCapacityBean> existCapacityList = testCapacityService.selectListByFilter(SearchFilter.instance().match("testMethodId", testMethodIdList).filter(MatchPattern.OR).match("orgId", parentOrgId).filter(MatchPattern.EQ));
        if (!existCapacityList.isEmpty()) {
            this.addConstraintViolation("EMC.MODULE.MDM.BIZMDM.TEST.CAPACITYS.TIP.TEST_METHOD_EXIST");
            return false;
        }

        return true;
    }
}
