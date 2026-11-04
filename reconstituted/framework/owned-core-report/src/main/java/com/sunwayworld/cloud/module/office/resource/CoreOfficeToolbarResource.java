/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.office.resource;

import com.sunwayworld.cloud.module.office.bean.CoreOfficeToolbarBean;
import com.sunwayworld.cloud.module.office.bean.CoreOfficeToolbarDTO;
import com.sunwayworld.cloud.module.office.bean.CoreOfficeToolbarLineBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;

public interface CoreOfficeToolbarResource
extends GenericCloudResource<CoreOfficeToolbarBean, Long> {
    public Long insertLine(Long var1, RestJsonWrapperBean var2);

    public Page<CoreOfficeToolbarLineBean> selectOfficeToolbarLinePagation(Long var1, RestJsonWrapperBean var2);

    public void deleteLine(RestJsonWrapperBean var1);

    public Page<CoreOfficeToolbarDTO> selectOfficeToolbarPagetion(String var1, RestJsonWrapperBean var2);

    public void unsuspend(RestJsonWrapperBean var1);

    public void suspend(RestJsonWrapperBean var1);
}
