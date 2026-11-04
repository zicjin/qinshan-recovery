/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.mybatis.dao.event;

import com.sunwayworld.framework.mybatis.dao.event.DaoEventType;
import com.sunwayworld.framework.support.domain.Persistable;
import java.io.Serializable;
import java.util.List;
import org.springframework.context.ApplicationEvent;

public abstract class AbstractDaoEvent<T extends Persistable<ID>, ID extends Serializable>
extends ApplicationEvent {
    private static final long serialVersionUID = 8854820244147211325L;
    private final List<T> itemList;
    private final DaoEventType eventType;

    public AbstractDaoEvent(List<T> itemList, DaoEventType eventType) {
        super(itemList);
        this.itemList = itemList;
        this.eventType = eventType;
    }

    public List<T> getItemList() {
        return this.itemList;
    }

    public DaoEventType eventType() {
        return this.eventType;
    }
}
