/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.dailysupervision.resource;

import com.sunwayworld.cloud.module.qms.dailysupervision.bean.CoreDailySupervisionBean;
import com.sunwayworld.cloud.module.qms.dailysupervision.bean.CoreDailySupervisionConBean;
import com.sunwayworld.cloud.module.qms.dailysupervision.bean.CoreDailySupervisionItemBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/module/qms/daily-supervisions"})
public interface CoreDailySupervisionResource
extends GenericCloudResource<CoreDailySupervisionBean, Long> {
    public Page<CoreDailySupervisionItemBean> selectSupervisionItemPaginationByDailySupervisionId(Long var1, RestJsonWrapperBean var2);

    public Long insertSupervisionItem(Long var1, RestJsonWrapperBean var2);

    public void deleteSupervisionItem(Long var1, RestJsonWrapperBean var2);

    public Page<CoreDailySupervisionConBean> selectSupervisionConPaginationByDailySupervisionId(Long var1, RestJsonWrapperBean var2);

    public Long insertSupervisionCon(Long var1, RestJsonWrapperBean var2);

    public void deleteSupervisionCon(Long var1, RestJsonWrapperBean var2);
}
