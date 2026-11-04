/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.email.resource.impl;

import com.sunwayworld.cloud.module.email.bean.CoreEmailConfigBean;
import com.sunwayworld.cloud.module.email.bean.CoreEmailConfigCcRoleBean;
import com.sunwayworld.cloud.module.email.bean.CoreEmailConfigCcUserBean;
import com.sunwayworld.cloud.module.email.bean.CoreEmailConfigRecRoleBean;
import com.sunwayworld.cloud.module.email.bean.CoreEmailConfigReceipentBean;
import com.sunwayworld.cloud.module.email.resource.CoreEmailConfigResource;
import com.sunwayworld.cloud.module.email.service.CoreEmailConfigCcRoleService;
import com.sunwayworld.cloud.module.email.service.CoreEmailConfigCcUserService;
import com.sunwayworld.cloud.module.email.service.CoreEmailConfigRecRoleService;
import com.sunwayworld.cloud.module.email.service.CoreEmailConfigReceipentService;
import com.sunwayworld.cloud.module.email.service.CoreEmailConfigService;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import com.sunwayworld.framework.support.suspendable.resource.AbstractGenericSuspendableResource;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@LogModule(value="\u90ae\u4ef6\u914d\u7f6e")
@GikamBean
@RequestMapping(value={"/secure/core/module/item/email-configs"})
public class CoreEmailConfigResourceImpl
implements CoreEmailConfigResource,
AbstractGenericResource<CoreEmailConfigService, CoreEmailConfigBean, String>,
AbstractGenericSuspendableResource<CoreEmailConfigService, CoreEmailConfigBean, String> {
    @Autowired
    private CoreEmailConfigService configService;
    @Autowired
    private CoreEmailConfigReceipentService configReceipentService;
    @Autowired
    private CoreEmailConfigRecRoleService configRecRoleService;
    @Autowired
    private CoreEmailConfigCcUserService configCcUserService;
    @Autowired
    private CoreEmailConfigCcRoleService configCcRoleService;

    @Override
    public CoreEmailConfigService getService() {
        return this.configService;
    }

    @Override
    @Log(value="\u65b0\u589e\u90ae\u4ef6\u914d\u7f6e\u63a5\u6536\u4eba", type=LogType.INSERT)
    public void insertReceiptent(@PathVariable String id, RestJsonWrapperBean wrapper) {
        this.getService().insertReceiptent(id, wrapper);
    }

    @Override
    @Log(value="\u5220\u9664\u90ae\u4ef6\u914d\u7f6e\u63a5\u6536\u4eba", type=LogType.DELETE)
    public void deleteReceiptent(@PathVariable String id, RestJsonWrapperBean wrapper) {
        this.getService().deleteReceiptent(id, wrapper);
    }

    @Override
    @Log(value="\u65b0\u589e\u90ae\u4ef6\u914d\u7f6e\u89d2\u8272", type=LogType.INSERT)
    public void insertRole(@PathVariable String id, RestJsonWrapperBean wrapper) {
        this.getService().insertRole(id, wrapper);
    }

    @Override
    @Log(value="\u5220\u9664\u90ae\u4ef6\u914d\u7f6e\u89d2\u8272", type=LogType.DELETE)
    public void deleteRole(@PathVariable String id, RestJsonWrapperBean wrapper) {
        this.getService().deleteRole(id, wrapper);
    }

    @Override
    @Log(value="\u67e5\u8be2\u90ae\u4ef6\u914d\u7f6e\u7684\u63a5\u6536\u4eba\u4fe1\u606f", type=LogType.SELECT)
    public Pagination<CoreEmailConfigReceipentBean> selectReceipentByConfig(@PathVariable String id, RestJsonWrapperBean wrapper) {
        SearchFilter filter = SearchFilter.instance().match("CONFIGID", id).filter(MatchPattern.EQ);
        return (Pagination)this.configReceipentService.selectPaginationByFilter(filter, wrapper);
    }

    @Override
    @Log(value="\u67e5\u8be2\u90ae\u4ef6\u914d\u7f6e\u7684\u89d2\u8272\u4fe1\u606f", type=LogType.SELECT)
    public Pagination<CoreEmailConfigRecRoleBean> selectRoleByConfig(@PathVariable String id, RestJsonWrapperBean wrapper) {
        return (Pagination)this.configRecRoleService.selectPaginationByFilter(SearchFilter.instance().match("CONFIGID", id).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    @Log(value="\u65b0\u589e\u90ae\u4ef6\u914d\u7f6e\u6284\u9001\u4eba", type=LogType.INSERT)
    public List<Long> insertCcUser(@PathVariable String id, RestJsonWrapperBean wrapper) {
        return this.getService().insertCcUser(id, wrapper);
    }

    @Override
    @Log(value="\u5220\u9664\u90ae\u4ef6\u914d\u7f6e\u6284\u9001\u4eba", type=LogType.DELETE)
    public void deleteCcUser(@PathVariable String id, RestJsonWrapperBean wrapper) {
        this.getService().deleteCcUser(id, wrapper);
    }

    @Override
    @Log(value="\u67e5\u8be2\u90ae\u4ef6\u914d\u7f6e\u6284\u9001\u4eba\u4fe1\u606f", type=LogType.INSERT)
    public Pagination<CoreEmailConfigCcUserBean> selectCcUserByConfig(String id, RestJsonWrapperBean wrapper) {
        SearchFilter filter = SearchFilter.instance().match("CONFIGID", id).filter(MatchPattern.EQ);
        return (Pagination)this.configCcUserService.selectPaginationByFilter(filter, wrapper);
    }

    @Override
    @Log(value="\u65b0\u589e\u90ae\u4ef6\u914d\u7f6e\u6284\u9001\u89d2\u8272", type=LogType.INSERT)
    public List<Long> insertCcRole(@PathVariable String id, RestJsonWrapperBean wrapper) {
        return this.getService().insertCcRole(id, wrapper);
    }

    @Override
    @Log(value="\u5220\u9664\u90ae\u4ef6\u914d\u7f6e\u6284\u9001\u89d2\u8272", type=LogType.DELETE)
    public void deleteCcRole(@PathVariable String id, RestJsonWrapperBean wrapper) {
        this.getService().deleteCcRole(id, wrapper);
    }

    @Override
    @Log(value="\u67e5\u8be2\u90ae\u4ef6\u914d\u7f6e\u6284\u9001\u4eba\u89d2\u8272\u4fe1\u606f", type=LogType.INSERT)
    public Pagination<CoreEmailConfigCcRoleBean> selectCcRoleByConfig(String id, RestJsonWrapperBean wrapper) {
        SearchFilter filter = SearchFilter.instance().match("CONFIGID", id).filter(MatchPattern.EQ);
        return (Pagination)this.configCcRoleService.selectPaginationByFilter(filter, wrapper);
    }
}
