/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.equipt.equipt.resource;

import com.sunwayworld.cloud.module.equipt.equipt.bean.CoreEquiptBean;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping(value={"/secure/core/module/equipt/equipts"})
public interface CoreEquiptResource
extends GenericCloudResource<CoreEquiptBean, Long> {
    @RequestMapping(value={"/fixed-assets/queries"}, method={RequestMethod.POST})
    public List<Map<String, Object>> selectFixedAssetList();

    @RequestMapping(value={"/category-statistics/queries"}, method={RequestMethod.POST})
    public List<Map<String, Object>> selectEquiptCategoryStatisticsData();

    @RequestMapping(value={"/categories/queries/selectable"}, method={RequestMethod.GET})
    public List<CoreEquiptBean> selectEquiptCategorySelectableList();

    @RequestMapping(value={"/locations/queries/selectable"}, method={RequestMethod.GET})
    public List<String> selectEquiptLocationSelectableList();
}
