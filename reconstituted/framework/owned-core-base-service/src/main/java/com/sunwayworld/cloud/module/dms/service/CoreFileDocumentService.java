/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.dms.service;

import com.sunwayworld.cloud.module.dms.bean.CoreFileDocumentBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.data.page.PageRequest;
import org.springframework.lang.Nullable;

public interface CoreFileDocumentService {
    public Long insertByFile(Long var1, @Nullable String var2);

    public Long updateByFile(Long var1, @Nullable String var2);

    public void deleteByFile(Long var1);

    public Page<CoreFileDocumentBean> selectPagination(PageRequest var1, @Nullable String var2, String var3);
}
