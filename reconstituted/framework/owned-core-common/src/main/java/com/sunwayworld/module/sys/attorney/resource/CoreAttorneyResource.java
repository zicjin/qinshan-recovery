/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.attorney.resource;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.activatable.resource.GenericActivatableResource;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.framework.support.suspendable.resource.GenericSuspendableResource;
import com.sunwayworld.module.sys.attorney.bean.CoreAttorneyBean;
import com.sunwayworld.module.sys.attorney.bean.CoreAttorneyDTO;
import com.sunwayworld.module.sys.attorney.bean.CoreAttorneyMenuBean;
import com.sunwayworld.module.sys.attorney.service.CoreAttorneyService;
import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping(value={"/secure/core/module/sys/attorneys"})
public interface CoreAttorneyResource
extends GenericResource<CoreAttorneyService, CoreAttorneyBean, Long>,
GenericActivatableResource<CoreAttorneyService, CoreAttorneyBean, Long>,
GenericSuspendableResource<CoreAttorneyService, CoreAttorneyBean, Long> {
    @Log(value="\u67e5\u8be2\u59d4\u6258\u4ee3\u7406\u7684\u83dc\u5355\u4fe1\u606f", type=LogType.SELECT)
    @RequestMapping(value={"/{id}/menus/queries"}, method={RequestMethod.POST})
    public Page<CoreAttorneyMenuBean> selectAttorneyMenuPagination(@PathVariable Long var1, RestJsonWrapperBean var2);

    @Override
    @Log(value="\u65b0\u589e\u59d4\u6258\u4ee3\u7406", type=LogType.INSERT)
    @RequestMapping(method={RequestMethod.POST})
    public Long insert(RestJsonWrapperBean var1);

    @Log(value="\u67e5\u8be2\u59d4\u6258\u4ee3\u7406\u7684\u83dc\u5355\u4fe1\u606f", type=LogType.INSERT)
    @RequestMapping(value={"/{id}/menus"}, method={RequestMethod.POST})
    public void insertAttorneyMenus(@PathVariable Long var1, RestJsonWrapperBean var2);

    @Log(value="\u5220\u9664\u59d4\u6258\u4ee3\u7406\u7684\u83dc\u5355\u4fe1\u606f", type=LogType.DELETE)
    @RequestMapping(value={"/{id}/menus"}, method={RequestMethod.DELETE})
    public void deleteAttorneyMenus(@PathVariable Long var1, RestJsonWrapperBean var2);

    @Log(value="\u53d7\u6258\u4eba\u5207\u6362\u5230\u59d4\u6258\u4eba", type=LogType.INSERT)
    @RequestMapping(value={"/action/switch-trustor/{trustorId}"}, method={RequestMethod.GET})
    public void switchTrustor(@PathVariable(value="trustorId") String var1);

    @Log(value="\u53d7\u6258\u4eba\u5207\u6362\u5230\u59d4\u6258\u4eba", type=LogType.INSERT)
    @RequestMapping(value={"/action/switch-trustee"}, method={RequestMethod.GET})
    public void switchTrustee();

    @Log(value="\u67e5\u8be2\u6267\u884c\u4e2d\u7684\u59d4\u6258\u4eba\u5f85\u529e\u4fe1\u606f", type=LogType.SELECT)
    @RequestMapping(value={"/todo"}, method={RequestMethod.POST})
    public List<CoreAttorneyDTO> selectAttorneyTodoList();
}
