package com.sunwayworld.framework.beans;

import java.util.List;

/** Listener contract exposed by bean property proxies. */
public interface BeanPropertyListener {
    List<BeanPropertyEvent> getChangedPropertyEventList();
}
