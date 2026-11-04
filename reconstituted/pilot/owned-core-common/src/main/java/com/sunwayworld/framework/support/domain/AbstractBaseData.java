/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.support.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.sunwayworld.framework.data.CaseInsensitiveLinkedMap;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.json.jackson.JacksonCaseInsensitiveLinkedMapSerializer;
import com.sunwayworld.framework.support.domain.BaseData;
import java.util.Map;

public abstract class AbstractBaseData
implements BaseData {
    @Transient
    private static final long serialVersionUID = -7205081622411099371L;
    @Transient
    @JsonSerialize(using=JacksonCaseInsensitiveLinkedMapSerializer.class)
    private final Map<String, String> ext$ = new CaseInsensitiveLinkedMap<String>();

    @Override
    public Map<String, String> getExt$() {
        return this.ext$;
    }

    @Override
    public void setExt$(Map<String, String> ext$) {
        this.ext$.putAll(ext$);
    }

    @Override
    public void setExt$Item(String key, String value) {
        this.ext$.put(key, value);
    }

    public String getExt$Item(String key) {
        return this.ext$.get(key);
    }
}
