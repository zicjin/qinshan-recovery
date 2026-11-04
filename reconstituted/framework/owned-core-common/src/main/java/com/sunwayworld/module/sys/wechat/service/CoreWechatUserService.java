/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.wechat.service;

import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.module.sys.wechat.bean.CoreWechatUserBean;

public interface CoreWechatUserService
extends GenericService<CoreWechatUserBean, String> {
    public void insert(String var1, String var2);
}
