/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.support.selectable.resource;

import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.support.domain.Persistable;
import com.sunwayworld.framework.support.selectable.service.GenericSelectableService;
import java.io.Serializable;
import java.util.List;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public interface GenericSelectableResource<S extends GenericSelectableService<T, ID>, T extends Persistable<ID>, ID extends Serializable> {
    public S getService();

    @Log(value="\u4e0b\u62c9\u6846\u6570\u636e\u67e5\u8be2", type=LogType.SELECT)
    @RequestMapping(value={"/queries/selectable"}, method={RequestMethod.GET})
    default public List<T> selectSelectableList() {
        return this.getService().selectSelectableList();
    }

    @Log(value="\u4e0b\u62c9\u6846\u6570\u636e\u67e5\u8be2\uff08POST\u8bf7\u6c42\uff09", type=LogType.SELECT)
    @RequestMapping(value={"/queries/post-selectable"}, method={RequestMethod.POST})
    default public List<T> selectPostSelectableList() {
        return this.getService().selectSelectableList();
    }
}
