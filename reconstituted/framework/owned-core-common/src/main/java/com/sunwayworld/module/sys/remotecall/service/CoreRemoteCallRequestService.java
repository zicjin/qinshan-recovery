/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.remotecall.service;

import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.module.sys.remotecall.bean.CoreRemoteCallRequestBean;
import com.sunwayworld.module.sys.remotecall.bean.CoreRemoteCallResponseBean;

public interface CoreRemoteCallRequestService
extends GenericService<CoreRemoteCallRequestBean, Long> {
    public Long insert(String var1, String var2);

    public Long insertInNewTransaction(String var1, String var2);

    public void processAsyncRequest(String var1, String var2);

    public <T> void processAsyncRequest(String var1, T var2);

    public String processSyncRequest(String var1, String var2);

    public <T, R> R processSyncRequest(String var1, T var2, Class<R> var3);

    public void processAsyncRequest(CoreRemoteCallRequestBean var1);

    public void record(CoreRemoteCallRequestBean var1, CoreRemoteCallResponseBean var2);

    public void reset(RestJsonWrapperBean var1);
}
