/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.lims.reporttemplate.support.data;

import com.sunwayworld.cloud.lims.reporttemplate.support.data.AbstractMergeTableRenderData;
import com.sunwayworld.cloud.lims.reporttemplate.support.data.LimsTemplateDataList;
import com.sunwayworld.cloud.lims.reporttemplate.support.data.LimsTemplateDataMap;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.StringUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LimsMergeOrdtaskVTableRenderData
extends AbstractMergeTableRenderData {
    private List<Integer> mergeVIndexList;
    private List<List<Integer>> mergeHIndexList;
    private String mergeByField;

    public LimsMergeOrdtaskVTableRenderData(LimsTemplateDataList<LimsTemplateDataMap> data, String mergeVIndexStr, String mergeHIndexStr) {
        super(data);
        List mergeVIndexList = ArrayUtils.asList(StringUtils.split(mergeVIndexStr, "$")).stream().map(e -> NumberUtils.parseInt(e)).collect(Collectors.toList());
        this.mergeVIndexList = mergeVIndexList;
        ArrayList<List<Integer>> mergeHIndexList = new ArrayList<List<Integer>>();
        List<String> mergeHIndexStrList = ArrayUtils.asList(StringUtils.split(mergeHIndexStr, "$"));
        for (String s : mergeHIndexStrList) {
            List curMergeHIndexList = ArrayUtils.asList(StringUtils.split(s, "-")).stream().map(e -> NumberUtils.parseInt(e)).collect(Collectors.toList());
            mergeHIndexList.add(curMergeHIndexList);
        }
        this.mergeHIndexList = mergeHIndexList;
    }

    public List<Integer> getMergeVIndexList() {
        return this.mergeVIndexList;
    }

    public void setMergeVIndexList(List<Integer> mergeVIndexList) {
        this.mergeVIndexList = mergeVIndexList;
    }

    public List<List<Integer>> getMergeHIndexList() {
        return this.mergeHIndexList;
    }

    public void setMergeHIndexList(List<List<Integer>> mergeHIndexList) {
        this.mergeHIndexList = mergeHIndexList;
    }

    public String getMergeByField() {
        return this.mergeByField;
    }

    public void setMergeByField(String mergeByField) {
        this.mergeByField = mergeByField;
    }
}
