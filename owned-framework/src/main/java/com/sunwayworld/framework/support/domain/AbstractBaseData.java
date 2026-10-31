package com.sunwayworld.framework.support.domain;

import java.util.Map;
import com.sunwayworld.framework.data.CaseInsensitiveLinkedMap;

public abstract class AbstractBaseData implements BaseData {
    private static final long serialVersionUID = 1L;
    private final Map<String, String> ext$ = new CaseInsensitiveLinkedMap<String>();
    public Map<String, String> getExt$() { return ext$; }
    public void setExt$(Map<String, String> values) { ext$.putAll(values); }
    public void setExt$Item(String key, String value) { ext$.put(key, value); }
    public String getExt$Item(String key) { return ext$.get(key); }
}
