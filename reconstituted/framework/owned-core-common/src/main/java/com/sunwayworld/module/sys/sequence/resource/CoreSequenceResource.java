/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.sequence.resource;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.module.sys.sequence.bean.CoreSequenceBean;
import com.sunwayworld.module.sys.sequence.bean.CoreSequenceLineBean;
import com.sunwayworld.module.sys.sequence.service.CoreSequenceService;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/module/sys/sequences"})
public interface CoreSequenceResource
extends GenericResource<CoreSequenceService, CoreSequenceBean, String> {
    public Page<CoreSequenceLineBean> selectLinePaginationBySequenceId(String var1, RestJsonWrapperBean var2);

    public Long insertLine(String var1, RestJsonWrapperBean var2);

    public void deleteLine(String var1, RestJsonWrapperBean var2);

    public void swapLine(Long var1, Long var2);
}
