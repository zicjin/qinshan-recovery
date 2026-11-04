/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.spcs.bean;

import com.sunwayworld.cloud.module.item.spcs.bean.CoreSpcResultBean;
import com.sunwayworld.framework.utils.CollectionUtils;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class CoreCompFlushRespBean
implements Serializable {
    private static final long serialVersionUID = -5037532648433353994L;
    private Map<String, Object> addition = CollectionUtils.emptyMap();
    private List<Map<String, Object>> dataList = CollectionUtils.emptyList();
    private CoreSpcResultBean resultBean;

    public void putAddition(String key, Object value) {
        this.addition.put(key, value);
    }

    public void putAddition(Map<String, Object> map) {
        this.addition.putAll(map);
    }

    public Map<String, Object> getAddition() {
        return this.addition;
    }

    public void setAddition(Map<String, Object> addition) {
        this.addition = addition;
    }

    public List<Map<String, Object>> getDataList() {
        return this.dataList;
    }

    public void setDataList(List<Map<String, Object>> dataList) {
        this.dataList = dataList;
    }

    public CoreSpcResultBean getResultBean() {
        return this.resultBean;
    }

    public void setResultBean(CoreSpcResultBean resultBean) {
        this.resultBean = resultBean;
    }
}
