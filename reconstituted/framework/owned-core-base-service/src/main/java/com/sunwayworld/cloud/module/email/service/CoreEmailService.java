/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.email.service;

import com.sunwayworld.cloud.module.email.bean.CoreEmailBean;
import com.sunwayworld.cloud.module.email.bean.CoreEmailFileBean;
import com.sunwayworld.cloud.module.email.bean.CoreEmailReceipentBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import java.util.List;
import java.util.Map;

public interface CoreEmailService
extends GenericService<CoreEmailBean, Long> {
    public void insert(String var1, List<String> var2, Map<String, Object> var3, String ... var4);

    public void insert(String var1, Map<String, Object> var2, List<CoreEmailReceipentBean> var3, List<Long> var4);

    public void startEmailJob();

    public void sendEmail(CoreEmailBean var1, CoreEmailReceipentBean var2);

    public void sendEmail(CoreEmailBean var1, CoreEmailReceipentBean var2, List<CoreEmailReceipentBean> var3);

    public void sendEmail(CoreEmailBean var1, CoreEmailReceipentBean var2, List<CoreEmailReceipentBean> var3, List<CoreEmailFileBean> var4);

    public void sendEmailForList(CoreEmailBean var1, List<CoreEmailReceipentBean> var2, List<CoreEmailReceipentBean> var3, List<CoreEmailFileBean> var4);
}
