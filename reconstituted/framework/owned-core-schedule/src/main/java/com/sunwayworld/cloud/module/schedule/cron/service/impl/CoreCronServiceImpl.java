/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.schedule.cron.service.impl;

import com.sunwayworld.cloud.module.cron.bean.CoreCronDTO;
import com.sunwayworld.cloud.module.schedule.cron.service.CoreCronService;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.CronUtils;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreCronServiceImpl
implements CoreCronService {
    @Override
    public CoreCronDTO selectCron(RestJsonWrapperBean wrapper) {
        String cron = wrapper.getParamValue("cron");
        String description = CronUtils.getDescription(cron);
        CoreCronDTO cronDTO = new CoreCronDTO();
        cronDTO.setCron(cron);
        cronDTO.setDescription(description);
        return cronDTO;
    }
}
