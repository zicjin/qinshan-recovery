/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.equipt.usagerecord.resource;

import com.sunwayworld.cloud.module.equipt.usagerecord.bean.CoreEquiptMonthUsageRateDTO;
import com.sunwayworld.cloud.module.equipt.usagerecord.bean.CoreEquiptUsageRecordBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import com.sunwayworld.framework.support.choosable.resource.GenericChoosableCloudResource;
import java.util.Map;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping(value={"/secure/core/module/equipt/usage-records"})
public interface CoreEquiptUsageRecordResource
extends GenericCloudResource<CoreEquiptUsageRecordBean, Long>,
GenericChoosableCloudResource<CoreEquiptUsageRecordBean, Long> {
    @RequestMapping(value={"/usage-rates/queries"}, method={RequestMethod.POST})
    public Map<String, Object> selectUsageRateData(RestJsonWrapperBean var1);

    @RequestMapping(value={"/equipt-category-usage-rates/queries"}, method={RequestMethod.POST})
    public Map<String, Object> selectEquiptCategoryUsageRateData(RestJsonWrapperBean var1);

    @RequestMapping(value={"/month-usage-rates/queries"}, method={RequestMethod.POST})
    public Page<CoreEquiptMonthUsageRateDTO> selectMonthUsageRatePagination(RestJsonWrapperBean var1);

    @RequestMapping(value={"/contributions/queries"}, method={RequestMethod.POST})
    public Map<String, Object> selectContributionData(RestJsonWrapperBean var1);

    @RequestMapping(value={"/equipt-category-contributions/queries"}, method={RequestMethod.POST})
    public Map<String, Object> selectEquiptCategoryContributionData(RestJsonWrapperBean var1);

    @RequestMapping(value={"/month-contributions/queries"}, method={RequestMethod.POST})
    public Page<CoreEquiptUsageRecordBean> selectMonthContributionPagination(RestJsonWrapperBean var1);
}
