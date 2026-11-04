/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.schedule.cron.resource.impl;

import com.sunwayworld.cloud.module.cron.bean.CoreCronDTO;
import com.sunwayworld.cloud.module.cron.resource.CoreCornResource;
import com.sunwayworld.cloud.module.schedule.cron.service.CoreCronService;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u7cfb\u7edfCRON\u8868\u8fbe\u5f0f")
@RestController
@GikamBean
@RequestMapping(value={"/core/module/sys/crons"})
public class CoreCronResourceImpl
implements CoreCornResource {
    @Autowired
    private CoreCronService cronService;

    @Override
    @Log(value="\u67e5\u8be2CRON\u8868\u8fbe\u5f0f\u5bf9\u5e94\u7684\u6587\u672c", type=LogType.SELECT)
    public CoreCronDTO selectCron(RestJsonWrapperBean wrapper) {
        return this.cronService.selectCron(wrapper);
    }
}
