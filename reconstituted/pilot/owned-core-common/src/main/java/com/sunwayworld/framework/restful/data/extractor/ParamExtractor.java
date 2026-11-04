/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.restful.data.extractor;

import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

public interface ParamExtractor<T> {
    public RestJsonWrapperBean getJsonWrapper();

    public T extract();
}
