/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.password.validator;

import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.validator.data.DataValidator;
import com.sunwayworld.module.sys.password.bean.CorePasswordPolicyBean;
import com.sunwayworld.module.sys.role.service.CoreRoleService;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

@Repository
public class CorePasswordPolicyDeleteValidator
implements DataValidator {
    @Autowired
    @Lazy
    private CoreRoleService coreRoleService;

    @Override
    public boolean doValid(Object ... args) {
        RestJsonWrapperBean wrapper = (RestJsonWrapperBean)args[0];
        List idList = wrapper.parse(CorePasswordPolicyBean.class).stream().map(e -> e.getId()).collect(Collectors.toList());
        List coreRoleList = this.coreRoleService.getDao().selectListByOneColumnValues(idList, "PASSWORDPOLICYID", new Order[0]);
        boolean validFlag = true;
        if (!coreRoleList.isEmpty()) {
            this.addConstraintViolation("CORE.MODULE.SYS.PASSWORDS.VALIDATTION.CAN_NOT_DELETE");
            validFlag = false;
        }
        return validFlag;
    }
}
