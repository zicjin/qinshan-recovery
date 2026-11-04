/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.document.service;

import com.sunwayworld.cloud.module.item.document.bean.CoreDocumentBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import java.util.List;

public interface CoreDocumentService {
    public void save(RestJsonWrapperBean var1);

    public void update(List<CoreDocumentBean> var1);

    public CoreDocumentBean selectOne(Long var1);

    public CoreDocumentBean selectOne(String var1, String var2, String var3);

    public <ID> void insert(String var1, ID var2, String var3);

    public <ID> void insert(String var1, ID var2, String var3, String var4);

    public <ID> void save(String var1, ID var2, String var3, String var4);

    public void batchDeleteByTargetId(String ... var1);

    public <ID> void delete(String var1, ID var2, String var3);

    public <ID> void deleteAsync(String var1, ID var2);

    public List<CoreDocumentBean> selectListByTargetId(List<String> var1);
}
