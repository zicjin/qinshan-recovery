/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.email.service;

import com.sunwayworld.cloud.module.email.bean.CoreEmailConfigBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.suspendable.service.GenericSuspendableService;
import java.util.List;

public interface CoreEmailConfigService
extends GenericService<CoreEmailConfigBean, String>,
GenericSuspendableService<CoreEmailConfigBean, String> {
    public void insertReceiptent(String var1, RestJsonWrapperBean var2);

    public void deleteReceiptent(String var1, RestJsonWrapperBean var2);

    public void insertRole(String var1, RestJsonWrapperBean var2);

    public void deleteRole(String var1, RestJsonWrapperBean var2);

    public List<Long> insertCcUser(String var1, RestJsonWrapperBean var2);

    public void deleteCcUser(String var1, RestJsonWrapperBean var2);

    public List<Long> insertCcRole(String var1, RestJsonWrapperBean var2);

    public void deleteCcRole(String var1, RestJsonWrapperBean var2);
}
