/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.log.service;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.module.sys.log.bean.CoreLoginLogBean;
import java.util.List;
import org.springframework.lang.Nullable;

public interface CoreLoginLogService
extends GenericService<CoreLoginLogBean, Long> {
    public void insertLoginSuccessLog(String var1, String var2, String var3);

    public void insertLoginFailureLog(String var1, String var2);

    public void assignLogout(String var1, String var2);

    public List<CoreLoginLogBean> selectOnlineUserLoginLogList(@Nullable String var1);

    public CoreLoginLogBean getCurrentLoginLog(String var1);

    public Page<CoreLoginLogBean> selectOnlinePagination(RestJsonWrapperBean var1);

    public void offline(RestJsonWrapperBean var1);
}
