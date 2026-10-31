package com.sunwayworld.framework.support.domain;

import java.io.Serializable;
import java.util.Map;

public interface BaseData extends Serializable {
    Map<String, String> getExt$();
    void setExt$(Map<String, String> values);
    void setExt$Item(String key, String value);
}
