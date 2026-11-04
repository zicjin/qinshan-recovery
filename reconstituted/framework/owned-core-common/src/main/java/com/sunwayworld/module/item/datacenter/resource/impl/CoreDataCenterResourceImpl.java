/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.datacenter.resource.impl;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.item.datacenter.bean.CoreDataCenterBean;
import com.sunwayworld.module.item.datacenter.bean.CoreDataCenterDTO;
import com.sunwayworld.module.item.datacenter.bean.CoreDataCenterParamBean;
import com.sunwayworld.module.item.datacenter.bean.CoreDataCenterRoleBean;
import com.sunwayworld.module.item.datacenter.bean.CoreDataCenterUserBean;
import com.sunwayworld.module.item.datacenter.resource.CoreDataCenterResource;
import com.sunwayworld.module.item.datacenter.service.CoreDataCenterService;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u6570\u636e\u4e2d\u5fc3")
@RestController
@GikamBean
public class CoreDataCenterResourceImpl
implements CoreDataCenterResource {
    @Autowired
    private CoreDataCenterService dataCenterService;

    @Override
    public CoreDataCenterService getService() {
        return this.dataCenterService;
    }

    @Override
    @Log(value="\u62d6\u52a8\u6570\u636e\u4e2d\u5fc3", type=LogType.UPDATE)
    @RequestMapping(value={"/action/drag"}, method={RequestMethod.PUT})
    public void drag(RestJsonWrapperBean wrapper) {
        this.getService().drag(wrapper);
    }

    @Override
    @Log(value="\u6839\u636e\u6570\u636e\u4e2d\u5fc3ID\u67e5\u8be2\u8bbf\u95ee\u89d2\u8272", type=LogType.SELECT)
    @RequestMapping(value={"/{id}/roles/queries"}, method={RequestMethod.POST})
    public Page<CoreDataCenterRoleBean> selectRolePaginationByDataCenterId(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return this.getService().selectRolePaginationByDataCenterId(id, wrapper);
    }

    @Override
    @Log(value="\u65b0\u589e\u6570\u636e\u4e2d\u5fc3\u8bbf\u95ee\u89d2\u8272", type=LogType.INSERT)
    @RequestMapping(value={"/{id}/roles"}, method={RequestMethod.POST})
    public Long insertRole(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return this.getService().insertRole(id, wrapper);
    }

    @Override
    @Log(value="\u5220\u9664\u6570\u636e\u4e2d\u5fc3\u8bbf\u95ee\u89d2\u8272", type=LogType.DELETE)
    @RequestMapping(value={"/{id}/roles"}, method={RequestMethod.DELETE})
    public void deleteRole(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        this.getService().deleteRole(id, wrapper);
    }

    @Override
    @Log(value="\u540c\u6b65\u6570\u636e\u4e2d\u5fc3\u6570\u636e", type=LogType.UPDATE)
    @RequestMapping(value={"/action/sync"}, method={RequestMethod.PUT})
    public void sync(RestJsonWrapperBean wrapper) {
        this.getService().sync(wrapper);
    }

    @Override
    @Log(value="\u83b7\u53d6\u5f53\u524d\u767b\u5f55\u4eba\u6743\u9650\u5185\u5927\u5c4f", type=LogType.SELECT)
    @RequestMapping(value={"/queries/index"}, method={RequestMethod.GET})
    public List<CoreDataCenterBean> selectCurrentUserDataCenter() {
        return this.getService().selectCurrentUserDataCenter();
    }

    @Override
    @Log(value="\u67e5\u8be2\u6570\u636e\u4e2d\u5fc3\u62a5\u8868\u6811", type=LogType.SELECT)
    @RequestMapping(value={"/reports/tree-grid"}, method={RequestMethod.POST})
    public Page<CoreDataCenterDTO> selectReportTree(RestJsonWrapperBean wrapper) {
        return this.getService().selectReportTree(wrapper);
    }

    @Override
    @Log(value="\u65b0\u589e\u6570\u636e\u4e2d\u5fc3\u53c2\u6570\u914d\u7f6e", type=LogType.INSERT)
    @RequestMapping(value={"/{id}/params"}, method={RequestMethod.POST})
    public Long insertParam(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return this.getService().insertParam(id, wrapper);
    }

    @Override
    @Log(value="\u5220\u9664\u6570\u636e\u4e2d\u5fc3\u53c2\u6570\u914d\u7f6e", type=LogType.DELETE)
    @RequestMapping(value={"/{id}/params"}, method={RequestMethod.DELETE})
    public void deleteParam(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        this.getService().deleteParam(id, wrapper);
    }

    @Override
    @Log(value="\u6839\u636e\u6570\u636e\u4e2d\u5fc3ID\u67e5\u8be2\u53c2\u6570\u914d\u7f6e", type=LogType.SELECT)
    @RequestMapping(value={"/{id}/params/queries"}, method={RequestMethod.POST})
    public Page<CoreDataCenterParamBean> selectParamPaginationByDataCenterId(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return this.getService().selectParamPaginationByDataCenterId(id, wrapper);
    }

    @Override
    @Log(value="\u4e2a\u4eba\u6570\u636e\u4e2d\u5fc3\u914d\u7f6e\u67e5\u8be2", type=LogType.SELECT)
    @RequestMapping(value={"/queries/current-user"}, method={RequestMethod.POST})
    public Page<CoreDataCenterUserBean> selectCurrentUserPagination(RestJsonWrapperBean wrapper) {
        return this.getService().selectCurrentUserPagination(wrapper);
    }

    @Override
    @Log(value="\u4e2a\u4eba\u6570\u636e\u4e2d\u5fc3\u914d\u7f6e\u6570\u636e\u540c\u6b65", type=LogType.INSERT)
    @RequestMapping(value={"/action/sync-user-data-center"}, method={RequestMethod.POST})
    public void syncUserDataCenterData(RestJsonWrapperBean wrapper) {
        this.getService().syncUserDataCenterData(wrapper);
    }

    @Override
    @Log(value="\u4e2a\u4eba\u6570\u636e\u4e2d\u5fc3\u4e92\u6362\u5bf9\u5e94\u7684\u503c", type=LogType.UPDATE)
    @RequestMapping(value={"/users/action/swap/{column}/{firstId}/{secondId}", "/users/action/swap/{firstId}/{secondId}"}, method={RequestMethod.PUT})
    public void swapUser(@PathVariable(required=false) String column, @PathVariable Long firstId, @PathVariable Long secondId) {
        this.getService().swapUser(Objects.toString(column, "ORDERNO"), firstId, secondId);
    }
}
