/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.train.examtmpl.resource;

import com.sunwayworld.cloud.module.train.examtmpl.bean.CoreExamTmplBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.data.RestValidationResultBean;
import com.sunwayworld.framework.support.activatable.resource.GenericActivatableCloudResource;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import com.sunwayworld.framework.support.choosable.resource.GenericChoosableCloudResource;
import java.util.List;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping(value={"/secure/core/module/train/exam-tmpls"})
public interface CoreExamTmplResource
extends GenericCloudResource<CoreExamTmplBean, Long>,
GenericChoosableCloudResource<CoreExamTmplBean, Long>,
GenericActivatableCloudResource<CoreExamTmplBean, Long> {
    @RequestMapping(value={"/action/clone"}, method={RequestMethod.PUT})
    public List<Long> clone(RestJsonWrapperBean var1);

    @RequestMapping(value={"/action/delete-validate"}, method={RequestMethod.POST})
    public RestValidationResultBean deleteValidate(RestJsonWrapperBean var1);
}
