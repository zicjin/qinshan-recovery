/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.checkcode.resource;

import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.module.sys.checkcode.bean.CoreCheckCodeBean;
import com.sunwayworld.module.sys.checkcode.service.CoreCheckCodeService;
import java.util.List;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/module/sys/check-codes"})
public interface CoreCheckCodeResource
extends GenericResource<CoreCheckCodeService, CoreCheckCodeBean, Long> {
    public List<CoreCheckCodeBean> selectCheckableList();

    public List<String> selectCategoryList();
}
