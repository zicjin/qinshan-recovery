/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.equipt.equipt.service;

import com.sunwayworld.cloud.module.equipt.equipt.bean.CoreEquiptBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import java.util.List;
import java.util.Map;

public interface CoreEquiptService
extends GenericService<CoreEquiptBean, Long> {
    public void updateEquiptSwitchStatus(String var1, String var2);

    public String selectLastWorkStatusByCode(String var1);

    public Long insertUsageRecordByCode(String var1);

    public void updateUsageRecordByCode(String var1);

    public List<CoreEquiptBean> selectEquiptCategorySelectableList();

    public List<String> selectEquiptLocationSelectableList();

    public List<Map<String, Object>> selectFixedAssetList();

    public List<Map<String, Object>> selectEquiptCategoryStatisticsData();
}
