/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.office.resource;

import com.sunwayworld.cloud.module.office.bean.CoreOfficeTmplBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.activatable.resource.GenericActivatableCloudResource;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import com.sunwayworld.framework.support.choosable.resource.GenericChoosableCloudResource;

public interface CoreOfficeTmplResource
extends GenericCloudResource<CoreOfficeTmplBean, Long>,
GenericActivatableCloudResource<CoreOfficeTmplBean, Long>,
GenericChoosableCloudResource<CoreOfficeTmplBean, Long> {
    public String buildReport(Long var1, RestJsonWrapperBean var2);

    public String testBuildReport(Long var1, RestJsonWrapperBean var2);

    public Long insertBlankDocument(Long var1);

    public String getTestFormFieldByTmplId(Long var1, RestJsonWrapperBean var2);

    public void clearBindDs(Long var1);
}
