/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.code.validator;

import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.validator.data.DataValidator;
import com.sunwayworld.module.sys.code.bean.CoreCodeCategoryBean;
import com.sunwayworld.module.sys.code.service.CoreCodeService;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class CodeCategoryDeleteValidator
implements DataValidator {
    @Autowired
    private CoreCodeService coreCodeServiceice;

    @Override
    public boolean doValid(Object ... args) {
        RestJsonWrapperBean wrapper = (RestJsonWrapperBean)args[0];
        List<CoreCodeCategoryBean> limsCodeCategoryList = wrapper.parse(CoreCodeCategoryBean.class);
        List limsCodeCategoryIdList = limsCodeCategoryList.stream().map(e -> e.getId()).collect(Collectors.toList());
        List coreCodeBeanList = this.coreCodeServiceice.getDao().selectListByOneColumnValues(limsCodeCategoryIdList, "CODECATEGORYID", new Order[0]);
        if (!coreCodeBeanList.isEmpty()) {
            this.addConstraintViolation("CORE.MODULE.SYS.CODECATEGORIES.TIP.HAS_BEEN_USED_ERROR");
            return false;
        }
        return true;
    }
}
