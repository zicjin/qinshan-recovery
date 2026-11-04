/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.lims.reporttemplate.support.data;

import com.sunwayworld.cloud.lims.reporttemplate.support.function.FunctionAble;
import com.sunwayworld.framework.data.CaseInsensitiveLinkedMap;
import java.util.Map;

public class LimsTemplateDataMap<V>
extends CaseInsensitiveLinkedMap<V>
implements FunctionAble {
    public LimsTemplateDataMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public LimsTemplateDataMap(int initialCapacity) {
        super(initialCapacity);
    }

    public LimsTemplateDataMap() {
    }

    public LimsTemplateDataMap(Map m) {
        super(m);
    }

    public LimsTemplateDataMap(int initialCapacity, float loadFactor, boolean accessOrder) {
        super(initialCapacity, loadFactor, accessOrder);
    }

    public <T> T get(String key) {
        return (T)super.get(key);
    }
}
