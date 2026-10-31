package com.sunwayworld.framework.restful.data.extractor;

/** Recorded source contract. Unimplemented operations fail explicitly. */
public interface ParamExtractor<T> {
    public abstract com.sunwayworld.framework.restful.data.RestJsonWrapperBean getJsonWrapper();
    public abstract T extract();
}
