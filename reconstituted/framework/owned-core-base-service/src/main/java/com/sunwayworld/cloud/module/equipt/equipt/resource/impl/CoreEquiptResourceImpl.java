/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.equipt.equipt.resource.impl;

import com.sunwayworld.cloud.module.equipt.equipt.bean.CoreEquiptBean;
import com.sunwayworld.cloud.module.equipt.equipt.resource.CoreEquiptResource;
import com.sunwayworld.cloud.module.equipt.equipt.service.CoreEquiptService;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u5173\u952e\u4eea\u5668\u7ba1\u7406")
@RestController
@GikamBean
public class CoreEquiptResourceImpl
implements CoreEquiptResource,
AbstractGenericResource<CoreEquiptService, CoreEquiptBean, Long> {
    @Autowired
    private CoreEquiptService equiptService;

    @Override
    public CoreEquiptService getService() {
        return this.equiptService;
    }

    @Override
    @Log(value="\u56fa\u5b9a\u8d44\u4ea7\u5360\u6bd4\u6570\u636e\u67e5\u8be2", type=LogType.SELECT)
    public List<Map<String, Object>> selectFixedAssetList() {
        return this.getService().selectFixedAssetList();
    }

    @Override
    @Log(value="\u8bbe\u5907\u7c7b\u578b\u7edf\u8ba1\u6570\u636e\u67e5\u8be2", type=LogType.SELECT)
    public List<Map<String, Object>> selectEquiptCategoryStatisticsData() {
        return this.getService().selectEquiptCategoryStatisticsData();
    }

    @Override
    @Log(value="\u8bbe\u5907\u7c7b\u578b\u4f7f\u7528\u7387\u8bbe\u5907\u7c7b\u578b\u4e0b\u62c9\u6846\u67e5\u8be2", type=LogType.SELECT)
    public List<CoreEquiptBean> selectEquiptCategorySelectableList() {
        return this.getService().selectEquiptCategorySelectableList();
    }

    @Override
    @Log(value="\u5173\u952e\u8bbe\u5907\u76d1\u63a7\u623f\u95f4\u4e0b\u62c9\u6846\u67e5\u8be2", type=LogType.SELECT)
    public List<String> selectEquiptLocationSelectableList() {
        return this.getService().selectEquiptLocationSelectableList();
    }
}
