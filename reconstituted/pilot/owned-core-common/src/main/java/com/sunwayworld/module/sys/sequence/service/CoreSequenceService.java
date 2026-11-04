/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.sequence.service;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.module.sys.sequence.bean.CoreSequenceBean;
import com.sunwayworld.module.sys.sequence.bean.CoreSequenceLineBean;
import java.util.List;
import java.util.function.Supplier;

public interface CoreSequenceService
extends GenericService<CoreSequenceBean, String> {
    public List<String> getNextSequenceList(String var1, String var2, int var3);

    public List<Long> getNextIdentityList(String var1, int var2);

    public List<String> getNextSequenceList(String var1, Supplier<String> var2, int var3, int var4);

    public Page<CoreSequenceLineBean> selectLinePaginationBySequenceId(String var1, RestJsonWrapperBean var2);

    public Long insertLine(String var1, RestJsonWrapperBean var2);

    public void deleteLine(String var1, RestJsonWrapperBean var2);

    public void buildSequencePattern(String var1);

    public void swapLine(String var1, Long var2, Long var3);
}
