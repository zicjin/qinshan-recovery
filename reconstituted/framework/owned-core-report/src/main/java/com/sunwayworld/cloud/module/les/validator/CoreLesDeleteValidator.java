/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.les.validator;

import com.sunwayworld.cloud.module.les.bean.CoreLesTemplateBean;
import com.sunwayworld.cloud.module.les.service.CoreLesInstanceService;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.validator.data.DataValidator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class CoreLesDeleteValidator
implements DataValidator {
    @Autowired
    private CoreLesInstanceService lesInstanceService;

    @Override
    public boolean doValid(Object ... args) {
        RestJsonWrapperBean wrapper = Objects.requireNonNull((RestJsonWrapperBean)args[0]);
        List<CoreLesTemplateBean> lesTemplateList = wrapper.parse(CoreLesTemplateBean.class);
        List lesTemplateIdList = lesTemplateList.stream().map(e -> e.getId()).collect(Collectors.toList());
        List coreCodeBeanList = this.lesInstanceService.getDao().selectListByOneColumnValues(lesTemplateIdList, "TEMPLATEID", new Order[0]);
        if (!coreCodeBeanList.isEmpty()) {
            this.addConstraintViolation("CORE.MODULE.LES.TEMPLATES.TIP.NOT_ALLOW_DELETE_EXIST_INSTANCE_DATA");
            return false;
        }
        return true;
    }
}
