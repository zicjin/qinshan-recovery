/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.design.studio.resource;

import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.module.design.studio.bean.CoreStudioTableFieldBean;
import com.sunwayworld.module.design.studio.service.CoreStudioTableFieldService;
import java.util.List;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping(value={"/secure/core/module/design/studio-table-fields"})
public interface CoreStudioTableFieldResource
extends GenericResource<CoreStudioTableFieldService, CoreStudioTableFieldBean, Long> {
    @RequestMapping(method={RequestMethod.GET})
    public List<CoreStudioTableFieldBean> selectListByTable(@RequestParam(value="tableName") String var1);
}
