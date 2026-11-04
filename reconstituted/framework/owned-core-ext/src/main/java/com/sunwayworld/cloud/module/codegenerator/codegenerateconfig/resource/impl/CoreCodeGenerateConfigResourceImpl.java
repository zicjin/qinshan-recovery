/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.codegenerator.codegenerateconfig.resource.impl;

import com.sunwayworld.cloud.module.codegenerator.codegenerateconfig.bean.CoreCodeGenerateConfigBean;
import com.sunwayworld.cloud.module.codegenerator.codegenerateconfig.bean.CoreCodeGenerateConfigFieldBean;
import com.sunwayworld.cloud.module.codegenerator.codegenerateconfig.bean.CoreCodeGenerateConfigTableBean;
import com.sunwayworld.cloud.module.codegenerator.codegenerateconfig.resource.CoreCodeGenerateConfigResource;
import com.sunwayworld.cloud.module.codegenerator.codegenerateconfig.service.CoreCodeGenerateConfigService;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@GikamBean
public class CoreCodeGenerateConfigResourceImpl
implements CoreCodeGenerateConfigResource,
AbstractGenericResource<CoreCodeGenerateConfigService, CoreCodeGenerateConfigBean, String> {
    @Autowired
    private CoreCodeGenerateConfigService configService;

    @Override
    public CoreCodeGenerateConfigService getService() {
        return this.configService;
    }

    @Override
    @Log(value="\u65b0\u589e\u4ee3\u7801\u751f\u6210\u5668\u914d\u7f6e", type=LogType.REST_CONTROLLER)
    public String insert(RestJsonWrapperBean wrapper) {
        return (String)this.getService().insert(wrapper);
    }

    @Override
    @Log(value="\u4ee3\u7801\u751f\u6210", type=LogType.REST_CONTROLLER)
    public String codeGenerate(@PathVariable String id, RestJsonWrapperBean wrapper) {
        return this.getService().codeGenerate(id, wrapper);
    }

    @Override
    @Log(value="\u590d\u5236TABLE", type=LogType.REST_CONTROLLER)
    public String copy(String id) {
        return this.getService().copy(id);
    }

    @Override
    @Log(value="\u65b0\u589eTABLE", type=LogType.REST_CONTROLLER)
    public Long insertTable(@PathVariable String id, RestJsonWrapperBean wrapper) {
        return this.getService().insertTable(id, wrapper);
    }

    @Override
    @Log(value="\u5220\u9664TABLE", type=LogType.REST_CONTROLLER)
    public void deleteTable(RestJsonWrapperBean wrapper) {
        this.getService().deleteTable(wrapper);
    }

    @Override
    @Log(value="\u67e5\u8be2TABLE", type=LogType.REST_CONTROLLER)
    public Pagination<CoreCodeGenerateConfigTableBean> selectTablePaginationByConfigId(@PathVariable String id, RestJsonWrapperBean wrapper) {
        return (Pagination)this.getService().selectTablePaginationByConfigId(id, wrapper);
    }

    @Override
    @Log(value="\u65b0\u589e\u5b57\u6bb5", type=LogType.REST_CONTROLLER)
    public Long insertField(RestJsonWrapperBean wrapper) {
        return this.getService().insertField(wrapper);
    }

    @Override
    @Log(value="\u5220\u9664\u5b57\u6bb5", type=LogType.REST_CONTROLLER)
    public void deleteField(RestJsonWrapperBean wrapper) {
        this.getService().deleteField(wrapper);
    }

    @Override
    @Log(value="\u67e5\u8be2\u5b57\u6bb5", type=LogType.REST_CONTROLLER)
    public Pagination<CoreCodeGenerateConfigFieldBean> selectFieldPagination(RestJsonWrapperBean wrapper) {
        return (Pagination)this.getService().selectFieldPagination(wrapper);
    }

    @Override
    @Log(value="\u4e92\u6362\u5b57\u6bb5\u4fe1\u606f\u6392\u5e8f\u7801\u7684\u503c", type=LogType.UPDATE)
    public void swapField(@PathVariable Long firstId, @PathVariable Long secondId) {
        this.getService().swapField(firstId, secondId);
    }
}
