/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.admin.script.resource;

import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.module.admin.script.bean.CoreAdminScriptResultDTO;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping(value={"/secure/core/module/admin/scripts"})
public interface CoreAdminScriptResource {
    @RequestMapping(value={"/action/execute"}, method={RequestMethod.POST})
    public CoreAdminScriptResultDTO execute(RestJsonWrapperBean var1);
}
