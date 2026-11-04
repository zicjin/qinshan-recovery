/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.customchart.resource.impl;

import com.sunwayworld.cloud.module.item.customchart.bean.CoreCustomChartShareBean;
import com.sunwayworld.cloud.module.item.customchart.resource.CoreCustomChartShareResource;
import com.sunwayworld.cloud.module.item.customchart.service.CoreCustomChartShareService;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u81ea\u5b9a\u4e49\u56fe\u8868\u5206\u4eab\u4fe1\u606f")
@RestController
@GikamBean
public class CoreCustomChartShareResourceImpl
implements CoreCustomChartShareResource,
AbstractGenericResource<CoreCustomChartShareService, CoreCustomChartShareBean, Long> {
    @Autowired
    private CoreCustomChartShareService coreCustomChartShareService;

    @Override
    public CoreCustomChartShareService getService() {
        return this.coreCustomChartShareService;
    }

    @Override
    @Log(value="\u65b0\u589e\u81ea\u5b9a\u4e49\u56fe\u8868\u5206\u4eab\u4fe1\u606f", type=LogType.INSERT)
    @RequestMapping(value={"/insert"}, method={RequestMethod.POST})
    public Long insert(RestJsonWrapperBean wrapper) {
        return this.getService().insert(wrapper);
    }

    @Override
    @Log(value="\u6839\u636echartId\u67e5\u8be2\u81ea\u5b9a\u4e49\u56fe\u8868\u7684\u5206\u4eab\u4fe1\u606f", type=LogType.SELECT)
    @RequestMapping(value={"/select"}, method={RequestMethod.POST})
    public List<CoreCustomChartShareBean> selectCoreCustomChartShareBeanByChartId(RestJsonWrapperBean wrapper) {
        return this.getService().selectCoreCustomChartShareBeanByChartId(wrapper);
    }
}
