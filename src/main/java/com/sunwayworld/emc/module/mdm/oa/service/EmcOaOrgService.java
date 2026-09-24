// 2026-09-24 从生产字节码反编译恢复（CFR 0.152）：生产新增类，历史源码缺失
package com.sunwayworld.emc.module.mdm.oa.service;

import com.sunwayworld.emc.module.mdm.oa.bean.EmcOaOrgBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

public interface EmcOaOrgService
extends GenericService<EmcOaOrgBean, Long> {
    public void syncOaUserOrg();

    public void syncOaClientArea();

    public void syncOaClientAreaWithParam(RestJsonWrapperBean var1);

    public void syncEmcClientSchedule();

    public void syncOaEquipt();

    public void syncOaNewClient(String var1);

    public void syncEmcClient(String var1);
}
