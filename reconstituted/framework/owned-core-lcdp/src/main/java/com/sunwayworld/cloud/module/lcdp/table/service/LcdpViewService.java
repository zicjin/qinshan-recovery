/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.table.service;

import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpAnalysisResultDTO;
import com.sunwayworld.cloud.module.lcdp.table.bean.LcdpTableCompareDTO;
import com.sunwayworld.cloud.module.lcdp.table.bean.LcdpViewBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import java.util.List;
import java.util.Map;

public interface LcdpViewService
extends GenericService<LcdpViewBean, Long> {
    public List<LcdpViewBean> selectPhysicalViewInfoList(RestJsonWrapperBean var1);

    public Long design(String var1);

    public void checkout(String var1);

    public void submit(List<LcdpViewBean> var1);

    public List<String> revert(List<String> var1);

    public LcdpTableCompareDTO<LcdpViewBean> compare(RestJsonWrapperBean var1);

    public Map<String, LcdpAnalysisResultDTO> analysisViewInfo(List<String> var1, Map<String, String> var2);

    public LcdpViewBean selectPhysicalViewInfoList(String var1);
}
