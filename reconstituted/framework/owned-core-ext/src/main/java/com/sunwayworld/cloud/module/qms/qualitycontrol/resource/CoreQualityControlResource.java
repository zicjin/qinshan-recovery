/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.qualitycontrol.resource;

import com.sunwayworld.cloud.module.qms.annualplan.bean.CoreQualityControlPlanBean;
import com.sunwayworld.cloud.module.qms.qualitycontrol.bean.CoreQualityControlBean;
import com.sunwayworld.cloud.module.qms.qualitycontrol.bean.CoreQualityControlItemBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/module/qms/quality-controls"})
public interface CoreQualityControlResource
extends GenericCloudResource<CoreQualityControlBean, Long> {
    public Page<CoreQualityControlPlanBean> selectControlPlanChoosablePagination(RestJsonWrapperBean var1);

    public Page<CoreQualityControlItemBean> selectControlItemPaginationByQualityControlId(Long var1, RestJsonWrapperBean var2);

    public Long insertControlItem(Long var1, RestJsonWrapperBean var2);

    public void deleteControlItem(Long var1, RestJsonWrapperBean var2);
}
