/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.support.selectable.resource;

import com.sunwayworld.framework.support.domain.Persistable;
import java.io.Serializable;
import java.util.ArrayList;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public interface GenericSelectableCloudResource<T extends Persistable<ID>, ID extends Serializable> {
    @RequestMapping(value={"/queries/selectable"}, method={RequestMethod.GET})
    public ArrayList<T> selectSelectableList();

    @RequestMapping(value={"/queries/post-selectable"}, method={RequestMethod.POST})
    public ArrayList<T> selectPostSelectableList();
}
