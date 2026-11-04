/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.train.examtmpl.resource.impl;

import com.sunwayworld.cloud.module.train.examtmpl.bean.CoreExamTmplBean;
import com.sunwayworld.cloud.module.train.examtmpl.resource.CoreExamTmplResource;
import com.sunwayworld.cloud.module.train.examtmpl.service.CoreExamTmplService;
import com.sunwayworld.cloud.module.train.examtmpl.validator.CoreExamTmplDeactivatableValidator;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.data.RestValidationResultBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.activatable.resource.AbstractGenericActivatableResource;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import com.sunwayworld.framework.support.choosable.resource.AbstractGenericChoosableResource;
import com.sunwayworld.framework.validator.data.annotation.ValidateDataWith;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u8bd5\u5377\u6a21\u677f")
@RestController
@GikamBean
public class CoreExamTmplResourceImpl
implements CoreExamTmplResource,
AbstractGenericResource<CoreExamTmplService, CoreExamTmplBean, Long>,
AbstractGenericChoosableResource<CoreExamTmplService, CoreExamTmplBean, Long>,
AbstractGenericActivatableResource<CoreExamTmplService, CoreExamTmplBean, Long> {
    @Autowired
    private CoreExamTmplService coreExamTmplService;

    @Override
    public CoreExamTmplService getService() {
        return this.coreExamTmplService;
    }

    @Log(value="\u590d\u5236\u8bd5\u5377\u6a21\u677f", type=LogType.INSERT)
    public ArrayList<Long> clone(RestJsonWrapperBean wrapper) {
        return (ArrayList)this.coreExamTmplService.clone(wrapper);
    }

    @Override
    @Log(value="\u505c\u7528\u8bd5\u5377\u6a21\u677f", type=LogType.INSERT)
    @ValidateDataWith(value=CoreExamTmplDeactivatableValidator.class)
    public void deactivate(RestJsonWrapperBean wrapper) {
        this.coreExamTmplService.deactivate(wrapper);
    }

    @Override
    @Log(value="\u8bd5\u5377\u6a21\u677f\u5220\u9664\u6821\u9a8c", type=LogType.VALIDATE)
    public RestValidationResultBean deleteValidate(RestJsonWrapperBean wrapper) {
        return this.getService().deleteValidate(wrapper);
    }
}
