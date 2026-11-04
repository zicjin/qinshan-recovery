/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.equipt.usagerecord.resource.impl;

import com.sunwayworld.cloud.module.equipt.usagerecord.bean.CoreEquiptMonthUsageRateDTO;
import com.sunwayworld.cloud.module.equipt.usagerecord.bean.CoreEquiptUsageRecordBean;
import com.sunwayworld.cloud.module.equipt.usagerecord.resource.CoreEquiptUsageRecordResource;
import com.sunwayworld.cloud.module.equipt.usagerecord.service.CoreEquiptUsageRecordService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import com.sunwayworld.framework.support.choosable.resource.AbstractGenericChoosableResource;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u4eea\u5668\u4f7f\u7528\u8bb0\u5f55")
@RestController
@GikamBean
public class CoreEquiptUsageRecordResourceImpl
implements CoreEquiptUsageRecordResource,
AbstractGenericResource<CoreEquiptUsageRecordService, CoreEquiptUsageRecordBean, Long>,
AbstractGenericChoosableResource<CoreEquiptUsageRecordService, CoreEquiptUsageRecordBean, Long> {
    @Autowired
    private CoreEquiptUsageRecordService equiptUsageRecordService;

    @Override
    public CoreEquiptUsageRecordService getService() {
        return this.equiptUsageRecordService;
    }

    @Override
    @Log(value="\u8bbe\u5907\u7c7b\u578b\u4f7f\u7528\u7387\u6570\u636e\u67e5\u8be2", type=LogType.SELECT)
    public Map<String, Object> selectEquiptCategoryUsageRateData(RestJsonWrapperBean jsonWrapper) {
        return this.getService().selectEquiptCategoryUsageRateData(jsonWrapper);
    }

    @Override
    @Log(value="\u603b\u4f7f\u7528\u7387\u6570\u636e\u67e5\u8be2", type=LogType.SELECT)
    public Map<String, Object> selectUsageRateData(RestJsonWrapperBean jsonWrapper) {
        return this.getService().selectUsageRateData(jsonWrapper);
    }

    @Override
    @Log(value="\u8bbe\u5907\u6708\u4f7f\u7528\u7387\u6570\u636e\u67e5\u8be2", type=LogType.SELECT)
    public Page<CoreEquiptMonthUsageRateDTO> selectMonthUsageRatePagination(RestJsonWrapperBean wrapper) {
        return this.getService().selectMonthUsageRatePagination(wrapper);
    }

    @Override
    @Log(value="\u603b\u4f53\u8d21\u732e\u503c\u6570\u636e\u67e5\u8be2", type=LogType.SELECT)
    public Map<String, Object> selectContributionData(RestJsonWrapperBean jsonWrapper) {
        return this.getService().selectContributionData(jsonWrapper);
    }

    @Override
    @Log(value="\u8bbe\u5907\u7c7b\u578b\u8d21\u732e\u503c\u6570\u636e\u67e5\u8be2", type=LogType.SELECT)
    public Map<String, Object> selectEquiptCategoryContributionData(RestJsonWrapperBean jsonWrapper) {
        return this.getService().selectEquiptCategoryContributionData(jsonWrapper);
    }

    @Override
    @Log(value="\u8bbe\u5907\u6708\u8d21\u732e\u503c\u6570\u636e\u67e5\u8be2", type=LogType.SELECT)
    public Page<CoreEquiptUsageRecordBean> selectMonthContributionPagination(RestJsonWrapperBean wrapper) {
        return this.getService().selectMonthContributionPagination(wrapper);
    }
}
