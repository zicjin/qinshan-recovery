/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.equipt.usagerecord.service;

import com.sunwayworld.cloud.module.equipt.usagerecord.bean.CoreEquiptMonthUsageRateDTO;
import com.sunwayworld.cloud.module.equipt.usagerecord.bean.CoreEquiptUsageRecordBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;
import java.util.Map;

public interface CoreEquiptUsageRecordService
extends GenericService<CoreEquiptUsageRecordBean, Long>,
GenericChoosableService<CoreEquiptUsageRecordBean, Long> {
    public Map<String, Object> selectUsageRateData(RestJsonWrapperBean var1);

    public Map<String, Object> selectEquiptCategoryUsageRateData(RestJsonWrapperBean var1);

    public Page<CoreEquiptMonthUsageRateDTO> selectMonthUsageRatePagination(RestJsonWrapperBean var1);

    public Map<String, Object> selectContributionData(RestJsonWrapperBean var1);

    public Map<String, Object> selectEquiptCategoryContributionData(RestJsonWrapperBean var1);

    public Page<CoreEquiptUsageRecordBean> selectMonthContributionPagination(RestJsonWrapperBean var1);
}
